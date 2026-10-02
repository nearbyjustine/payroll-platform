package dev.justine.payroll.payroll;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import dev.justine.payroll.common.ConflictException;
import java.time.Clock;
import java.time.Instant;
import java.time.YearMonth;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

@ExtendWith(MockitoExtension.class)
class PayrollRunServiceTest {

    @Mock PayrollRunRepository runs;
    @Mock PayslipRepository payslips;
    @Mock ApplicationEventPublisher events;

    // A fixed clock: "now" is 15 Sep 2026, so tests never depend on the real date.
    private final Clock clock = Clock.fixed(Instant.parse("2026-09-15T02:00:00Z"), ZoneOffset.UTC);
    private PayrollRunService service;

    @BeforeEach
    void setUp() {
        service = new PayrollRunService(runs, payslips, events, clock);
    }

    @Test
    void acceptsRunAndPublishesEvent() {
        when(runs.existsByPeriod("2026-09")).thenReturn(false);
        when(runs.saveAndFlush(any())).thenAnswer(inv -> inv.getArgument(0));

        var response = service.request(YearMonth.of(2026, 9));

        assertThat(response.status()).isEqualTo(RunStatus.PENDING);
        verify(events).publishEvent(any(PayrollRunRequested.class));
    }

    @Test
    void rejectsSecondRunForSamePeriod() {
        when(runs.existsByPeriod("2026-09")).thenReturn(true);

        assertThatThrownBy(() -> service.request(YearMonth.of(2026, 9))).isInstanceOf(ConflictException.class);
        verify(runs, never()).saveAndFlush(any());
        verifyNoInteractions(events);
    }

    @Test
    void rejectsPeriodsFarInTheFuture() {
        assertThatThrownBy(() -> service.request(YearMonth.of(2027, 1))).isInstanceOf(ConflictException.class);
    }
}
