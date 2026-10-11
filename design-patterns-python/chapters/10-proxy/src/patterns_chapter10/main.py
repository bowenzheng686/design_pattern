"""虚拟代理控制昂贵对象的创建时机和复用。"""

from __future__ import annotations
from threading import Lock
from typing import Protocol


class Image(Protocol):
    def display(self) -> str: ...


class HighResolutionImage:
    loads = 0

    def __init__(self, file: str) -> None:
        self.file = file
        type(self).loads += 1  # 模拟磁盘读取与解码。

    def display(self) -> str:
        return f"display pixels from {self.file}"


class ImageProxy:
    def __init__(self, file: str) -> None:
        self._file = file
        self._real: HighResolutionImage | None = None
        self._lock = Lock()

    def display(self) -> str:
        # 双重检查避免并发首次访问时重复创建真实对象。
        if self._real is None:
            with self._lock:
                if self._real is None:
                    self._real = HighResolutionImage(self._file)
        return self._real.display()


def main() -> None:
    image: Image = ImageProxy("mountain.webp")
    print(f"after proxy creation, loads={HighResolutionImage.loads}")
    first, second = image.display(), image.display()
    print(first)
    print(f"after two displays, loads={HighResolutionImage.loads}")
    assert first == second and HighResolutionImage.loads == 1
    print("[PASS] chapter-10")


if __name__ == "__main__":
    main()

