# Python 本地环境配置

## 1. 必需环境

| 项目 | 要求 | 验证命令 |
|---|---:|---|
| Python | 3.11+，CPython 推荐 | `python --version` |
| PowerShell | Windows 5.1+ | `$PSVersionTable.PSVersion` |
| Bash | Linux/macOS 常规 Bash | `bash --version` |
| 编码 | UTF-8 | runner 会设置 `PYTHONUTF8=1` |

没有第三方运行依赖，也不要求数据库、Docker 或网络。`pyproject.toml` 用于描述工程版本及 IDE/静态检查配置。

## 2. 安装与虚拟环境

推荐从 Python 官方发行版或系统包管理器安装 Python 3.11+，然后在仓库根目录执行：

```powershell
python -m venv .venv
.\.venv\Scripts\Activate.ps1
python --version
```

Linux/macOS：

```bash
python3 -m venv .venv
source .venv/bin/activate
python --version
```

本项目没有需要 `pip install` 的依赖。虚拟环境的意义是固定解释器与隔离后续练习中自行添加的工具。

## 3. 成功标准

```powershell
.\scripts\doctor.ps1
.\scripts\run.ps1 01
```

应依次看到 `[PASS] environment` 和 `[PASS] chapter-01`。

## 4. 环境边界

- runner 设置 `PYTHONDONTWRITEBYTECODE=1`，正常运行不产生 `__pycache__`；
- 案例只在内存构造确定性输入；
- 清理脚本仅移除 Python 缓存、覆盖率缓存和构建目录；
- 任何章节都能单独复制、运行和调试。

