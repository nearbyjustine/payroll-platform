package dev.justine.payroll.payroll.rules;

import dev.justine.payroll.employee.Employee;
import java.math.BigDecimal;

/**
 * What a rule needs to compute its amount. Contributions computed so far are included because
 * withholding tax is computed on income AFTER mandatory contributions.
 */
public record DeductionContext(Employee employee, BigDecimal gross, BigDecimal contributionsSoFar) {
    public BigDecimal taxableIncome() {
        return gross.subtract(contributionsSoFar).max(BigDecimal.ZERO);
    }
}
