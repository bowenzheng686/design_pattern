# 综合项目操作与验收记录

## 空环境重建

1. 安装 JDK 17 并运行 `scripts/doctor.ps1`；
2. 运行 `scripts/run.ps1 21`；
3. 运行两个故障/边界场景；
4. 运行 `scripts/run-all.ps1` 做全库回归；
5. 运行 `scripts/clean.ps1`，确认构建产物已回滚。

## 可观测入口

- `retry.trace`：外部调用每次尝试及失败；
- `audit.events`：状态业务事件；
- `notifications`：用户侧可见副作用；
- `metrics`：请求、拒绝、支付尝试/重试、履约成功计数；
- `CheckoutResult`：一次请求的最终机器可判定结果。

## 验收矩阵

| 场景 | 状态 | SDK 尝试 | 通知 | 关键指标 |
|---|---|---:|---:|---|
| 正常 | `SHIPPED` | 1 | 2 | `checkout.success=1` |
| 首次支付失败 | `SHIPPED` | 2 | 2 | `payment.retries=1` |
| 非法订单 | `NEW` | 0 | 0 | `checkout.rejected=1` |

## 回滚

项目不写业务数据到磁盘、不占端口。进程退出即清空业务状态；构建文件通过根目录 `scripts/clean.ps1` 删除。

