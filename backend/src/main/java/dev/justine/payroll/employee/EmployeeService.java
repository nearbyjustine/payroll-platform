package dev.justine.payroll.employee;

import dev.justine.payroll.common.ConflictException;
import dev.justine.payroll.common.NotFoundException;
import dev.justine.payroll.employee.EmployeeDtos.*;
import java.util.List;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class EmployeeService {

    private final EmployeeRepository employees;
    private final DepartmentRepository departments;

    public EmployeeService(EmployeeRepository employees, DepartmentRepository departments) {
        this.employees = employees;
        this.departments = departments;
    }

    public Page<EmployeeResponse> search(String q, Long departmentId, Pageable pageable) {
        return employees.findAll(EmployeeSpecifications.search(q, departmentId), pageable).map(EmployeeResponse::from);
    }

    public EmployeeResponse get(Long id) {
        return EmployeeResponse.from(find(id));
    }

    @Cacheable("departments")
    public List<DepartmentResponse> departments() {
        return departments.findAll().stream().map(d -> new DepartmentResponse(d.getId(), d.getName())).toList();
    }

    @Transactional
    public EmployeeResponse create(CreateEmployeeRequest req) {
        // Friendly checks first; the DB unique constraints remain the real guarantee under concurrency.
        if (employees.existsByEmailIgnoreCase(req.email())) throw new ConflictException("error.conflict.duplicate", "email");
        if (employees.existsByEmployeeNo(req.employeeNo())) throw new ConflictException("error.conflict.duplicate", "employee number");
        Employee e = new Employee(req.employeeNo(), req.fullName(), req.email().toLowerCase(), blankToNull(req.username()),
            req.employmentType(), req.baseSalary(), department(req.departmentId()));
        return EmployeeResponse.from(employees.save(e));
    }

    @Transactional
    public EmployeeResponse update(Long id, UpdateEmployeeRequest req) {
        Employee e = find(id);
        if (e.getVersion() != req.version()) {
            // The client edited an old copy. Without this, last-write-wins silently loses someone's change.
            throw new ObjectOptimisticLockingFailureException(Employee.class, id);
        }
        e.update(req.fullName(), req.email().toLowerCase(), req.employmentType(), req.baseSalary(),
            department(req.departmentId()), req.active());
        employees.flush();   // flush now so the version bump (and any constraint error) happens inside this method
        return EmployeeResponse.from(e);
    }

    private Employee find(Long id) {
        return employees.findById(id).orElseThrow(() -> new NotFoundException("entity.employee", id));
    }

    private Department department(Long id) {
        return departments.findById(id).orElseThrow(() -> new NotFoundException("entity.department", id));
    }

    private static String blankToNull(String s) {
        return (s == null || s.isBlank()) ? null : s.trim();
    }
}
