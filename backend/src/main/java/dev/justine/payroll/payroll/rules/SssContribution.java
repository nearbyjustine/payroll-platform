package dev.justine.payroll.payroll.rules;

import dev.justine.payroll.employee.EmploymentType;
import java.math.BigDecimal;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/** Simplified employee share: 4.5% of gross, capped at a 30,000 salary credit (max 1,350). Regular employees only. */
@Component
@Order(10)
public class SssContribution implements DeductionRule {
    private static final BigDecimal RATE = Money.of("0.045");
    private static final BigDecimal MAX_CREDIT = Money.of("30000");

    public String code() { return "SSS"; }
    public String label() { return "SSS contribution"; }
    public boolean preTax() { return true; }

    public boolean appliesTo(DeductionContext ctx) {
        return ctx.employee().getEmploymentType() == EmploymentType.REGULAR;
    }

    public BigDecimal compute(DeductionContext ctx) {
        return Money.round(ctx.gross().min(MAX_CREDIT).multiply(RATE));
    }
}
