package patterns.chapter10;

import java.util.concurrent.atomic.AtomicInteger;

/** 第 10 章入口：代理与真实对象接口相同，但代理负责延迟创建和缓存。 */
public final class Main {
    interface Image { String display(); }

    static final class HighResolutionImage implements Image {
        private static final AtomicInteger LOADS = new AtomicInteger();
        private final String file;
        HighResolutionImage(String file) {
            this.file = file;
            LOADS.incrementAndGet(); // 构造模拟读取磁盘和解码等昂贵操作。
        }
        public String display() { return "display pixels from " + file; }
        static int loads() { return LOADS.get(); }
    }

    static final class ImageProxy implements Image {
        private final String file;
        // volatile + synchronized 双重检查避免多线程首次访问时重复加载。
        private volatile HighResolutionImage realImage;
        ImageProxy(String file) { this.file = file; }
        public String display() {
            HighResolutionImage current = realImage;
            if (current == null) {
                synchronized (this) {
                    current = realImage;
                    if (current == null) realImage = current = new HighResolutionImage(file);
                }
            }
            return current.display();
        }
    }

    public static void main(String[] args) {
        Image image = new ImageProxy("mountain.webp");
        System.out.println("after proxy creation, loads=" + HighResolutionImage.loads());
        String first = image.display();
        String second = image.display();
        System.out.println(first);
        System.out.println("after two displays, loads=" + HighResolutionImage.loads());
        if (!first.equals(second) || HighResolutionImage.loads() != 1)
            throw new IllegalStateException("代理未正确延迟加载或复用对象");
        System.out.println("[PASS] chapter-10");
    }
}

