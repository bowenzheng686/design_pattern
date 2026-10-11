"""统一章节运行器：发现章节、隔离导入路径、执行并核验 PASS 标记。"""

from __future__ import annotations

import json
import os
from pathlib import Path
import subprocess
import sys


ROOT = Path(__file__).resolve().parents[1]
CHAPTERS = ROOT / "chapters"


def find_chapter(raw_id: str) -> Path:
    """将 1/01 等输入正规化为两位编号，并确保只匹配一个章节目录。"""
    chapter_id = f"{int(raw_id):02d}"
    matches = sorted(path for path in CHAPTERS.iterdir() if path.is_dir() and path.name.startswith(chapter_id + "-"))
    if len(matches) != 1:
        raise SystemExit(f"chapter {chapter_id} match count must be 1, actual={len(matches)}")
    return matches[0]


def run(raw_id: str, app_args: list[str]) -> None:
    chapter = find_chapter(raw_id)
    metadata = json.loads((chapter / "chapter.json").read_text(encoding="utf-8"))
    module = metadata["module"]
    pass_marker = metadata["pass_marker"]
    source = chapter / "src"
    if not source.is_dir():
        raise SystemExit(f"missing source directory: {source}")

    env = os.environ.copy()
    env["PYTHONDONTWRITEBYTECODE"] = "1"
    env["PYTHONUTF8"] = "1"
    env["PYTHONPATH"] = str(source)
    command = [sys.executable, "-m", module, *app_args]
    print(f"==> [{chapter.name[:2]}] run {module}", flush=True)
    completed = subprocess.run(command, cwd=chapter, env=env, text=True, encoding="utf-8", capture_output=True)
    if completed.stdout:
        print(completed.stdout, end="")
    if completed.stderr:
        print(completed.stderr, end="", file=sys.stderr)
    if completed.returncode:
        raise SystemExit(f"chapter exited with code {completed.returncode}")
    if pass_marker not in completed.stdout:
        raise SystemExit(f"missing pass marker: {pass_marker}")


if __name__ == "__main__":
    if len(sys.argv) < 2:
        raise SystemExit("usage: python tools/run_chapter.py CHAPTER [args...]")
    run(sys.argv[1], sys.argv[2:])

