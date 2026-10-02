package dev.justine.payroll.payroll.rules;

import java.math.BigDecimal;
import java.math.RoundingMode;

/** One place that defines how money is rounded, so every rule rounds the same way. */
public final class Money {
    private Money() {}

    public static BigDecimal of(String value) { return new BigDecimal(value); }

    public static BigDecimal round(BigDecimal value) { return value.setScale(2, RoundingMode.HALF_UP); }

    /** value clamped to [min, max] */
    public static BigDecimal clamp(BigDecimal value, BigDecimal min, BigDecimal max) {
        return value.max(min).min(max);
    }
}
