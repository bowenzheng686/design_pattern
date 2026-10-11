package patterns.chapter02;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

/** 第 02 章入口：验证单例的“唯一身份”和“共享状态”两个可观测性质。 */
public final class Main {
    /**
     * 枚举实例由 JVM 类初始化过程创建。该过程自带同步保证，因此无需手写 volatile 或双重检查锁。
     * 枚举字段仍应谨慎保存可变状态；这里只用一个配置项演示共享，生产代码应考虑并发集合或不可变快照。
     */
    enum Configuration {
        INSTANCE;
        private volatile String environment = "development";
        String environment() { return environment; }
        void environment(String value) { environment = value; }
    }

    private static void require(boolean condition, String message) {
        if (!condition) throw new IllegalStateException(message);
    }

    public static void main(String[] args) throws InterruptedException {
        int workers = 32;
        CountDownLatch ready = new CountDownLatch(workers);
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(workers);
        // IdentityHashMap 按“==”而不是 equals 判重，正好用于验证对象身份唯一。
        Set<Configuration> identities = Collections.synchronizedSet(
                Collections.newSetFromMap(new IdentityHashMap<>()));

        for (int i = 0; i < workers; i++) {
            Thread thread = new Thread(() -> {
                ready.countDown();
                try { start.await(); identities.add(Configuration.INSTANCE); }
                catch (InterruptedException e) { Thread.currentThread().interrupt(); }
                finally { done.countDown(); }
            }, "singleton-check-" + i);
            thread.start();
        }
        ready.await(); start.countDown(); done.await();

        Configuration.INSTANCE.environment("production");
        String observedElsewhere = readEnvironment();
        System.out.printf("workers=%d, uniqueInstances=%d, environment=%s%n",
                workers, identities.size(), observedElsewhere);
        require(identities.size() == 1, "同一 ClassLoader 中必须只有一个实例");
        require("production".equals(observedElsewhere), "所有调用点应观察到同一实例状态");
        System.out.println("[PASS] chapter-02");
    }

    private static String readEnvironment() { return Configuration.INSTANCE.environment(); }
}

