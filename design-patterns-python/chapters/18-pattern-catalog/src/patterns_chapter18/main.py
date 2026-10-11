"""将静态速查表变成可查询、可验证的数据工具。"""

from __future__ import annotations
from dataclasses import dataclass
import sys


@dataclass(frozen=True)
class Advice:
    pattern: str
    intent: str
    caution: str


CATALOG = {
    "unique": Advice("Singleton", "进程内唯一实例", "需要隔离时用依赖注入"),
    "complex-create": Advice("Builder", "分步构造复杂对象", "字段少时构造器更简单"),
    "incompatible": Advice("Adapter", "翻译不兼容接口", "只简化调用时考虑 Facade"),
    "dynamic-feature": Advice("Decorator", "运行时叠加职责", "包装过深增加调试成本"),
    "algorithm": Advice("Strategy", "替换算法族", "单一算法无需预抽象"),
    "undo": Advice("Command", "封装操作与逆操作", "大型状态保存差量"),
    "notify": Advice("Observer", "一对多状态通知", "明确异常与退订语义"),
    "simplify": Advice("Facade", "统一复杂子系统入口", "避免上帝类"),
    "pipeline": Advice("Chain of Responsibility", "请求沿链处理", "观测未处理请求"),
    "internal-state": Advice("State", "行为由内部状态驱动", "少量状态可表驱动"),
}


def main() -> None:
    queries = sys.argv[1:2] or ["complex-create", "incompatible", "algorithm", "undo", "internal-state"]
    for query in queries:
        advice = CATALOG[query]
        print(f"{query:15} -> {advice.pattern:24} | {advice.intent} | 注意: {advice.caution}")
    assert CATALOG["undo"].pattern == "Command" and CATALOG["internal-state"].pattern == "State"
    print("[PASS] chapter-18")


if __name__ == "__main__": main()

