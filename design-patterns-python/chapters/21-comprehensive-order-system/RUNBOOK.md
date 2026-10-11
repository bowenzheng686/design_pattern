# 综合项目运行手册

## 验收矩阵

| 场景 | 结果 | 支付次数 | 最终状态 | 通知/审计 |
|---|---|---:|---|---|
| 正常 | SUCCESS | 1 | SHIPPED | 2/2 |
| 首次支付失败 | SUCCESS | 2 | SHIPPED | 2/2 |
| 空客户 | REJECTED | 0 | NEW | 0/0 |

观测入口：`retry.trace`、`metrics`、事件审计、通知计数和最终 `CheckoutResult`。进程退出即回滚业务状态；`scripts/clean.*` 删除解释器缓存。

