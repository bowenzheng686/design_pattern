package patterns.chapter01;

import java.util.ArrayList;
import java.util.List;

/** 第 01 章入口：用一个小型通知服务验证 SOLID 不是口号，而是可观察的依赖方向。 */
public final class Main {
    /** 细粒度接口体现接口隔离：通道实现不需要知道模板如何生成。 */
    interface Channel { String send(String recipient, String content); }

    /** 模板同样是独立变化轴；高层模块依赖此抽象，而不是某个字符串工具类。 */
    interface TemplateRenderer { String render(String name, String action); }

    static final class PlainTextRenderer implements TemplateRenderer {
        public String render(String name, String action) { return "你好 " + name + "，请" + action; }
    }

    static final class EmailChannel implements Channel {
        private final List<String> outbox;
        EmailChannel(List<String> outbox) { this.outbox = outbox; }
        public String send(String recipient, String content) {
            String receipt = "EMAIL -> " + recipient + ": " + content;
            outbox.add(receipt);
            return receipt;
        }
    }

    static final class SmsChannel implements Channel {
        private final List<String> outbox;
        SmsChannel(List<String> outbox) { this.outbox = outbox; }
        public String send(String recipient, String content) {
            String receipt = "SMS -> " + recipient + ": " + content;
            outbox.add(receipt);
            return receipt;
        }
    }

    /**
     * 高层业务类只做编排（SRP），并通过构造器接收抽象（DIP）。
     * 新通道作为新的 Channel 实现接入，因此已有服务代码对扩展开放、对修改关闭（OCP）。
     */
    static final class NotificationService {
        private final Channel channel;
        private final TemplateRenderer renderer;
        NotificationService(Channel channel, TemplateRenderer renderer) {
            this.channel = channel;
            this.renderer = renderer;
        }
        String notify(String recipient, String name, String action) {
            return channel.send(recipient, renderer.render(name, action));
        }
    }

    private static void require(boolean condition, String message) {
        if (!condition) throw new IllegalStateException(message);
    }

    public static void main(String[] args) {
        List<String> outbox = new ArrayList<>();
        TemplateRenderer renderer = new PlainTextRenderer();
        String email = new NotificationService(new EmailChannel(outbox), renderer)
                .notify("ada@example.test", "Ada", "确认订单");
        String sms = new NotificationService(new SmsChannel(outbox), renderer)
                .notify("13800000000", "Lin", "确认订单");
        outbox.forEach(System.out::println);

        // 两种实现都能无条件替代 Channel，并保持“写入一次且返回回执”的契约（LSP）。
        require(outbox.size() == 2, "每个通道应只发送一次");
        require(email.startsWith("EMAIL") && sms.startsWith("SMS"), "通道替换必须保持正确回执");
        System.out.println("[PASS] chapter-01");
    }
}

