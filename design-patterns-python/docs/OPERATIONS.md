# 操作手册

所有命令在 `design-patterns-python` 根目录执行。

## 单章

```powershell
.\scripts\run.ps1 08
```

脚本解析 `chapter.json`，将该章 `src` 加入隔离的 `PYTHONPATH`，启动新的 Python 进程，再检查退出码与 PASS 标记。

直接运行等价命令：

```powershell
python .\tools\run_chapter.py 08
```

## 全量验收

```powershell
.\scripts\run-all.ps1
```

任何章节断言失败、退出码非零或缺少 PASS 标记都会中断；成功时输出 `21/21 chapters passed`。

## 综合项目

```powershell
.\scripts\run.ps1 21
.\scripts\run.ps1 21 --fail-payment
.\scripts\run.ps1 21 --invalid-order
```

三个场景分别验证正常履约、支付瞬时失败后恢复、非法订单在外部调用前被拦截。

## 清理与回滚

```powershell
.\scripts\clean.ps1
```

删除范围只包括 `__pycache__`、`.pytest_cache`、`.ruff_cache`、`.mypy_cache`、`build`、`dist` 和 `*.egg-info`。

