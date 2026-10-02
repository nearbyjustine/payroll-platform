package dev.justine.payroll.payroll;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.YearMonth;
import org.junit.jupiter.api.Test;

/** The run's status machine: only legal transitions are allowed. */
class PayrollRunTest {

    @Test
    void movesPendingToProcessingToCompleted() {
        PayrollRun run = new PayrollRun(YearMonth.of(2026, 9), "paolo", Instant.now());
        run.start(2);
        run.recordPayslip(new BigDecimal("100"), new BigDecimal("10"), new BigDecimal("90"));
        run.complete(Instant.now());

        assertThat(run.getStatus()).isEqualTo(RunStatus.COMPLETED);
        assertThat(run.getTotalNet()).isEqualByComparingTo("90");
    }

    @Test
    void cannotCompleteARunThatNeverStarted() {
        PayrollRun run = new PayrollRun(YearMonth.of(2026, 9), "paolo", Instant.now());
        assertThatThrownBy(() -> run.complete(Instant.now())).isInstanceOf(IllegalStateException.class);
    }
}
