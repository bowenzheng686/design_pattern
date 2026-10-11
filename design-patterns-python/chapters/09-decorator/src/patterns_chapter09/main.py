"""运行时层层包装组件，动态叠加描述与价格职责。"""

from __future__ import annotations
from dataclasses import dataclass
from typing import Protocol


class Beverage(Protocol):
    @property
    def description(self) -> str: ...
    @property
    def price(self) -> int: ...


class Espresso:
    description = "浓缩咖啡"
    price = 10


@dataclass
class Milk:
    wrapped: Beverage

    @property
    def description(self) -> str: return self.wrapped.description + " + 牛奶"
    @property
    def price(self) -> int: return self.wrapped.price + 3


@dataclass
class Mocha:
    wrapped: Beverage

    @property
    def description(self) -> str: return self.wrapped.description + " + 摩卡"
    @property
    def price(self) -> int: return self.wrapped.price + 4


def main() -> None:
    order: Beverage = Mocha(Mocha(Milk(Espresso())))
    print(order.description)
    print(f"price={order.price}")
    assert order.price == 21 and order.description.endswith("摩卡 + 摩卡")
    print("[PASS] chapter-09")


if __name__ == "__main__":
    main()

