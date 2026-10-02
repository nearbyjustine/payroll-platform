package dev.justine.payroll.payroll;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PayrollRunRepository extends JpaRepository<PayrollRun, Long> {
    boolean existsByPeriod(String period);
    List<PayrollRun> findTop24ByOrderByPeriodDesc();
}
