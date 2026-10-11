"""通过元类截获构造调用，演示线程安全的进程内单例。"""

from __future__ import annotations
from concurrent.futures import ThreadPoolExecutor
from threading import Lock
from typing import Any


class SingletonMeta(type):
    """每个使用该元类的具体类拥有一个实例；锁保护首次创建窗口。"""
    _instances: dict[type, Any] = {}
    _lock = Lock()

    def __call__(cls, *args: object, **kwargs: object) -> Any:
        if cls not in cls._instances:
            with cls._lock:
                if cls not in cls._instances:
                    cls._instances[cls] = super().__call__(*args, **kwargs)
        return cls._instances[cls]


class Configuration(metaclass=SingletonMeta):
    def __init__(self) -> None:
        # __init__ 在本元类实现下仅于实际创建时调用一次。
        self.environment = "development"


def main() -> None:
    with ThreadPoolExecutor(max_workers=32) as executor:
        instances = list(executor.map(lambda _: Configuration(), range(64)))
    identities = {id(instance) for instance in instances}
    Configuration().environment = "production"
    observed = Configuration().environment
    print(f"workers=32, calls=64, unique_instances={len(identities)}, environment={observed}")
    assert len(identities) == 1
    assert observed == "production"
    print("[PASS] chapter-02")


if __name__ == "__main__":
    main()

