"""把不兼容 SDK 的方法、参数和结果协议翻译为领域接口。"""

from __future__ import annotations
from dataclasses import dataclass
from typing import Protocol


@dataclass(frozen=True)
class PaymentRequest:
    order_id: str
    amount_in_cents: int


@dataclass(frozen=True)
class PaymentResult:
    successful: bool
    transaction_id: str
    message: str


class PaymentGateway(Protocol):
    def pay(self, request: PaymentRequest) -> PaymentResult: ...


class LegacyPaySdk:
    """Adaptee 假设来自不可修改的第三方包。"""
    def create_trade(self, cents: int, merchant_order: str) -> dict[str, object]:
        if cents <= 0:
            return {"code": 400, "trade_no": "", "detail": "invalid amount"}
        return {"code": 0, "trade_no": f"T-{merchant_order}-{cents}", "detail": "accepted"}


@dataclass
class LegacyPayAdapter:
    sdk: LegacyPaySdk

    def pay(self, request: PaymentRequest) -> PaymentResult:
        raw = self.sdk.create_trade(request.amount_in_cents, request.order_id)
        return PaymentResult(
            successful=raw["code"] == 0,
            transaction_id=str(raw["trade_no"]),
            message=str(raw["detail"]),
        )


def main() -> None:
    gateway: PaymentGateway = LegacyPayAdapter(LegacyPaySdk())
    result = gateway.pay(PaymentRequest("ORDER-7", 1999))
    print(f"normalized_result={result}")
    assert result.successful and result.transaction_id.endswith("-1999")
    print("[PASS] chapter-07")


if __name__ == "__main__":
    main()

