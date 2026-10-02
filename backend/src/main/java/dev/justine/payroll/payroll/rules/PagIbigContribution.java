package dev.justine.payroll.payroll.rules;

import java.math.BigDecimal;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/** Simplified employee share: 2% of gross, capped at 200. */
@Component
@Order(30)
public class PagIbigContribution implements DeductionRule {
    private static final BigDecimal RATE = Money.of("0.02");
    private static final BigDecimal CAP = Money.of("200.00");

    public String code() { return "PAGIBIG"; }
    public String label() { return "Pag-IBIG contribution"; }
    public boolean preTax() { return true; }
    public boolean appliesTo(DeductionContext ctx) { return true; }

    public BigDecimal compute(DeductionContext ctx) {
        return Money.round(ctx.gross().multiply(RATE)).min(CAP);
    }
}
