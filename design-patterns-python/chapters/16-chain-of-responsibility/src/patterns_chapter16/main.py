"""发送者只认识链首，不知道最终由哪个节点处理。"""

from __future__ import annotations
from dataclasses import dataclass


@dataclass(frozen=True)
class LeaveRequest:
    employee: str
    days: int


@dataclass(frozen=True)
class Approval:
    handled: bool
    approver: str
    message: str


class Approver:
    def __init__(self, role: str, max_days: int) -> None:
        self.role, self.max_days = role, max_days
        self._next: Approver | None = None

    def link_with(self, next_handler: "Approver") -> "Approver":
        self._next = next_handler
        return next_handler

    def handle(self, request: LeaveRequest) -> Approval:
        if request.days <= self.max_days:
            return Approval(True, self.role, f"{self.role} approved {request.days} day(s)")
        if self._next:
            return self._next.handle(request)
        return Approval(False, "NONE", f"无人有权限审批 {request.days} 天")


def main() -> None:
    leader, manager, director = Approver("LEADER", 1), Approver("MANAGER", 3), Approver("DIRECTOR", 7)
    leader.link_with(manager).link_with(director)
    results = [leader.handle(LeaveRequest("Ada", days)) for days in (1, 3, 7, 10)]
    for result in results: print(result.message)
    assert [result.approver for result in results] == ["LEADER", "MANAGER", "DIRECTOR", "NONE"]
    assert not results[-1].handled
    print("[PASS] chapter-16")


if __name__ == "__main__": main()

