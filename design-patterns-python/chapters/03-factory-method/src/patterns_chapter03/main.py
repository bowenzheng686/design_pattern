"""Creator 固定产品使用流程，子类延迟决定具体产品。"""

from __future__ import annotations
from abc import ABC, abstractmethod
from dataclasses import dataclass
from typing import Protocol


class Logger(Protocol):
    def write(self, level: str, message: str) -> str: ...


class ConsoleLogger:
    def write(self, level: str, message: str) -> str:
        return f"CONSOLE [{level}] {message}"


@dataclass
class MemoryLogger:
    entries: list[str]

    def write(self, level: str, message: str) -> str:
        line = f"MEMORY [{level}] {message}"
        self.entries.append(line)
        return line


class LoggerCreator(ABC):
    @abstractmethod
    def create_logger(self) -> Logger:
        """工厂方法：实例化决策由具体 Creator 实现。"""

    def record(self, level: str, message: str) -> str:
        return self.create_logger().write(level.upper(), message.strip())


class ConsoleLoggerCreator(LoggerCreator):
    def create_logger(self) -> Logger:
        return ConsoleLogger()


@dataclass
class MemoryLoggerCreator(LoggerCreator):
    entries: list[str]

    def create_logger(self) -> Logger:
        return MemoryLogger(self.entries)


def main() -> None:
    entries: list[str] = []
    creators: list[LoggerCreator] = [ConsoleLoggerCreator(), MemoryLoggerCreator(entries)]
    results = [creator.record("info", "order created") for creator in creators]
    print(*results, sep="\n")
    assert results[0].startswith("CONSOLE")
    assert entries == [results[1]] and results[1].startswith("MEMORY")
    print("[PASS] chapter-03")


if __name__ == "__main__":
    main()

