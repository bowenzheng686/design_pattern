"""使用 deepcopy 明确原型的深拷贝边界。"""

from __future__ import annotations
from copy import deepcopy
from dataclasses import dataclass, field


@dataclass
class Document:
    title: str
    tags: list[str] = field(default_factory=list)

    def clone(self) -> "Document":
        # deepcopy 会递归复制可变对象图，并能处理常见循环引用。
        return deepcopy(self)


class PrototypeRegistry:
    def __init__(self) -> None:
        self._prototypes: dict[str, Document] = {}

    def register(self, key: str, prototype: Document) -> None:
        # 注册时也复制，避免调用者随后修改“库内原型”。
        self._prototypes[key] = prototype.clone()

    def create(self, key: str) -> Document:
        try:
            return self._prototypes[key].clone()
        except KeyError as error:
            raise ValueError(f"未知原型: {key}") from error


def main() -> None:
    registry = PrototypeRegistry()
    registry.register("report", Document("标准报告", ["internal", "draft"]))
    first, second = registry.create("report"), registry.create("report")
    first.title = "第一季度报告"
    first.tags.append("finance")
    third = registry.create("report")
    print(f"first={first}")
    print(f"second={second}")
    print(f"prototype_copy={third}")
    assert first is not second and "finance" not in second.tags and "finance" not in third.tags
    print("[PASS] chapter-06")


if __name__ == "__main__":
    main()
