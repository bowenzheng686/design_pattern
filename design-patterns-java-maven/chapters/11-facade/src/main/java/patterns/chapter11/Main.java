package patterns.chapter11;

import java.util.ArrayList;
import java.util.List;

/** 第 11 章入口：把碎片化子系统调用收敛为具有业务含义的粗粒度入口。 */
public final class Main {
    static final class Light {
        private final List<String> log; Light(List<String> log) { this.log = log; }
        void dim() { log.add("light:dim"); } void on() { log.add("light:on"); }
    }
    static final class Screen {
        private final List<String> log; Screen(List<String> log) { this.log = log; }
        void down() { log.add("screen:down"); } void up() { log.add("screen:up"); }
    }
    static final class Projector {
        private final List<String> log; Projector(List<String> log) { this.log = log; }
        void on() { log.add("projector:on"); } void off() { log.add("projector:off"); }
    }
    static final class Player {
        private final List<String> log; Player(List<String> log) { this.log = log; }
        void play(String title) { log.add("player:play:" + title); } void stop() { log.add("player:stop"); }
    }

    static final class HomeTheaterFacade {
        private final Light light; private final Screen screen;
        private final Projector projector; private final Player player;
        HomeTheaterFacade(Light light, Screen screen, Projector projector, Player player) {
            this.light = light; this.screen = screen; this.projector = projector; this.player = player;
        }
        /** 外观拥有“正确顺序”的知识，客户端只表达观看电影的意图。 */
        void watchMovie(String title) { light.dim(); screen.down(); projector.on(); player.play(title); }
        void endMovie() { player.stop(); projector.off(); screen.up(); light.on(); }
    }

    public static void main(String[] args) {
        List<String> log = new ArrayList<>();
        HomeTheaterFacade theater = new HomeTheaterFacade(
                new Light(log), new Screen(log), new Projector(log), new Player(log));
        theater.watchMovie("Design Patterns");
        theater.endMovie();
        log.forEach(System.out::println);
        if (log.size() != 8 || !log.get(0).equals("light:dim") || !log.get(7).equals("light:on"))
            throw new IllegalStateException("外观调用顺序错误");
        System.out.println("[PASS] chapter-11");
    }
}
