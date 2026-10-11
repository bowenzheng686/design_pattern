package patterns.chapter07;

/** 第 07 章入口：把第三方接口翻译为系统已经稳定使用的目标接口。 */
public final class Main {
    record PaymentRequest(String orderId, long amountInCents) {}
    record PaymentResult(boolean successful, String transactionId, String message) {}

    /** Target：业务层期望的统一接口，使用领域对象而不是 SDK 参数。 */
    interface PaymentGateway { PaymentResult pay(PaymentRequest request); }

    /** Adaptee：假设来自不可修改的第三方包，命名、参数顺序和返回模型都不同。 */
    static final class LegacyPaySdk {
        record SdkResponse(int code, String tradeNo, String detail) {}
        SdkResponse createTrade(long cents, String merchantOrder) {
            if (cents <= 0) return new SdkResponse(400, "", "invalid amount");
            return new SdkResponse(0, "T-" + merchantOrder + "-" + cents, "accepted");
        }
    }

    /**
     * Adapter 只负责协议翻译：领域层不出现 code=0 等第三方约定。
     * 使用组合持有 SDK，便于测试时注入替身，也不受 Java 单继承限制。
     */
    static final class LegacyPayAdapter implements PaymentGateway {
        private final LegacyPaySdk sdk;
        LegacyPayAdapter(LegacyPaySdk sdk) { this.sdk = sdk; }
        public PaymentResult pay(PaymentRequest request) {
            LegacyPaySdk.SdkResponse response = sdk.createTrade(request.amountInCents(), request.orderId());
            return new PaymentResult(response.code() == 0, response.tradeNo(), response.detail());
        }
    }

    public static void main(String[] args) {
        PaymentGateway gateway = new LegacyPayAdapter(new LegacyPaySdk());
        PaymentResult result = gateway.pay(new PaymentRequest("ORDER-7", 1999));
        System.out.println("normalized-result=" + result);
        if (!result.successful() || !result.transactionId().endsWith("-1999"))
            throw new IllegalStateException("适配器翻译错误");
        System.out.println("[PASS] chapter-07");
    }
}

