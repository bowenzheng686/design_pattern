package patterns.chapter21;

import java.util.ArrayList;
import java.util.List;

/** Implementor：发送通道可以独立增加为邮件、短信或消息队列。 */
interface MessageSender { void send(String recipient, String content); }

final class ConsoleMessageSender implements MessageSender {
    private final List<String> messages = new ArrayList<>();
    public void send(String recipient, String content) {
        String message = "NOTIFY -> " + recipient + ": " + content;
        messages.add(message); System.out.println(message);
    }
    List<String> messages() { return List.copyOf(messages); }
}

/** Abstraction：消息语义独立于具体 Sender；这条组合引用就是 Bridge。 */
abstract class Notification {
    protected final MessageSender sender;
    Notification(MessageSender sender) { this.sender = sender; }
    abstract void deliver(String recipient, DomainEvent event);
}

final class OrderNotification extends Notification {
    OrderNotification(MessageSender sender) { super(sender); }
    void deliver(String recipient, DomainEvent event) {
        sender.send(recipient, "订单 " + event.orderId() + " 状态事件: " + event.type());
    }
}

/** Observer 适配事件生命周期；Notification/MessageSender 负责内容与通道两个独立维度。 */
final class NotificationObserver implements DomainObserver {
    private final String recipient;
    private final Notification notification;
    NotificationObserver(String recipient, Notification notification) {
        this.recipient = recipient; this.notification = notification;
    }
    public void onEvent(DomainEvent event) { notification.deliver(recipient, event); }
}

