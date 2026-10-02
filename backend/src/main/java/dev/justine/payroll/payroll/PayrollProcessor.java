package dev.justine.payroll.payroll;

import dev.justine.payroll.config.AppProperties;
import dev.justine.payroll.employee.Employee;
import dev.justine.payroll.employee.EmployeeRepository;
import dev.justine.payroll.payroll.pdf.PayslipPdfRenderer;
import dev.justine.payroll.payroll.rules.DeductionEngine;
import dev.justine.payroll.payroll.rules.DeductionEngine.Calculation;
import dev.justine.payroll.payroll.storage.PayslipStorage;
import java.time.Clock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Processes a payroll run in the background, in chunks.
 *
 * Why chunks with their own transactions instead of one giant transaction?
 *  - one huge transaction holds locks and memory for the whole run and loses everything on one failure;
 *  - per-chunk commits make progress visible (processedCount) and let a retry skip finished employees.
 * Idempotency: the (run, employee) unique constraint + the existence check mean re-processing never duplicates.
 */
@Component
public class PayrollProcessor {
    private static final Logger log = LoggerFactory.getLogger(PayrollProcessor.class);

    private final PayrollRunRepository runs;
    private final PayslipRepository payslips;
    private final EmployeeRepository employees;
    private final DeductionEngine engine;
    private final PayslipPdfRenderer pdf;
    private final PayslipStorage storage;
    private final TransactionTemplate tx;
    private final AppProperties props;
    private final Clock clock;

    public PayrollProcessor(PayrollRunRepository runs, PayslipRepository payslips, EmployeeRepository employees,
                            DeductionEngine engine, PayslipPdfRenderer pdf, PayslipStorage storage,
                            PlatformTransactionManager txManager, AppProperties props, Clock clock) {
        this.runs = runs;
        this.payslips = payslips;
        this.employees = employees;
        this.engine = engine;
        this.pdf = pdf;
        this.storage = storage;
        this.tx = new TransactionTemplate(txManager);
        this.props = props;
        this.clock = clock;
    }

    /** AFTER_COMMIT: if the request transaction rolled back there is no run, so nothing must be processed. */
    @Async("payrollExecutor")
    @TransactionalEventListener
    public void onRunRequested(PayrollRunRequested event) {
        process(event.runId());
    }

    public void process(Long runId) {
        long started = System.currentTimeMillis();
        Boolean claimed = tx.execute(status -> {
            PayrollRun run = runs.findById(runId).orElseThrow();
            if (run.getStatus() != RunStatus.PENDING) return false;   // already picked up: idempotent no-op
            run.start((int) employees.countByActiveTrue());
            return true;
        });
        if (!Boolean.TRUE.equals(claimed)) {
            log.info("Run {} is not pending; skipping", runId);
            return;
        }
        log.info("Payroll run {} started", runId);

        try {
            int page = 0;
            boolean more = true;
            while (more) {
                final int current = page;
                more = Boolean.TRUE.equals(tx.execute(status -> processChunk(runId, current)));
                page++;
            }
            tx.executeWithoutResult(status -> runs.findById(runId).orElseThrow().complete(clock.instant()));
            log.info("Payroll run {} completed in {} ms", runId, System.currentTimeMillis() - started);
        } catch (RuntimeException e) {
            log.error("Payroll run {} failed", runId, e);
            tx.executeWithoutResult(status -> runs.findById(runId).orElseThrow().fail(e.getMessage(), clock.instant()));
        }
    }

    /** One chunk = one transaction. Returns true if there is another chunk after this one. */
    private boolean processChunk(Long runId, int page) {
        PayrollRun run = runs.findById(runId).orElseThrow();
        Slice<Employee> chunk = employees.findByActiveTrueOrderByIdAsc(PageRequest.of(page, props.payroll().chunkSize()));
        for (Employee employee : chunk) {
            if (payslips.existsByRunIdAndEmployeeId(runId, employee.getId())) continue;
            Calculation calc = engine.calculate(employee, employee.getBaseSalary());
            Payslip payslip = new Payslip(run, employee, calc, clock.instant());

            String key = PayslipPdfRenderer.key(run.getPeriod(), employee.getEmployeeNo());
            storage.put(key, pdf.render(employee, run.getPeriod(), calc));
            payslip.attachPdf(key);

            payslips.save(payslip);
            run.recordPayslip(calc.gross(), calc.totalDeductions(), calc.net());
        }
        return chunk.hasNext();
    }
}
