package patterns.chapter05;

/** 第 05 章入口：把“如何逐步构建”与最终不可变对象的表示分开。 */
public final class Main {
    /** Product 使用 final 字段，构建成功后状态不可再被外部破坏。 */
    record Computer(String cpu, int memoryGb, int storageGb, String gpu, String os) {
        static Builder builder(String cpu) { return new Builder(cpu); }

        static final class Builder {
            private final String cpu;             // 必填字段由 builder 工厂强制传入。
            private int memoryGb = 8;              // 可选字段提供合理默认值。
            private int storageGb = 256;
            private String gpu = "integrated";
            private String os = "Linux";

            private Builder(String cpu) { this.cpu = cpu; }
            Builder memoryGb(int value) { this.memoryGb = value; return this; }
            Builder storageGb(int value) { this.storageGb = value; return this; }
            Builder gpu(String value) { this.gpu = value; return this; }
            Builder os(String value) { this.os = value; return this; }

            Computer build() {
                // 所有跨字段约束集中在出口，避免产生“半合法”产品。
                if (cpu == null || cpu.isBlank()) throw new IllegalStateException("CPU 必填");
                if (memoryGb < 4 || storageGb < 128) throw new IllegalStateException("配置低于最低要求");
                return new Computer(cpu, memoryGb, storageGb, gpu, os);
            }
        }
    }

    /** Director 不是必需角色，但适合保存可复用的标准构建算法。 */
    static final class Director {
        Computer gamingPc() {
            return Computer.builder("Ryzen 9").memoryGb(32).storageGb(2000).gpu("RTX").os("Windows").build();
        }
        Computer officePc() {
            return Computer.builder("Core i5").memoryGb(16).storageGb(512).build();
        }
    }

    public static void main(String[] args) {
        Director director = new Director();
        Computer gaming = director.gamingPc();
        Computer office = director.officePc();
        System.out.println("gaming=" + gaming);
        System.out.println("office=" + office);
        if (gaming.memoryGb() <= office.memoryGb() || !"integrated".equals(office.gpu()))
            throw new IllegalStateException("配方或默认值错误");
        System.out.println("[PASS] chapter-05");
    }
}

