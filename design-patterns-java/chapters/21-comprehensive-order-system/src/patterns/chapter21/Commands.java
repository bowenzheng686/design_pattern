package patterns.chapter21;

import java.util.ArrayDeque;
import java.util.Deque;

interface OrderCommand { void execute(); void undo(); String name(); }

final class PayOrderCommand implements OrderCommand {
    private final Order order;
    private final PaymentReceipt receipt;
    private Order.State before;
    PayOrderCommand(Order order, PaymentReceipt receipt) { this.order = order; this.receipt = receipt; }
    public void execute() {
        if (receipt.paidInCents() <= 0) throw new IllegalStateException("支付回执金额非法");
        before = order.snapshotState(); order.pay();
    }
    public void undo() { order.restoreState(before); }
    public String name() { return "pay-order"; }
}

final class ShipOrderCommand implements OrderCommand {
    private final Order order;
    private Order.State before;
    ShipOrderCommand(Order order) { this.order = order; }
    public void execute() { before = order.snapshotState(); order.ship(); }
    public void undo() { order.restoreState(before); }
    public String name() { return "ship-order"; }
}

/** Invoker 保存成功命令历史；失败补偿必须严格逆序。 */
final class CommandBus {
    private final Deque<OrderCommand> executed = new ArrayDeque<>();
    void execute(OrderCommand command) { command.execute(); executed.push(command); }
    void rollbackAll() {
        while (!executed.isEmpty()) executed.pop().undo();
    }
}

