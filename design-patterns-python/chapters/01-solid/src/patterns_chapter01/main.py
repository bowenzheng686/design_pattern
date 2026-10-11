"""用 Protocol 和构造注入展示 SOLID 的依赖方向。"""

from __future__ import annotations
from dataclasses import dataclass
from typing import Protocol


class Channel(Protocol):
    """小接口体现 ISP；实现者只承担发送职责。"""
    def send(self, recipient: str, content: str) -> str: ...


class Renderer(Protocol):
    def render(self, name: str, action: str) -> str: ...


class PlainTextRenderer:
    def render(self, name: str, action: str) -> str:
        return f"你好 {name}，请{action}"


@dataclass
class RecordingChannel:
    """具体通道可替换而不改变高层服务，体现 LSP/OCP。"""
    kind: str
    outbox: list[str]

    def send(self, recipient: str, content: str) -> str:
        receipt = f"{self.kind} -> {recipient}: {content}"
        self.outbox.append(receipt)
        return receipt


@dataclass
class NotificationService:
    """高层只依赖两个协议（DIP），职责仅是编排（SRP）。"""
    channel: Channel
    renderer: Renderer

    def notify(self, recipient: str, name: str, action: str) -> str:
        return self.channel.send(recipient, self.renderer.render(name, action))


def main() -> None:
    outbox: list[str] = []
    renderer = PlainTextRenderer()
    email = NotificationService(RecordingChannel("EMAIL", outbox), renderer)
    sms = NotificationService(RecordingChannel("SMS", outbox), renderer)
    results = [
        email.notify("ada@example.test", "Ada", "确认订单"),
        sms.notify("13800000000", "Lin", "确认订单"),
    ]
    print(*outbox, sep="\n")
    assert len(outbox) == 2
    assert results[0].startswith("EMAIL") and results[1].startswith("SMS")
    print("[PASS] chapter-01")


if __name__ == "__main__":
    main()

