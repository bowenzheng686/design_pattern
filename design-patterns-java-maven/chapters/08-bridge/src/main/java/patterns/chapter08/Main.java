package patterns.chapter08;

import java.util.List;

/** 第 08 章入口：抽象与实现各自拥有继承层次，并通过组合建立“桥”。 */
public final class Main {
    /** Implementor：定义所有形状需要的最小渲染原语，而非绑定某个具体形状。 */
    interface Renderer {
        String renderCircle(double radius);
        String renderRectangle(double width, double height);
    }

    static final class VectorRenderer implements Renderer {
        public String renderCircle(double r) { return "VECTOR circle(r=" + r + ")"; }
        public String renderRectangle(double w, double h) { return "VECTOR rect(" + w + "x" + h + ")"; }
    }
    static final class RasterRenderer implements Renderer {
        public String renderCircle(double r) { return "RASTER pixels-for-circle(r=" + r + ")"; }
        public String renderRectangle(double w, double h) { return "RASTER pixels-for-rect(" + w + "x" + h + ")"; }
    }

    /** Abstraction 把实际绘制委派给 implementor；protected 让精化抽象复用桥。 */
    abstract static class Shape {
        protected final Renderer renderer;
        Shape(Renderer renderer) { this.renderer = renderer; }
        abstract String draw();
    }
    static final class Circle extends Shape {
        private final double radius;
        Circle(double radius, Renderer renderer) { super(renderer); this.radius = radius; }
        String draw() { return renderer.renderCircle(radius); }
    }
    static final class Rectangle extends Shape {
        private final double width, height;
        Rectangle(double width, double height, Renderer renderer) {
            super(renderer); this.width = width; this.height = height;
        }
        String draw() { return renderer.renderRectangle(width, height); }
    }

    public static void main(String[] args) {
        Renderer vector = new VectorRenderer();
        Renderer raster = new RasterRenderer();
        List<String> drawings = List.of(
                new Circle(2, vector).draw(), new Circle(2, raster).draw(),
                new Rectangle(3, 4, vector).draw(), new Rectangle(3, 4, raster).draw());
        drawings.forEach(System.out::println);
        long vectorCount = drawings.stream().filter(v -> v.startsWith("VECTOR")).count();
        if (drawings.size() != 4 || vectorCount != 2) throw new IllegalStateException("桥接组合不完整");
        System.out.println("[PASS] chapter-08");
    }
}

