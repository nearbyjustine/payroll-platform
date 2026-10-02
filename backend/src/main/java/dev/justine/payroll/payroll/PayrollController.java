package dev.justine.payroll.payroll;

import dev.justine.payroll.payroll.PayrollDtos.*;
import jakarta.validation.Valid;
import java.net.URI;
import java.time.YearMonth;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class PayrollController {

    private final PayrollRunService runs;
    private final PayslipService payslips;

    public PayrollController(PayrollRunService runs, PayslipService payslips) {
        this.runs = runs;
        this.payslips = payslips;
    }

    /** 202 Accepted: "we've got it, it's processing". The client polls the Location URL. */
    @PostMapping("/payroll-runs")
    public ResponseEntity<RunResponse> start(@Valid @RequestBody StartRunRequest req) {
        RunResponse run = runs.request(YearMonth.parse(req.period()));
        return ResponseEntity.accepted().location(URI.create("/api/payroll-runs/" + run.id())).body(run);
    }

    @GetMapping("/payroll-runs")
    public List<RunResponse> recent() {
        return runs.recent();
    }

    @GetMapping("/payroll-runs/{id}")
    public RunResponse get(@PathVariable Long id) {
        return runs.get(id);
    }

    @GetMapping("/payroll-runs/{id}/payslips")
    public PagedModel<PayslipResponse> payslips(@PathVariable Long id, @PageableDefault(size = 25) Pageable pageable) {
        return new PagedModel<>(runs.payslips(id, pageable));
    }

    @GetMapping("/payslips/{id}/download-url")
    public DownloadUrlResponse downloadUrl(@PathVariable Long id) {
        return payslips.downloadUrl(id);
    }
}
