package dev.justine.payroll.me;

import dev.justine.payroll.payroll.PayrollDtos.PayslipResponse;
import dev.justine.payroll.payroll.PayslipService;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/me")
public class MeController {

    public record MeResponse(String username, String name, String email, List<String> roles) {}

    private final PayslipService payslips;

    public MeController(PayslipService payslips) {
        this.payslips = payslips;
    }

    @GetMapping
    public MeResponse me(@AuthenticationPrincipal Jwt jwt, JwtAuthenticationToken auth) {
        List<String> roles = auth.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .filter(a -> a.startsWith("ROLE_"))
            .map(a -> a.substring(5))
            .sorted()
            .toList();
        return new MeResponse(jwt.getClaimAsString("preferred_username"), jwt.getClaimAsString("name"),
            jwt.getClaimAsString("email"), roles);
    }

    @GetMapping("/payslips")
    public List<PayslipResponse> myPayslips() {
        return payslips.mine();
    }
}
