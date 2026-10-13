from firebase_admin import auth
from database import Session, get_db
from fastapi import HTTPException, Depends
from fastapi.security import OAuth2PasswordBearer
from sqlmodel import select
from models import Account

firebase_scheme = OAuth2PasswordBearer(tokenUrl="")

def get_firebase_uid(token: str = Depends(firebase_scheme)):
    try:
        decoded_token = auth.verify_id_token(token)
        return decoded_token["uid"]
    except Exception as e:
        print(f"Hata meydana geldi -> {e}")
        raise HTTPException(status_code=401, detail="Oturum bulunamadı")

def get_current_user_id(firebase_uid: str = Depends(get_firebase_uid), db: Session = Depends(get_db)):
    account = db.exec(select(Account).where(Account.firebase_uid==firebase_uid)).first()
    if not account:
        raise HTTPException(status_code=404, detail="Kullanıcı bulunamadı")
    return account.id