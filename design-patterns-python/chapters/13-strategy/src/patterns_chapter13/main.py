"""用一等函数表达可替换算法族，这是 Pythonic Strategy。"""

from __future__ import annotations
from dataclasses import dataclass
from typing import Callable


@dataclass(frozen=True)
class Shipment:
    weight_grams: int
    distance_km: int


ShippingStrategy = Callable[[Shipment], int]


def standard(shipment: Shipment) -> int:
    return 500 + (shipment.weight_grams // 1000) * 100


def express(shipment: Shipment) -> int:
    return 1000 + shipment.distance_km * 20


def pickup(_: Shipment) -> int:
    return 0


@dataclass(frozen=True)
class Order:
    shipment: Shipment

    def shipping_fee(self, strategy: ShippingStrategy) -> int:
        return strategy(self.shipment)


def main() -> None:
    order = Order(Shipment(2500, 30))
    strategies: dict[str, ShippingStrategy] = {"standard": standard, "express": express, "pickup": pickup}
    fees = {name: order.shipping_fee(strategy) for name, strategy in strategies.items()}
    for name, fee in fees.items(): print(f"{name}={fee} cents")
    assert fees == {"standard": 700, "express": 1600, "pickup": 0}
    print("[PASS] chapter-13")


if __name__ == "__main__": main()

