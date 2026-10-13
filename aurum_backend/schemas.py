from pydantic import BaseModel, ConfigDict
from decimal import Decimal
from datetime import datetime
from models import Snapshot, Summary

class AccountOp(BaseModel):
    email: str
    display_name: str
    phone_number: str | None = None
    photo_url: str

class BaseSnapshot(BaseModel):
    gram_buy: Decimal
    gram_sell: Decimal
    quarter_buy: Decimal
    quarter_sell: Decimal
    half_buy: Decimal
    half_sell: Decimal
    full_buy: Decimal
    full_sell: Decimal

# snapshot: BaseSnapshot # Bu şekilde tanımlanması durumunda sınıfın miras aldığı sınıf BaseModel olarak değiştirilmeli

class GetSnapshotAndId(BaseSnapshot):
    model_config = ConfigDict(from_attributes=True)
    id: int
    created_at: datetime

class GetSnapshot(BaseModel):
    snapshots: list[GetSnapshotAndId]

class GetSummary(BaseModel):
    summaries: list[Summary]