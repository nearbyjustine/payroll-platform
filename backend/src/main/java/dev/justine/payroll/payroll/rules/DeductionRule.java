package dev.justine.payroll.payroll.rules;

import java.math.BigDecimal;

/**
 * STRATEGY: one implementation per deduction. The engine receives all of them through DI, so a new
 * deduction is a new @Component; DeductionEngine never changes (open/closed principle).
 * Ordering is controlled with @Order: contributions first, then tax.
 */
public interface DeductionRule {
    String code();
    String label();
    /** True for mandatory contributions that reduce taxable income. */
    boolean preTax();
    boolean appliesTo(DeductionContext ctx);
    BigDecimal compute(DeductionContext ctx);
}
