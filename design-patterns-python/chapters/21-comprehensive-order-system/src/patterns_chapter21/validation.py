"""订单校验责任链。"""

from __future__ import annotations
from dataclasses import dataclass
from .domain import Order


@dataclass(frozen=True)
class ValidationResult:
    valid: bool
    reason: str = "OK"


class ValidationHandler:
    def __init__(self) -> None: self._next: ValidationHandler | None = None
    def link_with(self, next_handler: "ValidationHandler") -> "ValidationHandler":
        self._next = next_handler; return next_handler
    def validate(self, order: Order) -> ValidationResult:
        current = self.check(order)
        return self._next.validate(order) if current.valid and self._next else current
    def check(self, order: Order) -> ValidationResult: raise NotImplementedError


class CustomerValidation(ValidationHandler):
    def check(self, order: Order) -> ValidationResult:
        return ValidationResult(bool(order.customer.strip()), "OK" if order.customer.strip() else "customer is blank")


class ItemValidation(ValidationHandler):
    def check(self, order: Order) -> ValidationResult:
        bad = any(item.quantity <= 0 or item.unit_price_in_cents <= 0 for item in order.items)
        return ValidationResult(not bad, "item quantity/price must be positive" if bad else "OK")


class InventoryValidation(ValidationHandler):
    def check(self, order: Order) -> ValidationResult:
        bad = any(not item.in_stock for item in order.items)
        return ValidationResult(not bad, "item is out of stock" if bad else "OK")

