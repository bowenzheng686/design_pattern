"""对象行为随内部 State 改变，客户端不负责选择下一状态。"""

from __future__ import annotations
from typing import Protocol


class VendingState(Protocol):
    name: str
    def insert_coin(self, machine: "VendingMachine") -> None: ...
    def eject_coin(self, machine: "VendingMachine") -> None: ...
    def buy(self, machine: "VendingMachine") -> None: ...


class NoCoinState:
    name = "NO_COIN"
    def insert_coin(self, machine: "VendingMachine") -> None:
        machine.log("coin accepted"); machine.transition(machine.has_coin)
    def eject_coin(self, machine: "VendingMachine") -> None: machine.log("no coin to eject")
    def buy(self, machine: "VendingMachine") -> None: machine.log("insert coin first")


class HasCoinState:
    name = "HAS_COIN"
    def insert_coin(self, machine: "VendingMachine") -> None: machine.log("coin already inserted")
    def eject_coin(self, machine: "VendingMachine") -> None:
        machine.log("coin returned"); machine.transition(machine.no_coin)
    def buy(self, machine: "VendingMachine") -> None:
        if machine.inventory <= 0:
            machine.log("sold out; coin returned")
        else:
            machine.inventory -= 1; machine.log("product dispensed")
        machine.transition(machine.no_coin)


class VendingMachine:
    def __init__(self, inventory: int) -> None:
        self.no_coin, self.has_coin = NoCoinState(), HasCoinState()
        self.state: VendingState = self.no_coin
        self.inventory, self.trace = inventory, []

    def log(self, value: str) -> None: self.trace.append(value); print(value)
    def transition(self, state: VendingState) -> None: self.trace.append(f"{self.state.name}->{state.name}"); self.state = state
    def insert_coin(self) -> None: self.state.insert_coin(self)
    def eject_coin(self) -> None: self.state.eject_coin(self)
    def buy(self) -> None: self.state.buy(self)


def main() -> None:
    machine = VendingMachine(2)
    machine.buy(); machine.insert_coin(); machine.insert_coin(); machine.buy()
    print(f"inventory={machine.inventory}, state={machine.state.name}")
    assert machine.inventory == 1 and machine.state.name == "NO_COIN"
    print("[PASS] chapter-17")


if __name__ == "__main__": main()
