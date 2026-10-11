package patterns.chapter12;

import java.util.ArrayList;
import java.util.List;

/** 第 12 章入口：Subject 状态变化自动传播给多个未知类型的观察者。 */
public final class Main {
    interface WeatherObserver { void update(WeatherStation station); }

    static final class WeatherStation {
        private final List<WeatherObserver> observers = new ArrayList<>();
        private double temperature;
        private double humidity;

        void subscribe(WeatherObserver observer) { observers.add(observer); }
        void unsubscribe(WeatherObserver observer) { observers.remove(observer); }
        double temperature() { return temperature; }
        double humidity() { return humidity; }

        void measurements(double temperature, double humidity) {
            this.temperature = temperature;
            this.humidity = humidity;
            // 快照让观察者可以安全地在回调中退订；一位观察者不应影响遍历结构。
            List.copyOf(observers).forEach(observer -> observer.update(this));
        }
    }

    /** Pull 模型：当前状况屏只拉取自己需要的两个字段。 */
    static final class CurrentConditionsDisplay implements WeatherObserver {
        private String text = ""; private int updates;
        public void update(WeatherStation station) {
            updates++;
            text = "current: %.1f°C, %.0f%%".formatted(station.temperature(), station.humidity());
            System.out.println(text);
        }
    }

    static final class StatisticsDisplay implements WeatherObserver {
        private double total; private int samples;
        public void update(WeatherStation station) {
            total += station.temperature(); samples++;
            System.out.println("average: %.1f°C".formatted(total / samples));
        }
    }

    public static void main(String[] args) {
        WeatherStation station = new WeatherStation();
        CurrentConditionsDisplay current = new CurrentConditionsDisplay();
        StatisticsDisplay statistics = new StatisticsDisplay();
        station.subscribe(current); station.subscribe(statistics);
        station.measurements(26, 60);
        station.measurements(28, 55);
        station.unsubscribe(current);
        station.measurements(30, 50);

        if (current.updates != 2 || statistics.samples != 3 || Math.abs(statistics.total / 3 - 28) > 0.001)
            throw new IllegalStateException("订阅生命周期或 Pull 数据错误");
        System.out.println("[PASS] chapter-12");
    }
}

