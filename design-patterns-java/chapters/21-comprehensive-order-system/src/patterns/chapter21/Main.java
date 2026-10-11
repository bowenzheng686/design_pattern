package patterns.chapter21;

/**
 * 综合项目演示入口。参数只选择预定义夹具，不改变架构：
 * --fail-payment 注入一次可恢复支付故障；--invalid-order 注入边界输入。
 */
public final class Main {
    private static void require(boolean condition, String message) {
        if (!condition) throw new IllegalStateException(message);
    }

    public static void main(String[] args) {
        boolean failPayment = hasArg(args, "--fail-payment");
        boolean invalidOrder = hasArg(args, "--invalid-order");
        MetricsRegistry.INSTANCE.reset();

        // 组合根：所有具体实现只在这里装配，领域工作流始终依赖抽象。
        ValidationHandler validation = new CustomerValidation();
        validation.linkWith(new ItemValidation()).linkWith(new InventoryValidation());
        LegacyPaymentSdk sdk = new LegacyPaymentSdk(failPayment);
        RetryingPaymentProxy payment = new RetryingPaymentProxy(new LegacyPaymentAdapter(sdk), 2);
        EventBus events = new EventBus();
        ConsoleMessageSender sender = new ConsoleMessageSender();
        AuditObserver audit = new AuditObserver();

        Order order = Order.builder("ORDER-21")
                .customer(invalidOrder ? "" : "Ada")
                .contact("ada@example.test")
                .addItem("DESIGN-PATTERN-BOOK", 2, 5000, true)
                .build();

        events.subscribe(new NotificationObserver(order.contact(), new OrderNotification(sender)));
        events.subscribe(audit);
        OrderSystemFacade facade = new OrderSystemFacade(new VipCheckoutWorkflow(validation, payment, events));
        CheckoutResult result = facade.placeOrder(order);

        payment.trace().forEach(line -> System.out.println("retry.trace=" + line));
        System.out.println("status=" + result.status());
        System.out.println("payable=" + result.payableInCents());
        System.out.println("order.state=" + order.stateName());
        System.out.println("payment.attempts=" + sdk.attempts());
        System.out.println("notifications=" + sender.messages().size());
        System.out.println("audit.events=" + audit.events().size());
        System.out.println("metrics=" + MetricsRegistry.INSTANCE.snapshot());

        if (invalidOrder) {
            require(result.status() == CheckoutStatus.REJECTED, "非法输入必须被拒绝");
            require(order.stateName().equals("NEW"), "拒绝路径不能修改订单状态");
            require(sdk.attempts() == 0 && sender.messages().isEmpty() && audit.events().isEmpty(),
                    "拒绝路径不能产生外部支付或通知副作用");
            require(MetricsRegistry.INSTANCE.value("checkout.rejected") == 1, "拒绝指标缺失");
        } else {
            require(result.status() == CheckoutStatus.SUCCESS, "有效订单必须履约成功");
            require(result.payableInCents() == 9000, "VIP 定价应为 10000 的九折");
            require(order.stateName().equals("SHIPPED"), "成功路径最终状态必须为 SHIPPED");
            require(sdk.attempts() == (failPayment ? 2 : 1), "支付尝试次数错误");
            require(sender.messages().size() == 2 && audit.events().size() == 2, "每次状态事件应通知并审计");
            require(MetricsRegistry.INSTANCE.value("checkout.success") == 1, "成功指标缺失");
            if (failPayment) require(MetricsRegistry.INSTANCE.value("payment.retries") == 1, "重试指标缺失");
        }
        System.out.println("[PASS] chapter-21");
    }

    private static boolean hasArg(String[] args, String expected) {
        for (String arg : args) if (arg.equals(expected)) return true;
        return false;
    }
}
