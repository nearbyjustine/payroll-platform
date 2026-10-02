package dev.justine.payroll.payroll;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PayslipRepository extends JpaRepository<Payslip, Long> {

    boolean existsByRunIdAndEmployeeId(Long runId, Long employeeId);

    @EntityGraph(attributePaths = {"employee", "run"})
    Page<Payslip> findByRunId(Long runId, Pageable pageable);

    @EntityGraph(attributePaths = {"run", "lines"})
    List<Payslip> findByEmployeeUsernameOrderByRunPeriodDesc(String username);

    @EntityGraph(attributePaths = {"employee", "run"})
    Optional<Payslip> findWithEmployeeById(Long id);
}
