"""用粗粒度用例入口隐藏多个子系统的调用顺序。"""

from __future__ import annotations
from dataclasses import dataclass


@dataclass
class Device:
    name: str
    log: list[str]

    def action(self, value: str) -> None:
        self.log.append(f"{self.name}:{value}")


@dataclass
class HomeTheaterFacade:
    light: Device
    screen: Device
    projector: Device
    player: Device

    def watch_movie(self, title: str) -> None:
        self.light.action("dim")
        self.screen.action("down")
        self.projector.action("on")
        self.player.action(f"play:{title}")

    def end_movie(self) -> None:
        self.player.action("stop")
        self.projector.action("off")
        self.screen.action("up")
        self.light.action("on")


def main() -> None:
    log: list[str] = []
    facade = HomeTheaterFacade(*(Device(name, log) for name in ("light", "screen", "projector", "player")))
    facade.watch_movie("Design Patterns")
    facade.end_movie()
    print(*log, sep="\n")
    assert len(log) == 8 and log[0] == "light:dim" and log[-1] == "light:on"
    print("[PASS] chapter-11")


if __name__ == "__main__":
    main()
