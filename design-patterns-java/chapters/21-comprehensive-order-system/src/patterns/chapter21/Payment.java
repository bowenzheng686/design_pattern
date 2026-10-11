package patterns.chapter21;

import java.util.ArrayList;
import java.util.List;

record PaymentReceipt(String transactionId, long paidInCents) {}

class PaymentException extends RuntimeException {
    PaymentException(String message) { super(message); }
}

/** Target：履约领域只依赖这个稳定支付端口。 */
interface PaymentGateway { PaymentReceipt charge(String orderId, long amountInCents); }

/** 不可修改的 Adaptee：用状态码和旧命名表达结果，并可注入首次瞬时失败。 */
final class LegacyPaymentSdk {
    record SdkResult(int code, String tradeNumber, long cents, String message) {}
    private final boolean failFirst;
    private int attempts;
    LegacyPaymentSdk(boolean failFirst) { this.failFirst = failFirst; }
    SdkResult makeTrade(String merchantReference, long cents) {
        attempts++;
        if (failFirst && attempts == 1) return new SdkResult(503, "", 0, "temporary unavailable");
        return new SdkResult(0, "TX-" + merchantReference + "-" + attempts, cents, "accepted");
    }
    int attempts() { return attempts; }
}

/** Adapter：把旧 SDK 的 code=0、tradeNumber 转成领域 Receipt/Exception。 */
final class LegacyPaymentAdapter implements PaymentGateway {
    private final LegacyPaymentSdk sdk;
    LegacyPaymentAdapter(LegacyPaymentSdk sdk) { this.sdk = sdk; }
    public PaymentReceipt charge(String orderId, long amountInCents) {
        LegacyPaymentSdk.SdkResult result = sdk.makeTrade(orderId, amountInCents);
        if (result.code() != 0) throw new PaymentException(result.message());
        return new PaymentReceipt(result.tradeNumber(), result.cents());
    }
}

/**
 * Proxy 与真实 Gateway 实现相同接口，因此工作流不知道重试存在。
 * 它限制最大次数、留下逐次证据，并只捕获明确的 PaymentException。
 */
final class RetryingPaymentProxy implements PaymentGateway {
    private final PaymentGateway delegate;
    private final int maxAttempts;
    private final List<String> trace = new ArrayList<>();
    RetryingPaymentProxy(PaymentGateway delegate, int maxAttempts) {
        if (maxAttempts < 1) throw new IllegalArgumentException("maxAttempts must be positive");
        this.delegate = delegate; this.maxAttempts = maxAttempts;
    }
    public PaymentReceipt charge(String orderId, long amountInCents) {
        PaymentException last = null;
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            trace.add("payment-attempt=" + attempt);
            MetricsRegistry.INSTANCE.increment("payment.attempts");
            try { return delegate.charge(orderId, amountInCents); }
            catch (PaymentException failure) {
                last = failure;
                trace.add("payment-failure=" + failure.getMessage());
                if (attempt < maxAttempts) MetricsRegistry.INSTANCE.increment("payment.retries");
            }
        }
        throw new PaymentException("payment exhausted retries: " + last.getMessage());
    }
    List<String> trace() { return List.copyOf(trace); }
}

