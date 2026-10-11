package patterns.chapter21;

/** 责任链返回明确结果，避免“无人处理”或失败原因静默丢失。 */
record ValidationResult(boolean valid, String reason) {
    static ValidationResult ok() { return new ValidationResult(true, "OK"); }
    static ValidationResult reject(String reason) { return new ValidationResult(false, reason); }
}

abstract class ValidationHandler {
    private ValidationHandler next;

    /** 链在组合根动态装配；返回下一个节点让配置顺序可读。 */
    ValidationHandler linkWith(ValidationHandler next) { this.next = next; return next; }

    final ValidationResult validate(Order order) {
        ValidationResult current = check(order);
        if (!current.valid() || next == null) return current;
        return next.validate(order);
    }
    protected abstract ValidationResult check(Order order);
}

final class CustomerValidation extends ValidationHandler {
    protected ValidationResult check(Order order) {
        return order.customer() == null || order.customer().isBlank()
                ? ValidationResult.reject("customer is blank") : ValidationResult.ok();
    }
}

final class ItemValidation extends ValidationHandler {
    protected ValidationResult check(Order order) {
        boolean bad = order.items().stream().anyMatch(item -> item.quantity() <= 0 || item.unitPriceInCents() <= 0);
        return bad ? ValidationResult.reject("item quantity/price must be positive") : ValidationResult.ok();
    }
}

final class InventoryValidation extends ValidationHandler {
    protected ValidationResult check(Order order) {
        boolean unavailable = order.items().stream().anyMatch(item -> !item.inStock());
        return unavailable ? ValidationResult.reject("item is out of stock") : ValidationResult.ok();
    }
}

