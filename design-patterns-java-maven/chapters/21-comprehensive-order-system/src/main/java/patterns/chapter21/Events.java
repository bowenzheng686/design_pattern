package patterns.chapter21;

import java.util.ArrayList;
import java.util.List;

record DomainEvent(String type, String orderId, String detail) {}
interface DomainObserver { void onEvent(DomainEvent event); }

/** Subject 只依赖 Observer 接口；同步模型让本演示的顺序与断言完全确定。 */
final class EventBus {
    private final List<DomainObserver> observers = new ArrayList<>();
    void subscribe(DomainObserver observer) { observers.add(observer); }
    void publish(DomainEvent event) { List.copyOf(observers).forEach(observer -> observer.onEvent(event)); }
}

final class AuditObserver implements DomainObserver {
    private final List<DomainEvent> events = new ArrayList<>();
    public void onEvent(DomainEvent event) {
        events.add(event);
        System.out.println("AUDIT " + event.type() + " order=" + event.orderId() + " " + event.detail());
    }
    List<DomainEvent> events() { return List.copyOf(events); }
}

