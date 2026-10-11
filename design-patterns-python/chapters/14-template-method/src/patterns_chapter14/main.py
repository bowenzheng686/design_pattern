"""父类定义算法骨架，子类覆盖必要步骤和可选 Hook。"""

from __future__ import annotations
from abc import ABC, abstractmethod


class DataImporter(ABC):
    def __init__(self) -> None:
        self.trace: list[str] = []

    def import_data(self, source: str) -> tuple[str, ...]:
        """模板方法：调用顺序集中且稳定。"""
        self.trace.append("read"); raw = source.strip()
        self.trace.append("parse"); records = self.parse(raw)
        if self.discard_first() and records: records.pop(0)
        self.trace.append("validate")
        for record in records:
            if not record.strip(): raise ValueError("empty record")
        self.trace.append("save"); saved = tuple(records)
        self.after_import(saved)
        return saved

    @abstractmethod
    def parse(self, raw: str) -> list[str]: ...
    def discard_first(self) -> bool: return False
    def after_import(self, records: tuple[str, ...]) -> None: self.trace.append(f"finish:{len(records)}")


class CsvImporter(DataImporter):
    def parse(self, raw: str) -> list[str]: return raw.splitlines()
    def discard_first(self) -> bool: return True


class JsonLinesImporter(DataImporter):
    def parse(self, raw: str) -> list[str]: return raw.splitlines()


def main() -> None:
    csv, json_lines = CsvImporter(), JsonLinesImporter()
    csv_records = csv.import_data("name\nAda\nLin")
    json_records = json_lines.import_data('{"name":"Ada"}\n{"name":"Lin"}')
    print(f"csv={csv_records} trace={csv.trace}")
    print(f"json={json_records} trace={json_lines.trace}")
    assert len(csv_records) == len(json_records) == 2 and csv.trace == json_lines.trace
    print("[PASS] chapter-14")


if __name__ == "__main__": main()

