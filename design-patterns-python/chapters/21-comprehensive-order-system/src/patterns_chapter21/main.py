"""综合项目入口：正常、瞬时支付故障和非法输入三种确定性夹具。"""

from __future__ import annotations
import argparse
from .domain import OrderBuilder
from .events import AuditObserver, ConsoleMessageSender, EventBus, NotificationObserver, OrderNotification
from .payment import LegacyPaymentAdapter, LegacyPaymentSdk, RetryingPaymentProxy
from .validation import CustomerValidation, InventoryValidation, ItemValidation
from .workflow import CheckoutStatus, CheckoutWorkflow, OrderSystemFacade, vip_pricing


def main() -> None:
    parser = argparse.ArgumentParser()
    parser.add_argument("--fail-payment", action="store_true")
    parser.add_argument("--invalid-order", action="store_true")
    args = parser.parse_args()

    metrics: dict[str, int] = {}
    validation = CustomerValidation()
    validation.link_with(ItemValidation()).link_with(InventoryValidation())
    sdk = LegacyPaymentSdk(args.fail_payment)
    payment = RetryingPaymentProxy(LegacyPaymentAdapter(sdk), 2, metrics)
    events, sender, audit = EventBus(), ConsoleMessageSender(), AuditObserver()
    order = (OrderBuilder("ORDER-21").customer("" if args.invalid_order else "Ada")
             .contact("ada@example.test").add_item("DESIGN-PATTERN-BOOK", 2, 5000, True).build())
    events.subscribe(NotificationObserver(order.contact, OrderNotification(sender)))
    events.subscribe(audit)
    facade = OrderSystemFacade(CheckoutWorkflow(validation, payment, events, vip_pricing, metrics))
    result = facade.place_order(order)

    for line in payment.trace: print(f"retry.trace={line}")
    print(f"status={result.status.value}")
    print(f"payable={result.payable}")
    print(f"order.state={order.state_name}")
    print(f"payment.attempts={sdk.attempts}")
    print(f"notifications={len(sender.messages)}")
    print(f"audit.events={len(audit.events)}")
    print(f"metrics={metrics}")

    if args.invalid_order:
        assert result.status is CheckoutStatus.REJECTED and order.state_name == "NEW"
        assert sdk.attempts == 0 and not sender.messages and not audit.events
        assert metrics["checkout.rejected"] == 1
    else:
        assert result.status is CheckoutStatus.SUCCESS and result.payable == 9000
        assert order.state_name == "SHIPPED" and sdk.attempts == (2 if args.fail_payment else 1)
        assert len(sender.messages) == len(audit.events) == 2 and metrics["checkout.success"] == 1
        if args.fail_payment: assert metrics["payment.retries"] == 1
    print("[PASS] chapter-21")


if __name__ == "__main__": main()
