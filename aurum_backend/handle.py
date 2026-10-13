from fastapi import Request
from fastapi.responses import JSONResponse
from slowapi.errors import RateLimitExceeded
from sqlalchemy.exc import OperationalError

def custom_rate_limit_handler(request: Request, exc: RateLimitExceeded):
    return JSONResponse(status_code=429, content={"detail": "Çok fazla istek gönderdiniz. Az sonra tekrar deneyin"})

def postgres_operational_exception_handler(request: Request, exc : OperationalError):
    return JSONResponse(status_code=503, content={"detail": "Veri tabanı geçici olarak kullanılamıyor. Lüften daha sonra tekrar deneyin"})

def global_exception_handler(request: Request, exc: Exception):
    return JSONResponse(status_code=500, content={"detail": "Beklenmeyen bir hata oluştu"})