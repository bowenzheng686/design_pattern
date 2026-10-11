"""用数据与集合断言确保学习路线不漏章、不重复。"""

from dataclasses import dataclass


@dataclass(frozen=True)
class Stage:
    name: str
    chapters: tuple[int, ...]
    goal: str
    evidence: str


def main() -> None:
    stages = (
        Stage("A 认识", (1, 2, 3, 4), "说清对象、边界和用途", "运行 + 口述"),
        Stage("B 操作", (5, 6, 7, 8, 9), "修改单变量并验证", "代码差异 + 断言"),
        Stage("C 原理", (10, 11, 12, 13, 14), "解释依赖方向与取舍", "结构图 + 对比"),
        Stage("D 实战", (15, 16, 17, 18), "组合、排错并验收", "全量脚本 + 复盘"),
    )
    covered: set[int] = set()
    for stage in stages:
        print(f"{stage.name} | chapters={stage.chapters} | goal={stage.goal} | evidence={stage.evidence}")
        for chapter in stage.chapters:
            assert chapter not in covered, f"章节重复: {chapter}"
            covered.add(chapter)
    assert covered == set(range(1, 19))
    print("first_round_check=explain + run + observe + troubleshoot")
    print("[PASS] chapter-19")


if __name__ == "__main__": main()

