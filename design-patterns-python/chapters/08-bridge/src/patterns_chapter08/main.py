"""抽象侧与实现侧各自变化，通过组合引用建立桥。"""

from __future__ import annotations
from abc import ABC, abstractmethod
from dataclasses import dataclass
from typing import Protocol


class Renderer(Protocol):
    def circle(self, radius: float) -> str: ...
    def rectangle(self, width: float, height: float) -> str: ...


class VectorRenderer:
    def circle(self, radius: float) -> str: return f"VECTOR circle(r={radius})"
    def rectangle(self, width: float, height: float) -> str: return f"VECTOR rect({width}x{height})"


class RasterRenderer:
    def circle(self, radius: float) -> str: return f"RASTER pixels-for-circle(r={radius})"
    def rectangle(self, width: float, height: float) -> str: return f"RASTER pixels-for-rect({width}x{height})"


class Shape(ABC):
    def __init__(self, renderer: Renderer) -> None:
        self.renderer = renderer

    @abstractmethod
    def draw(self) -> str: ...


@dataclass(init=False)
class Circle(Shape):
    radius: float

    def __init__(self, radius: float, renderer: Renderer) -> None:
        super().__init__(renderer)
        self.radius = radius

    def draw(self) -> str: return self.renderer.circle(self.radius)


@dataclass(init=False)
class Rectangle(Shape):
    width: float
    height: float

    def __init__(self, width: float, height: float, renderer: Renderer) -> None:
        super().__init__(renderer)
        self.width, self.height = width, height

    def draw(self) -> str: return self.renderer.rectangle(self.width, self.height)


def main() -> None:
    vector, raster = VectorRenderer(), RasterRenderer()
    drawings = [Circle(2, vector).draw(), Circle(2, raster).draw(),
                Rectangle(3, 4, vector).draw(), Rectangle(3, 4, raster).draw()]
    print(*drawings, sep="\n")
    assert len(drawings) == 4 and sum(line.startswith("VECTOR") for line in drawings) == 2
    print("[PASS] chapter-08")


if __name__ == "__main__":
    main()

