package dev.justine.payroll.payroll;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
public class PayslipLine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "payslip_id")
    private Payslip payslip;

    @Column(nullable = false, length = 20)
    private String code;
    @Column(nullable = false, length = 100)
    private String label;
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    protected PayslipLine() {}

    PayslipLine(Payslip payslip, String code, String label, BigDecimal amount) {
        this.payslip = payslip;
        this.code = code;
        this.label = label;
        this.amount = amount;
    }

    public String getCode() { return code; }
    public String getLabel() { return label; }
    public BigDecimal getAmount() { return amount; }
}
