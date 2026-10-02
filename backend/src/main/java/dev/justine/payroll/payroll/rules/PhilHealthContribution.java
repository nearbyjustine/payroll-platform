package dev.justine.payroll.payroll.rules;

import java.math.BigDecimal;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/** Simplified employee share: 2.5% of a salary base floored at 10,000 and capped at 100,000. */
@Component
@Order(20)
public class PhilHealthContribution implements DeductionRule {
    private static final BigDecimal RATE = Money.of("0.025");
    private static final BigDecimal FLOOR = Money.of("10000");
    private static final BigDecimal CEILING = Money.of("100000");

    public String code() { return "PHILHEALTH"; }
    public String label() { return "PhilHealth contribution"; }
    public boolean preTax() { return true; }
    public boolean appliesTo(DeductionContext ctx) { return true; }

    public BigDecimal compute(DeductionContext ctx) {
        return Money.round(Money.clamp(ctx.gross(), FLOOR, CEILING).multiply(RATE));
    }
}
