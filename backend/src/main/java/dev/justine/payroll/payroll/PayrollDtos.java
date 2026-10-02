package dev.justine.payroll.payroll;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public final class PayrollDtos {
    private PayrollDtos() {}

    public record StartRunRequest(@NotBlank @Pattern(regexp = "\\d{4}-(0[1-9]|1[0-2])", message = "{payroll.period.format}") String period) {}

    public record RunResponse(Long id, String period, RunStatus status, int employeeCount, int processedCount,
                              BigDecimal totalGross, BigDecimal totalDeductions, BigDecimal totalNet,
                              String failureReason, String requestedBy, Instant requestedAt, Instant completedAt) {
        static RunResponse from(PayrollRun r) {
            return new RunResponse(r.getId(), r.getPeriod().toString(), r.getStatus(), r.getEmployeeCount(),
                r.getProcessedCount(), r.getTotalGross(), r.getTotalDeductions(), r.getTotalNet(),
                r.getFailureReason(), r.getRequestedBy(), r.getRequestedAt(), r.getCompletedAt());
        }
    }

    public record LineResponse(String code, String label, BigDecimal amount) {}

    public record PayslipResponse(Long id, String period, String employeeNo, String employeeName,
                                  BigDecimal gross, BigDecimal totalDeductions, BigDecimal net,
                                  boolean pdfAvailable, List<LineResponse> lines) {
        static PayslipResponse summary(Payslip p) {
            return new PayslipResponse(p.getId(), p.getRun().getPeriod().toString(), p.getEmployee().getEmployeeNo(),
                p.getEmployee().getFullName(), p.getGross(), p.getTotalDeductions(), p.getNet(), p.getPdfKey() != null, List.of());
        }

        static PayslipResponse mine(Payslip p, String employeeNo, String employeeName) {
            return new PayslipResponse(p.getId(), p.getRun().getPeriod().toString(), employeeNo, employeeName,
                p.getGross(), p.getTotalDeductions(), p.getNet(), p.getPdfKey() != null,
                p.getLines().stream().map(l -> new LineResponse(l.getCode(), l.getLabel(), l.getAmount())).toList());
        }
    }

    public record DownloadUrlResponse(String url, Instant expiresAt) {}
}
