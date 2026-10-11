# 第 03 章：工厂方法 Factory Method

抽象 Creator 的稳定 `record()` 流程调用工厂方法 `create_logger()`；具体 Creator 决定生成控制台或内存 Logger。运行：`python tools/run_chapter.py 03`。新增产品只需新增产品类和工厂子类。
