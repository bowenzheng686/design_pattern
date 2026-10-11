"""订单聚合、Builder 与 State 模式。"""

from __future__ import annotations
from dataclasses import dataclass
from typing import Protocol


@dataclass(frozen=True)
class LineItem:
    sku: str
    quantity: int
    unit_price_in_cents: int
    in_stock: bool

    @property
    def subtotal(self) -> int: return self.quantity * self.unit_price_in_cents


class OrderState(Protocol):
    name: str
    def pay(self, order: "Order") -> None: ...
    def ship(self, order: "Order") -> None: ...


class NewState:
    name = "NEW"
    def pay(self, order: "Order") -> None: order._transition(PaidState())
    def ship(self, order: "Order") -> None: raise RuntimeError("NEW 订单不能发货")


class PaidState:
    name = "PAID"
    def pay(self, order: "Order") -> None: raise RuntimeError("订单不可重复支付")
    def ship(self, order: "Order") -> None: order._transition(ShippedState())


class ShippedState:
    name = "SHIPPED"
    def pay(self, order: "Order") -> None: raise RuntimeError("已发货订单不可支付")
    def ship(self, order: "Order") -> None: raise RuntimeError("订单不可重复发货")


@dataclass
class Order:
    order_id: str
    customer: str
    contact: str
    items: tuple[LineItem, ...]
    _state: OrderState

    @property
    def state_name(self) -> str: return self._state.name
    @property
    def subtotal(self) -> int: return sum(item.subtotal for item in self.items)
    def pay(self) -> None: self._state.pay(self)
    def ship(self) -> None: self._state.ship(self)
    def _transition(self, next_state: OrderState) -> None: self._state = next_state


class OrderBuilder:
    def __init__(self, order_id: str) -> None:
        self._order_id, self._customer, self._contact = order_id, "", ""
        self._items: list[LineItem] = []

    def customer(self, value: str) -> "OrderBuilder": self._customer = value; return self
    def contact(self, value: str) -> "OrderBuilder": self._contact = value; return self
    def add_item(self, sku: str, quantity: int, price: int, in_stock: bool) -> "OrderBuilder":
        self._items.append(LineItem(sku, quantity, price, in_stock)); return self

    def build(self) -> Order:
        # 客户名等业务规则留给责任链；Builder 只保证对象结构可安全存在。
        if not self._order_id or not self._items: raise ValueError("order id and item are required")
        return Order(self._order_id, self._customer, self._contact, tuple(self._items), NewState())

