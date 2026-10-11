package patterns.chapter16;

import java.util.List;

/** 第 16 章入口：发送者只接触链首，不知道最终由哪个节点处理请求。 */
public final class Main {
    record LeaveRequest(String employee, int days) {}
    record Approval(boolean handled, String approver, String message) {}

    abstract static class Approver {
        private Approver next;
        /** 返回 this 便于链式组装，同时明确链方向。 */
        Approver linkWith(Approver next) { this.next = next; return next; }

        final Approval handle(LeaveRequest request) {
            if (canApprove(request)) return approve(request);
            if (next != null) return next.handle(request);
            return new Approval(false, "NONE", "无人有权限审批 " + request.days() + " 天");
        }
        protected abstract boolean canApprove(LeaveRequest request);
        protected abstract Approval approve(LeaveRequest request);
    }

    static final class LimitApprover extends Approver {
        private final String role; private final int maxDays;
        LimitApprover(String role, int maxDays) { this.role = role; this.maxDays = maxDays; }
        protected boolean canApprove(LeaveRequest request) { return request.days() <= maxDays; }
        protected Approval approve(LeaveRequest request) {
            return new Approval(true, role, role + " approved " + request.days() + " day(s)");
        }
    }

    public static void main(String[] args) {
        Approver leader = new LimitApprover("LEADER", 1);
        Approver manager = new LimitApprover("MANAGER", 3);
        Approver director = new LimitApprover("DIRECTOR", 7);
        leader.linkWith(manager).linkWith(director);

        List<Approval> results = List.of(1, 3, 7, 10).stream()
                .map(days -> leader.handle(new LeaveRequest("Ada", days))).toList();
        results.forEach(r -> System.out.println(r.message()));
        if (!results.get(0).approver().equals("LEADER") || !results.get(1).approver().equals("MANAGER")
                || !results.get(2).approver().equals("DIRECTOR") || results.get(3).handled())
            throw new IllegalStateException("责任链路由错误");
        System.out.println("[PASS] chapter-16");
    }
}

