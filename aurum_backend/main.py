import firebase_admin
from firebase_admin import credentials
from fastapi import FastAPI, Depends, HTTPException, Request
from typing import Annotated
from contextlib import asynccontextmanager
from schemas import *
from dependencies import get_firebase_uid, get_current_user_id
from database import Session, get_db, create_db_and_tables
from services import ensure_account, add_snapshot, get_snapshots, add_summary, get_summaries, cron_add_summary
# from models import Account, Snapshot
from schemas import AccountOp, BaseSnapshot, GetSnapshot, GetSummary
from slowapi import Limiter
from slowapi.util import get_remote_address
from slowapi.errors import RateLimitExceeded
from sqlalchemy.exc import OperationalError
from handle import custom_rate_limit_handler, postgres_operational_exception_handler, global_exception_handler
import asyncio
import requests
import os
from dotenv import load_dotenv
import base64
import json
# https://www.darphane.gov.tr/cumhuriyet-altini-uretimi
# https://github.com/firebase/snippets-android/blob/8ae2d231b20c3fa10e7654ead53cc933e3e21608/auth/app/src/main/java/com/google/firebase/quickstart/auth/kotlin/GoogleSignInActivity.kt#L42-L43
# https://developers.binance.com/en/docs/catalog/core-trading-spot-trading/api/rest-api/market#klines
load_dotenv()

DatabaseDep = Annotated[Session, Depends(get_db)]
UserIdDep = Annotated[int, Depends(get_current_user_id)]
FirebaseUidDep = Annotated[str, Depends(get_firebase_uid)]

@asynccontextmanager
async def lifespan(app: FastAPI):
    create_db_and_tables()
    # task = asyncio.create_task(daily_task())
    # try:
    #     yield
    # finally:
    #     task.cancel()
    yield

app = FastAPI(lifespan=lifespan, root_path="/api/v1")
limiter = Limiter(key_func=get_remote_address)
app.state.limiter = limiter
app.add_exception_handler(RateLimitExceeded, custom_rate_limit_handler)
app.add_exception_handler(OperationalError, postgres_operational_exception_handler)
app.add_exception_handler(Exception, global_exception_handler)

base64_creds = os.getenv("FIREBASE_BASE64")
decoded_bytes = base64.b64decode(base64_creds)
creds_dict = json.loads(decoded_bytes.decode("utf-8"))
cred = credentials.Certificate(creds_dict)
default_app = firebase_admin.initialize_app(cred)

# check_user("")

# Bu işlem websocket olmadan da yapılabilir
# async def listen():
#     url = "wss://stream.binance.com:9443/ws/xauttry@bookTicker"
#     print("Adrese bağlanılıyor...")
#     try:
#         async with websockets.connect(url) as websocket:
#             print("Bağlantı başarılı")
#             async for message in websocket:
#                 print(f"Gelen mesaj: {message}")
#     except:
#         print("Bir hata meydana geldi")
# asyncio.run(listen())

# Şemalar tanımlanacak ve veri tipi olarak hem app.py'ye hem de services.py'ye eklenecekler. Düz olarak veri tabanı modeli olmayacaklar ⭐

@app.get("/")
@limiter.limit("3/minute")
def root(request: Request):
    return "Hello World!"

# Register ve login beraber
@app.post("/auth/login")
@limiter.limit("5/minute")
def login(account: AccountOp, firebase_uid: FirebaseUidDep, db: DatabaseDep, request: Request):
    if "@" not in account.email:
        raise HTTPException(400, detail="Email'de @ işareti bulunmuyor")
    return ensure_account(account, firebase_uid, db)

@app.post("/snapshot", response_model=GetSnapshotAndId)
@limiter.limit("10/minute")
def snapshot(snapshot: BaseSnapshot, user_id: UserIdDep, db: DatabaseDep, request: Request):
    # user_id, değerler, tarih
    return add_snapshot(snapshot, user_id, db)

@app.get("/snapshot", response_model=GetSnapshot)
@limiter.limit("10/minute")
def snapshots(user_id: UserIdDep, db: DatabaseDep, request: Request):
    return get_snapshots(user_id, db)

@app.post("/summary/daily")
def daily_summary(db: DatabaseDep):
    return cron_add_summary(db) # add_summary(db)

@app.get("/summary/daily", response_model=GetSummary)
def daily_summaries(user_id: UserIdDep, db: DatabaseDep, request: Request):
    return get_summaries(db)

# async def daily_task():
#     sleep_time = 60 * 30 # 60 * 60 * 24 # 86400 saniye - 1 gün
#     try:
#         while True:
#             try:
#                 base_url = os.getenv("BASE_URL")
#                 wake_up_response = requests.get(f"{base_url}/")
#                 print("WAKE STATUS:", wake_up_response.status_code, flush=True)
#                 print("WAKE TEXT:", repr(wake_up_response.text), flush=True)
#                 response = requests.post(f"{base_url}/summary/daily")
#                 print("STATUS:", response.status_code)
#                 print("TEXT:", repr(response.text))
#                 remaining = response.json()
#                 if remaining is not None and response.ok:
#                     sleep_time = remaining
#             except requests.RequestException as e:
#                 print(f"Günlük görev isteği başarısız -> {e}")
#             await asyncio.sleep(sleep_time)
#     except asyncio.CancelledError as e:
#         print(f"Günlük görev sonlandırıldı -> {e}")