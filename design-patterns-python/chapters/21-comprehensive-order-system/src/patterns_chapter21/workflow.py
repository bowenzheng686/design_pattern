"""策略、Command、模板工作流和 Facade。"""

from __future__ import annotations
from dataclasses import dataclass
from enum import Enum
from typing import Callable, Protocol
from .domain import Order, OrderState
from .events import DomainEvent, EventBus
from .payment import PaymentGateway, PaymentReceipt
from .validation import ValidationHandler


PricingStrategy = Callable[[Order], int]


def vip_pricing(order: Order) -> int:
    return order.subtotal * 90 // 100


class OrderCommand(Protocol):
    def execute(self) -> None: ...
    def undo(self) -> None: ...


class PayCommand:
    def __init__(self, order: Order, receipt: PaymentReceipt) -> None:
        self.order, self.receipt, self.before = order, receipt, None
    def execute(self) -> None:
        if self.receipt.paid_in_cents <= 0: raise RuntimeError("invalid receipt")
        self.before = self.order._state; self.order.pay()
    def undo(self) -> None:
        assert self.before is not None; self.order._state = self.before


class ShipCommand:
    def __init__(self, order: Order) -> None: self.order, self.before = order, None
    def execute(self) -> None: self.before = self.order._state; self.order.ship()
    def undo(self) -> None:
        assert self.before is not None; self.order._state = self.before


class CommandBus:
    def __init__(self) -> None: self.executed: list[OrderCommand] = []
    def execute(self, command: OrderCommand) -> None: command.execute(); self.executed.append(command)
    def rollback_all(self) -> None:
        for command in reversed(self.executed): command.undo()
        self.executed.clear()


class CheckoutStatus(Enum):
    SUCCESS = "SUCCESS"
    REJECTED = "REJECTED"
    FAILED = "FAILED"


@dataclass(frozen=True)
class CheckoutResult:
    status: CheckoutStatus
    payable: int
    message: str


class CheckoutWorkflow:
    """Template Method：子类/构造参数提供策略，checkout 固定关键顺序。"""
    def __init__(self, validation: ValidationHandler, payment: PaymentGateway, events: EventBus,
                 pricing: PricingStrategy, metrics: dict[str, int]) -> None:
        self.validation, self.payment, self.events = validation, payment, events
        self.pricing, self.metrics = pricing, metrics

    def checkout(self, order: Order) -> CheckoutResult:
        self._inc("checkout.requests")
        validation = self.validation.validate(order)
        if not validation.valid:
            self._inc("checkout.rejected")
            result = CheckoutResult(CheckoutStatus.REJECTED, 0, validation.reason)
            self.after_completion(result); return result
        payable, commands = self.pricing(order), CommandBus()
        try:
            receipt = self.payment.charge(order.order_id, payable)
            commands.execute(PayCommand(order, receipt))
            self.events.publish(DomainEvent("ORDER_PAID", order.order_id, f"paid={payable}"))
            commands.execute(ShipCommand(order))
            self.events.publish(DomainEvent("ORDER_SHIPPED", order.order_id, f"state={order.state_name}"))
            self._inc("checkout.success")
            result = CheckoutResult(CheckoutStatus.SUCCESS, payable, receipt.transaction_id)
        except RuntimeError as error:
            commands.rollback_all(); self._inc("checkout.failed")
            result = CheckoutResult(CheckoutStatus.FAILED, payable, str(error))
        self.after_completion(result); return result

    def after_completion(self, result: CheckoutResult) -> None:
        print(f"workflow_complete status={result.status.value} strategy=vip-10%-off")

    def _inc(self, name: str) -> None: self.metrics[name] = self.metrics.get(name, 0) + 1


@dataclass
class OrderSystemFacade:
    workflow: CheckoutWorkflow
    def place_order(self, order: Order) -> CheckoutResult: return self.workflow.checkout(order)

