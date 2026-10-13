from sqlmodel import SQLModel, Field, Column, DateTime, func, Numeric
from decimal import Decimal
from pydantic import EmailStr
from datetime import datetime

# Ayrıca kendi veri tabanımda kullanıcı bilgilerini tutabilirim
class Account(SQLModel, table=True):
    __tablename__="accounts"
    id: int | None = Field(default=None, primary_key=True)
    firebase_uid: str = Field(index=True, unique=True, max_length=128)
    email: EmailStr = Field(index=True, unique=True, max_length=320)
    display_name: str | None = Field(default=None, max_length=255)
    phone_number: str | None = Field(default=None, max_length=20)
    photo_url: str | None = Field(default=None, max_length=2048)
    created_at: datetime = Field(sa_column=Column(DateTime(timezone=True), server_default=func.now(), nullable=False))
    last_login_at: datetime | None = Field(default=None, sa_column=Column(DateTime(timezone=True), nullable=False))

class Snapshot(SQLModel, table=True):
    __tablename__="snapshots"
    id: int | None = Field(default=None, primary_key=True)
    user_id: int = Field(foreign_key="accounts.id", ondelete="CASCADE", index=True, nullable=False)
    gram_buy: Decimal = Field(sa_column=Column(Numeric(7,2), nullable=False))
    gram_sell: Decimal = Field(sa_column=Column(Numeric(7,2), nullable=False))
    quarter_buy: Decimal = Field(sa_column=Column(Numeric(7,2), nullable=False))
    quarter_sell: Decimal = Field(sa_column=Column(Numeric(7,2), nullable=False))
    half_buy: Decimal = Field(sa_column=Column(Numeric(7,2), nullable=False))
    half_sell: Decimal = Field(sa_column=Column(Numeric(7,2), nullable=False))
    full_buy: Decimal = Field(sa_column=Column(Numeric(7,2), nullable=False))
    full_sell: Decimal = Field(sa_column=Column(Numeric(7,2), nullable=False))
    created_at: datetime = Field(sa_column=Column(DateTime(timezone=True), server_default=func.now(), nullable=False))

class Summary(SQLModel, table=True):
    __tablename__="summaries"
    id: int | None = Field(default=None, primary_key=True)
    gram_high: Decimal = Field(sa_column=Column(Numeric(7, 2), nullable=False))
    gram_low: Decimal = Field(sa_column=Column(Numeric(7, 2), nullable=False))
    gram_close_buy: Decimal = Field(sa_column=Column(Numeric(7, 2), nullable=False))
    gram_close_sell: Decimal = Field(sa_column=Column(Numeric(7, 2), nullable=False))
    quarter_high: Decimal = Field(sa_column=Column(Numeric(7, 2), nullable=False))
    quarter_low: Decimal = Field(sa_column=Column(Numeric(7, 2), nullable=False))
    quarter_close_buy: Decimal = Field(sa_column=Column(Numeric(7, 2), nullable=False))
    quarter_close_sell: Decimal = Field(sa_column=Column(Numeric(7, 2), nullable=False))
    half_high: Decimal = Field(sa_column=Column(Numeric(7, 2), nullable=False))
    half_low: Decimal = Field(sa_column=Column(Numeric(7, 2), nullable=False))
    half_close_buy: Decimal = Field(sa_column=Column(Numeric(7, 2), nullable=False))
    half_close_sell: Decimal = Field(sa_column=Column(Numeric(7, 2), nullable=False))
    full_high: Decimal = Field(sa_column=Column(Numeric(7, 2), nullable=False))
    full_low: Decimal = Field(sa_column=Column(Numeric(7, 2), nullable=False))
    full_close_buy: Decimal = Field(sa_column=Column(Numeric(7, 2), nullable=False))
    full_close_sell: Decimal = Field(sa_column=Column(Numeric(7, 2), nullable=False))
    change_rate: Decimal = Field(default=Decimal("0.00"), sa_column=Column(Numeric(4, 2), nullable=False))
    created_at: datetime = Field(sa_column=Column(DateTime(timezone=True), index=True, nullable=False, unique=True))