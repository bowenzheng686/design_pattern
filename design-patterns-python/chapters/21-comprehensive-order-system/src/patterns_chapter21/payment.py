"""支付 Target、旧 SDK、Adapter 和重试 Proxy。"""

from __future__ import annotations
from dataclasses import dataclass
from typing import Protocol


@dataclass(frozen=True)
class PaymentReceipt:
    transaction_id: str
    paid_in_cents: int


class PaymentError(RuntimeError): pass


class PaymentGateway(Protocol):
    def charge(self, order_id: str, amount: int) -> PaymentReceipt: ...


class LegacyPaymentSdk:
    def __init__(self, fail_first: bool) -> None:
        self.fail_first, self.attempts = fail_first, 0

    def make_trade(self, reference: str, cents: int) -> dict[str, object]:
        self.attempts += 1
        if self.fail_first and self.attempts == 1:
            return {"code": 503, "trade": "", "cents": 0, "message": "temporary unavailable"}
        return {"code": 0, "trade": f"TX-{reference}-{self.attempts}", "cents": cents, "message": "accepted"}


@dataclass
class LegacyPaymentAdapter:
    sdk: LegacyPaymentSdk

    def charge(self, order_id: str, amount: int) -> PaymentReceipt:
        raw = self.sdk.make_trade(order_id, amount)
        if raw["code"] != 0: raise PaymentError(str(raw["message"]))
        return PaymentReceipt(str(raw["trade"]), int(raw["cents"]))


class RetryingPaymentProxy:
    def __init__(self, delegate: PaymentGateway, max_attempts: int, metrics: dict[str, int]) -> None:
        self.delegate, self.max_attempts, self.metrics = delegate, max_attempts, metrics
        self.trace: list[str] = []

    def charge(self, order_id: str, amount: int) -> PaymentReceipt:
        last: PaymentError | None = None
        for attempt in range(1, self.max_attempts + 1):
            self.trace.append(f"payment-attempt={attempt}")
            self.metrics["payment.attempts"] = self.metrics.get("payment.attempts", 0) + 1
            try: return self.delegate.charge(order_id, amount)
            except PaymentError as error:
                last = error; self.trace.append(f"payment-failure={error}")
                if attempt < self.max_attempts:
                    self.metrics["payment.retries"] = self.metrics.get("payment.retries", 0) + 1
        raise PaymentError(f"payment exhausted retries: {last}")

