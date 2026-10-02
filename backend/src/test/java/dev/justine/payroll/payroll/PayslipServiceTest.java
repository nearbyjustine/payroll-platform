package dev.justine.payroll.payroll;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import dev.justine.payroll.common.ForbiddenException;
import dev.justine.payroll.employee.*;
import dev.justine.payroll.payroll.rules.DeductionEngine.Calculation;
import dev.justine.payroll.payroll.storage.PayslipStorage;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PayslipServiceTest {

    @Mock PayslipRepository payslips;
    @Mock EmployeeRepository employees;
    @Mock PayslipStorage storage;

    @AfterEach
    void clear() {
        SecurityContextHolder.clearContext();
    }

    private static void loginAs(String username, String... roles) {
        String[] authorities = java.util.Arrays.stream(roles).map(r -> "ROLE_" + r).toArray(String[]::new);
        var auth = new TestingAuthenticationToken(username, null, authorities);
        auth.setAuthenticated(true);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    private Payslip payslipOwnedBy(String username) {
        Employee owner = new Employee("E-0002", "Hana", "h@x.ph", username, EmploymentType.REGULAR,
            new BigDecimal("42000"), new Department("HR"));
        PayrollRun run = new PayrollRun(YearMonth.of(2026, 9), "paolo", Instant.now());
        Payslip p = new Payslip(run, owner, new Calculation(new BigDecimal("42000"), List.of(), BigDecimal.ZERO,
            new BigDecimal("42000")), Instant.now());
        p.attachPdf("payslips/2026-09/E-0002.pdf");
        return p;
    }

    @Test
    void employeeCannotDownloadSomeoneElsesPayslip() {
        loginAs("ana", "EMPLOYEE");
        when(payslips.findWithEmployeeById(7L)).thenReturn(Optional.of(payslipOwnedBy("hana")));

        var service = new PayslipService(payslips, employees, storage);

        assertThatThrownBy(() -> service.downloadUrl(7L)).isInstanceOf(ForbiddenException.class);
    }

    @Test
    void ownerAndPayrollAdminCanDownload() {
        when(payslips.findWithEmployeeById(7L)).thenReturn(Optional.of(payslipOwnedBy("hana")));
        when(storage.presignedDownload(anyString(), anyString()))
            .thenReturn(new PayslipStorage.DownloadLink("http://signed", Instant.now()));
        var service = new PayslipService(payslips, employees, storage);

        loginAs("hana", "EMPLOYEE");
        assertThat(service.downloadUrl(7L).url()).isEqualTo("http://signed");

        loginAs("paolo", "PAYROLL_ADMIN");
        assertThat(service.downloadUrl(7L).url()).isEqualTo("http://signed");
    }
}
