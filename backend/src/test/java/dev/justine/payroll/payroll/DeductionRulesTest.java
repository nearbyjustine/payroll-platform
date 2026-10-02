package dev.justine.payroll.payroll;

import static org.assertj.core.api.Assertions.assertThat;

import dev.justine.payroll.employee.Department;
import dev.justine.payroll.employee.Employee;
import dev.justine.payroll.employee.EmploymentType;
import dev.justine.payroll.payroll.rules.*;
import java.math.BigDecimal;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/** Pure unit tests: no Spring, milliseconds to run. Boundaries are where payroll bugs live. */
class DeductionRulesTest {

    static DeductionContext ctx(EmploymentType type, String gross, String contributions) {
        Employee e = new Employee("E-9999", "Test", "t@x.ph", null, type, new BigDecimal(gross), new Department("QA"));
        return new DeductionContext(e, new BigDecimal(gross), new BigDecimal(contributions));
    }

    @ParameterizedTest(name = "SSS on {0} = {1}")
    @CsvSource({"20000, 900.00", "30000, 1350.00", "80000, 1350.00"})
    void sssIsCappedAtMaximumSalaryCredit(String gross, String expected) {
        assertThat(new SssContribution().compute(ctx(EmploymentType.REGULAR, gross, "0"))).isEqualByComparingTo(expected);
    }

    @ParameterizedTest(name = "PhilHealth on {0} = {1}")
    @CsvSource({"5000, 250.00", "30000, 750.00", "150000, 2500.00"})
    void philHealthHasFloorAndCeiling(String gross, String expected) {
        assertThat(new PhilHealthContribution().compute(ctx(EmploymentType.REGULAR, gross, "0"))).isEqualByComparingTo(expected);
    }

    @ParameterizedTest(name = "Pag-IBIG on {0} = {1}")
    @CsvSource({"5000, 100.00", "10000, 200.00", "90000, 200.00"})
    void pagIbigIsCapped(String gross, String expected) {
        assertThat(new PagIbigContribution().compute(ctx(EmploymentType.REGULAR, gross, "0"))).isEqualByComparingTo(expected);
    }

    @ParameterizedTest(name = "tax on taxable {0} = {1}")
    @CsvSource({
        "20833.00, 0.00",          // top of the zero bracket
        "20834.00, 0.15",          // first peso over it
        "33333.00, 1875.00",       // boundary: 12,500 * 15%
        "50000.00, 5208.40",
        "100000.00, 16875.05",       // 8,541.80 + 33,333 * 25%
        "700000.00, 195208.35"})   // 183,541.80 + 33,333 * 35%
    void withholdingTaxBrackets(String taxable, String expected) {
        assertThat(new WithholdingTax().compute(ctx(EmploymentType.REGULAR, taxable, "0"))).isEqualByComparingTo(expected);
    }

    @ParameterizedTest
    @CsvSource({"30000, 2300, 1030.05"})
    void taxIsComputedAfterContributions(String gross, String contributions, String expected) {
        assertThat(new WithholdingTax().compute(ctx(EmploymentType.REGULAR, gross, contributions))).isEqualByComparingTo(expected);
    }
}
