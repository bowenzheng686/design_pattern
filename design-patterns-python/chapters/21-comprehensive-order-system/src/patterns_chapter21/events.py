"""Observer 事件总线与 Bridge 通知。"""

from __future__ import annotations
from dataclasses import dataclass
from typing import Protocol


@dataclass(frozen=True)
class DomainEvent:
    event_type: str
    order_id: str
    detail: str


class Observer(Protocol):
    def on_event(self, event: DomainEvent) -> None: ...


class EventBus:
    def __init__(self) -> None: self._observers: list[Observer] = []
    def subscribe(self, observer: Observer) -> None: self._observers.append(observer)
    def publish(self, event: DomainEvent) -> None:
        for observer in tuple(self._observers): observer.on_event(event)


class AuditObserver:
    def __init__(self) -> None: self.events: list[DomainEvent] = []
    def on_event(self, event: DomainEvent) -> None:
        self.events.append(event); print(f"AUDIT {event.event_type} order={event.order_id} {event.detail}")


class MessageSender(Protocol):
    def send(self, recipient: str, content: str) -> None: ...


class ConsoleMessageSender:
    def __init__(self) -> None: self.messages: list[str] = []
    def send(self, recipient: str, content: str) -> None:
        message = f"NOTIFY -> {recipient}: {content}"
        self.messages.append(message); print(message)


@dataclass
class OrderNotification:
    """消息语义（抽象侧）持有发送通道（实现侧），构成 Bridge。"""
    sender: MessageSender
    def deliver(self, recipient: str, event: DomainEvent) -> None:
        self.sender.send(recipient, f"订单 {event.order_id} 状态事件: {event.event_type}")


@dataclass
class NotificationObserver:
    recipient: str
    notification: OrderNotification
    def on_event(self, event: DomainEvent) -> None: self.notification.deliver(self.recipient, event)

