package dev.justine.payroll.employee;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface EmployeeRepository extends JpaRepository<Employee, Long>, JpaSpecificationExecutor<Employee> {

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByEmployeeNo(String employeeNo);

    Optional<Employee> findByUsername(String username);

    /** Search + paging, with the department fetched in the same query (no N+1 when mapping to DTOs). */
    @Override
    @EntityGraph(attributePaths = "department")
    Page<Employee> findAll(Specification<Employee> spec, Pageable pageable);

    /** Slice = no COUNT query; the payroll processor only needs "is there a next chunk?". */
    @EntityGraph(attributePaths = "department")
    Slice<Employee> findByActiveTrueOrderByIdAsc(Pageable pageable);

    long countByActiveTrue();
}
