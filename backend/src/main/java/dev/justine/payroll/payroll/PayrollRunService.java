package dev.justine.payroll.payroll;

import dev.justine.payroll.common.ConflictException;
import dev.justine.payroll.common.CurrentUser;
import dev.justine.payroll.common.NotFoundException;
import dev.justine.payroll.payroll.PayrollDtos.*;
import java.time.Clock;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.List;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class PayrollRunService {

    private static final ZoneId MANILA = ZoneId.of("Asia/Manila");

    private final PayrollRunRepository runs;
    private final PayslipRepository payslips;
    private final ApplicationEventPublisher events;
    private final Clock clock;

    public PayrollRunService(PayrollRunRepository runs, PayslipRepository payslips,
                             ApplicationEventPublisher events, Clock clock) {
        this.runs = runs;
        this.payslips = payslips;
        this.events = events;
        this.clock = clock;
    }

    /**
     * Accepts the request and returns immediately (HTTP 202). The heavy work happens in
     * PayrollProcessor AFTER this transaction commits, so the worker can always see the new row.
     */
    @Transactional
    public RunResponse request(YearMonth period) {
        YearMonth current = YearMonth.now(clock.withZone(MANILA));
        if (period.isAfter(current.plusMonths(1))) {
            throw new ConflictException("error.badRequest", "period is too far in the future");
        }
        if (runs.existsByPeriod(period.toString())) {
            throw new ConflictException("error.conflict.payrollExists", period);
        }
        PayrollRun run;
        try {
            // saveAndFlush: hit the unique constraint here (two admins clicking at once) rather than at commit.
            run = runs.saveAndFlush(new PayrollRun(period, CurrentUser.username().orElse("system"), clock.instant()));
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("error.conflict.payrollExists", period);
        }
        events.publishEvent(new PayrollRunRequested(run.getId()));
        return RunResponse.from(run);
    }

    public List<RunResponse> recent() {
        return runs.findTop24ByOrderByPeriodDesc().stream().map(RunResponse::from).toList();
    }

    public RunResponse get(Long id) {
        return runs.findById(id).map(RunResponse::from).orElseThrow(() -> new NotFoundException("entity.payrollRun", id));
    }

    public Page<PayslipResponse> payslips(Long runId, Pageable pageable) {
        if (!runs.existsById(runId)) throw new NotFoundException("entity.payrollRun", runId);
        return payslips.findByRunId(runId, pageable).map(PayslipResponse::summary);
    }
}
