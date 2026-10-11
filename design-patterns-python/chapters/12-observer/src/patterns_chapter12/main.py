"""Subject 状态改变后，自动通知多个未知类型的 Observer。"""

from __future__ import annotations
from dataclasses import dataclass
from typing import Protocol


class WeatherObserver(Protocol):
    def update(self, station: "WeatherStation") -> None: ...


class WeatherStation:
    def __init__(self) -> None:
        self.temperature = 0.0
        self.humidity = 0.0
        self._observers: list[WeatherObserver] = []

    def subscribe(self, observer: WeatherObserver) -> None: self._observers.append(observer)
    def unsubscribe(self, observer: WeatherObserver) -> None: self._observers.remove(observer)

    def measurements(self, temperature: float, humidity: float) -> None:
        self.temperature, self.humidity = temperature, humidity
        # 快照允许回调期间退订，不破坏本轮迭代。
        for observer in tuple(self._observers):
            observer.update(self)


@dataclass
class CurrentDisplay:
    updates: int = 0
    text: str = ""

    def update(self, station: WeatherStation) -> None:
        self.updates += 1
        self.text = f"current: {station.temperature:.1f}°C, {station.humidity:.0f}%"
        print(self.text)


@dataclass
class StatisticsDisplay:
    total: float = 0.0
    samples: int = 0

    def update(self, station: WeatherStation) -> None:
        self.total += station.temperature
        self.samples += 1
        print(f"average: {self.total / self.samples:.1f}°C")


def main() -> None:
    station, current, stats = WeatherStation(), CurrentDisplay(), StatisticsDisplay()
    station.subscribe(current); station.subscribe(stats)
    station.measurements(26, 60); station.measurements(28, 55)
    station.unsubscribe(current); station.measurements(30, 50)
    assert current.updates == 2 and stats.samples == 3 and stats.total / 3 == 28
    print("[PASS] chapter-12")


if __name__ == "__main__": main()

