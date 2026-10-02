package dev.justine.payroll.employee;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

/**
 * Specification pattern for dynamic search: each filter is a small, testable predicate,
 * combined only when the caller supplied it (instead of string-concatenated JPQL).
 */
public final class EmployeeSpecifications {
    private EmployeeSpecifications() {}

    public static Specification<Employee> nameOrNumberContains(String q) {
        if (!StringUtils.hasText(q)) return null;
        String like = "%" + q.trim().toLowerCase() + "%";
        return (root, query, cb) -> cb.or(
            cb.like(cb.lower(root.get("fullName")), like),
            cb.like(cb.lower(root.get("employeeNo")), like));
    }

    public static Specification<Employee> inDepartment(Long departmentId) {
        if (departmentId == null) return null;
        return (root, query, cb) -> cb.equal(root.get("department").get("id"), departmentId);
    }

    /** Combines only the filters the caller actually supplied; no filters means "match all". */
    public static Specification<Employee> search(String q, Long departmentId) {
        Specification<Employee> result = (root, query, cb) -> cb.conjunction();
        for (Specification<Employee> spec : java.util.Arrays.asList(nameOrNumberContains(q), inDepartment(departmentId))) {
            if (spec != null) result = result.and(spec);
        }
        return result;
    }
}
