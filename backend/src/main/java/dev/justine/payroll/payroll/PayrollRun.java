package dev.justine.payroll.payroll;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.YearMonth;

/** A monthly payroll run. Its status only moves forward through the methods below (a tiny state machine). */
@Entity
public class PayrollRun {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 7)
    private String period;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RunStatus status;

    private int employeeCount;
    private int processedCount;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal totalGross = BigDecimal.ZERO;
    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal totalDeductions = BigDecimal.ZERO;
    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal totalNet = BigDecimal.ZERO;

    @Column(length = 500)
    private String failureReason;

    @Column(nullable = false, length = 50)
    private String requestedBy;
    @Column(nullable = false)
    private Instant requestedAt;
    private Instant completedAt;

    @Version
    private long version;

    protected PayrollRun() {}

    public PayrollRun(YearMonth period, String requestedBy, Instant requestedAt) {
        this.period = period.toString();
        this.status = RunStatus.PENDING;
        this.requestedBy = requestedBy;
        this.requestedAt = requestedAt;
    }

    public void start(int employeeCount) {
        requireStatus(RunStatus.PENDING);
        this.status = RunStatus.PROCESSING;
        this.employeeCount = employeeCount;
    }

    public void recordPayslip(BigDecimal gross, BigDecimal deductions, BigDecimal net) {
        requireStatus(RunStatus.PROCESSING);
        processedCount++;
        totalGross = totalGross.add(gross);
        totalDeductions = totalDeductions.add(deductions);
        totalNet = totalNet.add(net);
    }

    public void complete(Instant at) {
        requireStatus(RunStatus.PROCESSING);
        this.status = RunStatus.COMPLETED;
        this.completedAt = at;
    }

    public void fail(String reason, Instant at) {
        this.status = RunStatus.FAILED;
        this.failureReason = reason == null ? "unknown" : reason.substring(0, Math.min(reason.length(), 500));
        this.completedAt = at;
    }

    private void requireStatus(RunStatus expected) {
        if (status != expected) throw new IllegalStateException("Run " + id + " is " + status + ", expected " + expected);
    }

    public Long getId() { return id; }
    public YearMonth getPeriod() { return YearMonth.parse(period); }
    public RunStatus getStatus() { return status; }
    public int getEmployeeCount() { return employeeCount; }
    public int getProcessedCount() { return processedCount; }
    public BigDecimal getTotalGross() { return totalGross; }
    public BigDecimal getTotalDeductions() { return totalDeductions; }
    public BigDecimal getTotalNet() { return totalNet; }
    public String getFailureReason() { return failureReason; }
    public String getRequestedBy() { return requestedBy; }
    public Instant getRequestedAt() { return requestedAt; }
    public Instant getCompletedAt() { return completedAt; }
}
