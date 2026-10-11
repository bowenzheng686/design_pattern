# 第 13 章：策略 Strategy

Python 函数是一等对象，因此无需为每个简单算法定义类；`Order.shipping_fee()` 接收可调用策略。运行：`python tools/run_chapter.py 13`。相同订单分别得到标准、加急、自提三种运费。
