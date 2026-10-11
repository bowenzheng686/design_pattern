package patterns.chapter21;

enum CheckoutStatus { SUCCESS, REJECTED, FAILED }
record CheckoutResult(CheckoutStatus status, long payableInCents, String message) {}

/**
 * Template Method：checkout() 固定验证、定价、支付、状态命令、事件和失败补偿的骨架。
 * 子类只提供定价策略，并可使用完成钩子；final 防止关键安全顺序被改写。
 */
abstract class CheckoutWorkflow {
    private final ValidationHandler validation;
    private final PaymentGateway payment;
    private final EventBus events;

    CheckoutWorkflow(ValidationHandler validation, PaymentGateway payment, EventBus events) {
        this.validation = validation; this.payment = payment; this.events = events;
    }

    final CheckoutResult checkout(Order order) {
        MetricsRegistry.INSTANCE.increment("checkout.requests");
        ValidationResult validationResult = validation.validate(order);
        if (!validationResult.valid()) {
            MetricsRegistry.INSTANCE.increment("checkout.rejected");
            CheckoutResult rejected = new CheckoutResult(CheckoutStatus.REJECTED, 0, validationResult.reason());
            afterCompletion(order, rejected);
            return rejected;
        }

        long payable = pricingStrategy().payableInCents(order);
        CommandBus commands = new CommandBus();
        try {
            PaymentReceipt receipt = payment.charge(order.id(), payable);
            commands.execute(new PayOrderCommand(order, receipt));
            events.publish(new DomainEvent("ORDER_PAID", order.id(), "paid=" + payable));
            commands.execute(new ShipOrderCommand(order));
            events.publish(new DomainEvent("ORDER_SHIPPED", order.id(), "state=" + order.stateName()));
            MetricsRegistry.INSTANCE.increment("checkout.success");
            CheckoutResult success = new CheckoutResult(CheckoutStatus.SUCCESS, payable, receipt.transactionId());
            afterCompletion(order, success);
            return success;
        } catch (RuntimeException failure) {
            // 已执行的状态命令逆序恢复；外部支付的真实退款在生产中必须是另一个幂等补偿命令。
            commands.rollbackAll();
            MetricsRegistry.INSTANCE.increment("checkout.failed");
            CheckoutResult failed = new CheckoutResult(CheckoutStatus.FAILED, payable, failure.getMessage());
            afterCompletion(order, failed);
            return failed;
        }
    }

    protected abstract PricingStrategy pricingStrategy();
    protected void afterCompletion(Order order, CheckoutResult result) {
        System.out.println("workflow-complete status=" + result.status() + " strategy=" + pricingStrategy().name());
    }
}

final class VipCheckoutWorkflow extends CheckoutWorkflow {
    private final PricingStrategy pricing = new VipPricingStrategy();
    VipCheckoutWorkflow(ValidationHandler validation, PaymentGateway payment, EventBus events) {
        super(validation, payment, events);
    }
    protected PricingStrategy pricingStrategy() { return pricing; }
}

/** Facade 是客户端唯一入口，隐藏工作流内部十余个协作者。 */
final class OrderSystemFacade {
    private final CheckoutWorkflow workflow;
    OrderSystemFacade(CheckoutWorkflow workflow) { this.workflow = workflow; }
    CheckoutResult placeOrder(Order order) { return workflow.checkout(order); }
}

