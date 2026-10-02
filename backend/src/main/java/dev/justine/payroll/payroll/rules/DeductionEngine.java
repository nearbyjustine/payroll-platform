package dev.justine.payroll.payroll.rules;

import dev.justine.payroll.employee.Employee;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class DeductionEngine {

    public record Line(String code, String label, BigDecimal amount) {}

    public record Calculation(BigDecimal gross, List<Line> lines, BigDecimal totalDeductions, BigDecimal net) {}

    private final List<DeductionRule> rules;

    /** Spring injects every DeductionRule bean, already sorted by @Order. */
    public DeductionEngine(List<DeductionRule> rules) {
        this.rules = List.copyOf(rules);
    }

    public Calculation calculate(Employee employee, BigDecimal gross) {
        List<Line> lines = new ArrayList<>();
        BigDecimal contributions = BigDecimal.ZERO;
        BigDecimal total = BigDecimal.ZERO;
        for (DeductionRule rule : rules) {
            DeductionContext ctx = new DeductionContext(employee, gross, contributions);
            if (!rule.appliesTo(ctx)) continue;
            BigDecimal amount = rule.compute(ctx);
            if (amount.signum() == 0) continue;
            lines.add(new Line(rule.code(), rule.label(), amount));
            total = total.add(amount);
            if (rule.preTax()) contributions = contributions.add(amount);
        }
        return new Calculation(gross, List.copyOf(lines), Money.round(total), Money.round(gross.subtract(total)));
    }
}
