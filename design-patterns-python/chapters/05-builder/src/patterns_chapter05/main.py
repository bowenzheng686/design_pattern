"""分步构建复杂不可变对象，并用 Director 复用标准构建算法。"""

from __future__ import annotations
from dataclasses import dataclass


@dataclass(frozen=True)
class Computer:
    cpu: str
    memory_gb: int
    storage_gb: int
    gpu: str
    os: str


class ComputerBuilder:
    def __init__(self, cpu: str) -> None:
        self._cpu = cpu
        self._memory_gb = 8
        self._storage_gb = 256
        self._gpu = "integrated"
        self._os = "Linux"

    def memory(self, gb: int) -> "ComputerBuilder":
        self._memory_gb = gb
        return self

    def storage(self, gb: int) -> "ComputerBuilder":
        self._storage_gb = gb
        return self

    def gpu(self, value: str) -> "ComputerBuilder":
        self._gpu = value
        return self

    def operating_system(self, value: str) -> "ComputerBuilder":
        self._os = value
        return self

    def build(self) -> Computer:
        # 只有出口能创建 Product，跨字段约束不会散落在调用方。
        if not self._cpu.strip() or self._memory_gb < 4 or self._storage_gb < 128:
            raise ValueError("computer configuration is below the minimum")
        return Computer(self._cpu, self._memory_gb, self._storage_gb, self._gpu, self._os)


class Director:
    def gaming_pc(self) -> Computer:
        return (ComputerBuilder("Ryzen 9").memory(32).storage(2000)
                .gpu("RTX").operating_system("Windows").build())

    def office_pc(self) -> Computer:
        return ComputerBuilder("Core i5").memory(16).storage(512).build()


def main() -> None:
    director = Director()
    gaming, office = director.gaming_pc(), director.office_pc()
    print(f"gaming={gaming}")
    print(f"office={office}")
    assert gaming.memory_gb > office.memory_gb
    assert office.gpu == "integrated" and office.os == "Linux"
    print("[PASS] chapter-05")


if __name__ == "__main__":
    main()

