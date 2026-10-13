from fastapi import HTTPException
from datetime import datetime, timezone, timedelta
from models import Account, Snapshot, Summary
from schemas import AccountOp, BaseSnapshot, GetSnapshot, GetSummary
from sqlmodel import Session, select, delete
from decimal import Decimal
import requests

def ensure_account(account: AccountOp, firebase_uid: str, db: Session):
    account_db = db.exec(select(Account).where(Account.firebase_uid==firebase_uid)).one_or_none()
    if account_db:
        account_db.last_login_at = datetime.now(timezone.utc)
        db.add(account_db)
        db.commit()
        return account_db.id
    new_account = Account(firebase_uid=firebase_uid, email=account.email, display_name=account.display_name, phone_number=account.phone_number, photo_url=account.photo_url, last_login_at=datetime.now(timezone.utc))
    db.add(new_account)
    # Hata durumları global exception altında tanımlanacak
    db.commit()
    db.refresh(new_account)
    return new_account.id

def add_snapshot(snapshot: BaseSnapshot, user_id: int, db: Session):
    new_snapshot = Snapshot(user_id=user_id, gram_buy=snapshot.gram_buy, gram_sell=snapshot.gram_sell, quarter_buy=snapshot.quarter_buy, quarter_sell=snapshot.quarter_sell, half_buy=snapshot.half_buy, half_sell=snapshot.half_sell, full_buy=snapshot.full_buy, full_sell=snapshot.full_sell)
    db.add(new_snapshot)
    db.commit()
    db.refresh(new_snapshot)
    return new_snapshot # new_snapshot.id

def get_snapshots(user_id: int, db: Session):
    snapshots = db.exec(select(Snapshot).where(Snapshot.user_id==user_id)).fetchall()
    return GetSnapshot(snapshots=snapshots[::-1])

def add_summary(db: Session): # Geri döndürülecek değer 24 saate kalan süre olabilir
    last_summary = db.exec(select(Summary).order_by(Summary.created_at.desc())).first()
    previous_close = None
    if last_summary:
        days_diff = (datetime.now(timezone.utc) - last_summary.created_at).days # limit
        if days_diff == 0:
            next_summary = last_summary.created_at + timedelta(days=1)
            remaining = (next_summary - datetime.now(timezone.utc))
            return int(remaining.total_seconds())
        days_diff += 1
    else:
        days_diff = 2
    troy_ons = Decimal(31.1034768)
    fineness = Decimal(0.9166)
    print("days_diff =", days_diff, flush=True)
    response = requests.get("https://data-api.binance.vision/api/v3/klines", params={"symbol": "XAUTTRY", "interval": "1d", "limit": days_diff + 2})
    print("Binance status =", response.status_code, flush=True)
    print("Binance text =", response.text[:1000], flush=True)
    klines = response.json()
    print("klines type =", type(klines), flush=True)
    if last_summary:
        previous_close = Decimal(klines[0][4]) / troy_ons
        klines.pop(0)
    klines.pop()
    for kline in klines:
        # start_time = kline[0]
        high_price = Decimal(kline[2]) / troy_ons
        low_price = Decimal(kline[3]) / troy_ons
        close_price = Decimal(kline[4]) / troy_ons
        close_time = kline[6]
        resp = requests.get("https://data-api.binance.vision/api/v3/aggTrades", params={"symbol": "XAUTTRY", "endTime": close_time, "limit": 1000})
        trades = resp.json()
        close_buy, close_sell = None, None
        for trade in reversed(trades):
            if trade["m"] and close_sell is None:
                close_sell = Decimal(trade["p"]) / troy_ons
            elif not trade["m"] and close_buy is None:
                close_buy = Decimal(trade["p"]) / troy_ons
            if close_sell is not None and close_buy is not None:
                break
        if previous_close is not None:
            change_percentage = ((close_price - previous_close) / previous_close) * Decimal(100)
        else:
            change_percentage = Decimal("0.00")
        previous_close = close_price
        summary_date = datetime.fromtimestamp(close_time / 1000, timezone.utc)
        new_summary = Summary(gram_high=high_price, gram_low=low_price, gram_close_buy=close_buy, gram_close_sell=close_sell, quarter_high=high_price * Decimal("1.754") * fineness, quarter_low=low_price * Decimal("1.754") * fineness, quarter_close_buy=close_buy * Decimal("1.754") * fineness, quarter_close_sell=close_sell * Decimal("1.754") * fineness, half_high=high_price * Decimal("3.508") * fineness, half_low=low_price * Decimal("3.508") * fineness, half_close_buy=close_buy * Decimal("3.508") * fineness, half_close_sell=close_sell * Decimal("3.508") * fineness, full_high=high_price * Decimal("7.016") * fineness, full_low=low_price * Decimal("7.016") * fineness, full_close_buy=close_buy * Decimal("7.016") * fineness, full_close_sell=close_sell * Decimal("7.016") * fineness, change_rate=change_percentage, created_at=summary_date)
        db.add(new_summary)
    db.commit()
    # db.refresh(new_summary)

def get_summaries(db: Session):
    summaries = db.exec(select(Summary)).all()
    return GetSummary(summaries=summaries[::-1])

def cron_add_summary(db: Session):
    last_summary = db.exec(select(Summary).order_by(Summary.created_at.desc())).first()
    previous_close = None
    if last_summary:
        now = datetime.now(timezone.utc)
        if last_summary.created_at.date() == now.date():
            return
        days_diff = (now.date() - last_summary.created_at.date()).days
    else:
        days_diff = 2
    troy_ons = Decimal(31.1034768)
    fineness = Decimal(0.9166)
    print("days_diff =", days_diff, flush=True)
    response = requests.get("https://data-api.binance.vision/api/v3/klines", params={"symbol": "XAUTTRY", "interval": "1d", "limit": days_diff + 1})
    print("Binance status =", response.status_code, flush=True)
    print("Binance text =", response.text[:1000], flush=True)
    klines = response.json()
    print("klines type =", type(klines), flush=True)
    if last_summary:
        previous_close = Decimal(klines[0][4]) / troy_ons
        klines.pop(0)
    #klines.pop()
    print("İŞLENECEK TARİHLER:", flush=True)

    for kline in klines:
        print(
            datetime.fromtimestamp(kline[0] / 1000, timezone.utc),
            "->",
            datetime.fromtimestamp(kline[6] / 1000, timezone.utc),
            flush=True
        )
        # start_time = kline[0]
        high_price = Decimal(kline[2]) / troy_ons
        low_price = Decimal(kline[3]) / troy_ons
        close_price = Decimal(kline[4]) / troy_ons
        close_time = kline[6]
        resp = requests.get("https://data-api.binance.vision/api/v3/aggTrades", params={"symbol": "XAUTTRY", "endTime": close_time, "limit": 1000})
        trades = resp.json()
        close_buy, close_sell = None, None
        for trade in reversed(trades):
            if trade["m"] and close_sell is None:
                close_sell = Decimal(trade["p"]) / troy_ons
            elif not trade["m"] and close_buy is None:
                close_buy = Decimal(trade["p"]) / troy_ons
            if close_sell is not None and close_buy is not None:
                break
        if previous_close is not None:
            change_percentage = ((close_price - previous_close) / previous_close) * Decimal(100)
        else:
            change_percentage = Decimal("0.00")
        previous_close = close_price
        summary_date = datetime.fromtimestamp(close_time / 1000, timezone.utc)
        new_summary = Summary(gram_high=high_price, gram_low=low_price, gram_close_buy=close_buy, gram_close_sell=close_sell, quarter_high=high_price * Decimal("1.754") * fineness, quarter_low=low_price * Decimal("1.754") * fineness, quarter_close_buy=close_buy * Decimal("1.754") * fineness, quarter_close_sell=close_sell * Decimal("1.754") * fineness, half_high=high_price * Decimal("3.508") * fineness, half_low=low_price * Decimal("3.508") * fineness, half_close_buy=close_buy * Decimal("3.508") * fineness, half_close_sell=close_sell * Decimal("3.508") * fineness, full_high=high_price * Decimal("7.016") * fineness, full_low=low_price * Decimal("7.016") * fineness, full_close_buy=close_buy * Decimal("7.016") * fineness, full_close_sell=close_sell * Decimal("7.016") * fineness, change_rate=change_percentage, created_at=summary_date)
        db.add(new_summary)
    db.commit()