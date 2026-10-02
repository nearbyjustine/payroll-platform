package dev.justine.payroll.payroll.rules;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Monthly withholding tax on taxable income (gross minus contributions), using a bracket TABLE
 * instead of an if/else chain: adding or changing a bracket is a data change, not new branching logic.
 * Brackets mirror the shape of the PH graduated monthly table (simplified for learning).
 */
@Component
@Order(100)   // runs after all contributions, because it needs taxableIncome()
public class WithholdingTax implements DeductionRule {

    record Bracket(BigDecimal over, BigDecimal base, BigDecimal rate) {}

    static final List<Bracket> BRACKETS = List.of(   // ordered from highest threshold to lowest
        new Bracket(Money.of("666667"), Money.of("183541.80"), Money.of("0.35")),
        new Bracket(Money.of("166667"), Money.of("33541.80"), Money.of("0.30")),
        new Bracket(Money.of("66667"), Money.of("8541.80"), Money.of("0.25")),
        new Bracket(Money.of("33333"), Money.of("1875.00"), Money.of("0.20")),
        new Bracket(Money.of("20833"), Money.of("0.00"), Money.of("0.15")));

    public String code() { return "WTAX"; }
    public String label() { return "Withholding tax"; }
    public boolean preTax() { return false; }
    public boolean appliesTo(DeductionContext ctx) { return true; }

    public BigDecimal compute(DeductionContext ctx) {
        BigDecimal taxable = ctx.taxableIncome();
        for (Bracket b : BRACKETS) {
            if (taxable.compareTo(b.over()) > 0) {
                return Money.round(b.base().add(taxable.subtract(b.over()).multiply(b.rate())));
            }
        }
        return Money.round(BigDecimal.ZERO);
    }
}
