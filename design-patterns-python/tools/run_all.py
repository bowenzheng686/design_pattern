"""顺序运行全部章节，保留失败章节的完整输出。"""

from __future__ import annotations

from run_chapter import run


if __name__ == "__main__":
    passed = 0
    for chapter_id in range(1, 22):
        run(str(chapter_id), [])
        passed += 1
    print(f"[PASS] all: {passed}/21 chapters passed")

