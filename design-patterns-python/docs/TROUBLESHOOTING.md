# 排错手册

| 现象 | 观测 | 处理 |
|---|---|---|
| 找不到 Python | `Get-Command python` / `command -v python3` | 安装 3.11+ 或设置 `PYTHON` 环境变量 |
| 版本过低 | `python --version` | 切换虚拟环境/解释器 |
| `ModuleNotFoundError` | 查看 `chapter.json` 和 `src` | 使用统一 runner，避免手工错误的工作目录 |
| 中文乱码 | 检查终端编码和 `PYTHONUTF8` | 使用仓库脚本或 UTF-8 终端 |
| 断言异常 | 阅读最后一次业务输出和 traceback | 对照章节 README 的不变量逐项验证 |
| 留下缓存 | 查找 `__pycache__` | 运行 `scripts/clean.*` |

排错漏斗：先运行 `doctor`，再单章，随后用 `python -X dev tools/run_chapter.py XX` 获取更多运行时诊断，最后才检查业务算法。修复后重放原始命令并执行全量验收。

