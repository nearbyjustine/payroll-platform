package dev.justine.payroll.payroll;

import dev.justine.payroll.common.CurrentUser;
import dev.justine.payroll.common.ForbiddenException;
import dev.justine.payroll.common.NotFoundException;
import dev.justine.payroll.config.Roles;
import dev.justine.payroll.employee.Employee;
import dev.justine.payroll.employee.EmployeeRepository;
import dev.justine.payroll.payroll.PayrollDtos.*;
import dev.justine.payroll.payroll.storage.PayslipStorage;
import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class PayslipService {

    private final PayslipRepository payslips;
    private final EmployeeRepository employees;
    private final PayslipStorage storage;

    public PayslipService(PayslipRepository payslips, EmployeeRepository employees, PayslipStorage storage) {
        this.payslips = payslips;
        this.employees = employees;
        this.storage = storage;
    }

    public List<PayslipResponse> mine() {
        String username = CurrentUser.username().orElseThrow(ForbiddenException::new);
        Employee me = employees.findByUsername(username).orElse(null);
        if (me == null) return List.of();
        return payslips.findByEmployeeUsernameOrderByRunPeriodDesc(username).stream()
            .map(p -> PayslipResponse.mine(p, me.getEmployeeNo(), me.getFullName()))
            .toList();
    }

    /**
     * Object-level authorization: URL rules can't express "only your own payslip", so it's checked here.
     * (Skipping this check is the classic IDOR / broken object level authorization bug.)
     */
    public DownloadUrlResponse downloadUrl(Long payslipId) {
        Payslip p = payslips.findWithEmployeeById(payslipId)
            .orElseThrow(() -> new NotFoundException("entity.payslip", payslipId));
        boolean owner = Objects.equals(p.getEmployee().getUsername(), CurrentUser.username().orElse(null));
        if (!owner && !CurrentUser.hasRole(Roles.PAYROLL_ADMIN)) throw new ForbiddenException();
        if (p.getPdfKey() == null) throw new NotFoundException("entity.payslip", payslipId);
        String fileName = "payslip-" + p.getRun().getPeriod() + "-" + p.getEmployee().getEmployeeNo() + ".pdf";
        var link = storage.presignedDownload(p.getPdfKey(), fileName);
        return new DownloadUrlResponse(link.url(), link.expiresAt());
    }
}
