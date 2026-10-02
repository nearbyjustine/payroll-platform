package dev.justine.payroll.payroll;

import dev.justine.payroll.employee.Employee;
import dev.justine.payroll.payroll.rules.DeductionEngine.Calculation;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Payslip {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "payroll_run_id")
    private PayrollRun run;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal gross;
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal totalDeductions;
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal net;

    private String pdfKey;

    @Column(nullable = false)
    private Instant createdAt;

    /** Aggregate: lines are owned by the payslip and saved/deleted with it. */
    @OneToMany(mappedBy = "payslip", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id")
    private List<PayslipLine> lines = new ArrayList<>();

    protected Payslip() {}

    public Payslip(PayrollRun run, Employee employee, Calculation calc, Instant createdAt) {
        this.run = run;
        this.employee = employee;
        this.gross = calc.gross();
        this.totalDeductions = calc.totalDeductions();
        this.net = calc.net();
        this.createdAt = createdAt;
        calc.lines().forEach(l -> lines.add(new PayslipLine(this, l.code(), l.label(), l.amount())));
    }

    public void attachPdf(String key) { this.pdfKey = key; }

    public Long getId() { return id; }
    public PayrollRun getRun() { return run; }
    public Employee getEmployee() { return employee; }
    public BigDecimal getGross() { return gross; }
    public BigDecimal getTotalDeductions() { return totalDeductions; }
    public BigDecimal getNet() { return net; }
    public String getPdfKey() { return pdfKey; }
    public Instant getCreatedAt() { return createdAt; }
    public List<PayslipLine> getLines() { return lines; }
}
