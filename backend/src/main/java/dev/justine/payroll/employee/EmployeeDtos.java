package dev.justine.payroll.employee;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

/** API contract. Entities never leave the service layer. */
public final class EmployeeDtos {
    private EmployeeDtos() {}

    public record CreateEmployeeRequest(
        @NotBlank @Pattern(regexp = "E-\\d{4}") String employeeNo,
        @NotBlank @Size(max = 150) String fullName,
        @NotBlank @Email @Size(max = 150) String email,
        @Size(max = 50) String username,
        @NotNull EmploymentType employmentType,
        @NotNull @Positive @Digits(integer = 10, fraction = 2) BigDecimal baseSalary,
        @NotNull Long departmentId) {}

    public record UpdateEmployeeRequest(
        @NotBlank @Size(max = 150) String fullName,
        @NotBlank @Email @Size(max = 150) String email,
        @NotNull EmploymentType employmentType,
        @NotNull @Positive @Digits(integer = 10, fraction = 2) BigDecimal baseSalary,
        @NotNull Long departmentId,
        boolean active,
        @NotNull Long version) {}

    public record EmployeeResponse(Long id, String employeeNo, String fullName, String email, String username,
                                   EmploymentType employmentType, BigDecimal baseSalary,
                                   Long departmentId, String departmentName, boolean active, long version) {
        public static EmployeeResponse from(Employee e) {
            return new EmployeeResponse(e.getId(), e.getEmployeeNo(), e.getFullName(), e.getEmail(), e.getUsername(),
                e.getEmploymentType(), e.getBaseSalary(), e.getDepartment().getId(), e.getDepartment().getName(),
                e.isActive(), e.getVersion());
        }
    }

    public record DepartmentResponse(Long id, String name) {}
}
