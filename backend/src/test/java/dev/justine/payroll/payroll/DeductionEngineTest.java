package dev.justine.payroll.payroll;

import static org.assertj.core.api.Assertions.assertThat;

import dev.justine.payroll.employee.Department;
import dev.justine.payroll.employee.Employee;
import dev.justine.payroll.employee.EmploymentType;
import dev.justine.payroll.payroll.rules.*;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;

class DeductionEngineTest {

    // Same order Spring would give us via @Order. Constructed by hand: constructor injection makes this trivial.
    private final DeductionEngine engine = new DeductionEngine(List.of(
        new SssContribution(), new PhilHealthContribution(), new PagIbigContribution(), new WithholdingTax()));

    private static Employee employee(EmploymentType type, String salary) {
        return new Employee("E-1", "T", "t@x.ph", null, type, new BigDecimal(salary), new Department("QA"));
    }

    @Test
    void regularEmployeeGetsAllLinesAndTaxUsesIncomeAfterContributions() {
        var calc = engine.calculate(employee(EmploymentType.REGULAR, "30000"), new BigDecimal("30000"));

        assertThat(calc.lines()).extracting(DeductionEngine.Line::code).containsExactly("SSS", "PHILHEALTH", "PAGIBIG", "WTAX");
        assertThat(calc.totalDeductions()).isEqualByComparingTo("3330.05");
        assertThat(calc.net()).isEqualByComparingTo("26669.95");
    }

    @Test
    void contractualEmployeeHasNoSssLine() {
        var calc = engine.calculate(employee(EmploymentType.CONTRACTUAL, "18000"), new BigDecimal("18000"));

        assertThat(calc.lines()).extracting(DeductionEngine.Line::code).containsExactly("PHILHEALTH", "PAGIBIG");
        assertThat(calc.net()).isEqualByComparingTo("17350.00");
    }

    @Test
    void grossMinusDeductionsAlwaysEqualsNet() {
        for (int salary = 10000; salary <= 300000; salary += 7350) {
            var calc = engine.calculate(employee(EmploymentType.REGULAR, String.valueOf(salary)), BigDecimal.valueOf(salary));
            BigDecimal sumOfLines = calc.lines().stream().map(DeductionEngine.Line::amount).reduce(BigDecimal.ZERO, BigDecimal::add);
            assertThat(sumOfLines).isEqualByComparingTo(calc.totalDeductions());
            assertThat(calc.gross().subtract(calc.totalDeductions())).isEqualByComparingTo(calc.net());
        }
    }
}
