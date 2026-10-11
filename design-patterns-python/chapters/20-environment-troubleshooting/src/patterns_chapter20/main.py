"""结构化演示确认现象、单变量注入、修复、重放与回滚。"""

from __future__ import annotations
from dataclasses import dataclass
import locale
import platform
import sys


@dataclass(frozen=True)
class Check:
    name: str
    ok: bool
    actual: str


@dataclass(frozen=True)
class ExperimentRecord:
    goal: str
    expected: str
    actual: str
    hypotheses: tuple[str, ...]
    conclusion: str


def validate_mode(config: dict[str, str]) -> Check:
    actual = config.get("mode", "<missing>")
    return Check("configuration.mode", actual == "demo", actual)


def main() -> None:
    checks = (
        Check("python.version", sys.version_info >= (3, 11), platform.python_version()),
        Check("stdout.encoding", (sys.stdout.encoding or "").lower().startswith("utf"), sys.stdout.encoding or ""),
        Check("preferred.encoding", bool(locale.getpreferredencoding(False)), locale.getpreferredencoding(False)),
    )
    for check in checks: print(f"{check.name}={check.actual} ok={check.ok}")
    assert all(check.ok for check in checks)

    config = {"mode": "prodution"}  # 单变量故障注入。
    failed = validate_mode(config)
    record = ExperimentRecord("证明配置错误可检测并回滚", "mode=demo", f"mode={failed.actual}",
                              ("路径错误", "配置未加载", "值拼写错误"), "值拼写错误")
    print(f"failure_injected={record}")
    assert not failed.ok
    config["mode"] = "demo"
    recovered = validate_mode(config)
    print(f"recovered={recovered}")
    assert recovered.ok
    print("[PASS] chapter-20")


if __name__ == "__main__": main()

