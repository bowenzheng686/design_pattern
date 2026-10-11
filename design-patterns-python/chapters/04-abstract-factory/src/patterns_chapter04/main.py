"""一次创建相互匹配的一族对象，避免跨主题产品混用。"""

from __future__ import annotations
from abc import ABC, abstractmethod
from dataclasses import dataclass
from typing import Protocol


class Product(Protocol):
    @property
    def theme(self) -> str: ...
    def paint(self) -> str: ...


@dataclass(frozen=True)
class Widget:
    _theme: str
    kind: str
    style: str

    @property
    def theme(self) -> str:
        return self._theme

    def paint(self) -> str:
        return f"{self._theme} {self.style} {self.kind}"


class UiFactory(ABC):
    @abstractmethod
    def create_button(self) -> Product: ...
    @abstractmethod
    def create_checkbox(self) -> Product: ...


class WindowsFactory(UiFactory):
    def create_button(self) -> Product: return Widget("Windows", "button", "square")
    def create_checkbox(self) -> Product: return Widget("Windows", "checkbox", "tick")


class MacFactory(UiFactory):
    def create_button(self) -> Product: return Widget("macOS", "button", "rounded")
    def create_checkbox(self) -> Product: return Widget("macOS", "checkbox", "toggle")


def build_screen(factory: UiFactory) -> tuple[str, str]:
    button, checkbox = factory.create_button(), factory.create_checkbox()
    assert button.theme == checkbox.theme, "抽象工厂必须防止产品族混用"
    return button.theme, f"{button.paint()} + {checkbox.paint()}"


def main() -> None:
    screens = [build_screen(WindowsFactory()), build_screen(MacFactory())]
    for theme, description in screens:
        print(f"{theme} => {description}")
    assert [theme for theme, _ in screens] == ["Windows", "macOS"]
    print("[PASS] chapter-04")


if __name__ == "__main__":
    main()

