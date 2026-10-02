package dev.justine.payroll.employee;

import static org.assertj.core.api.Assertions.assertThat;

import dev.justine.payroll.TestcontainersConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;

/**
 * JPA slice against REAL Postgres (Testcontainers), with the real Flyway migrations + seed data.
 * H2 would hide Postgres-specific behaviour (LOWER() indexes, NUMERIC, constraint names).
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestcontainersConfiguration.class)
class EmployeeRepositoryTest {

    @Autowired EmployeeRepository employees;

    @Test
    void searchesByNameOrNumberCaseInsensitively() {
        var page = employees.findAll(EmployeeSpecifications.search("ana s", null), PageRequest.of(0, 10));
        assertThat(page.getContent()).extracting(Employee::getEmployeeNo).containsExactly("E-0001");

        var byNumber = employees.findAll(EmployeeSpecifications.search("e-0003", null), PageRequest.of(0, 10));
        assertThat(byNumber.getContent()).extracting(Employee::getFullName).containsExactly("Paolo Cruz");
    }

    @Test
    void filtersByDepartmentAndPaginates() {
        Long engineering = employees.findByUsername("ana").orElseThrow().getDepartment().getId();
        var page = employees.findAll(EmployeeSpecifications.search(null, engineering), PageRequest.of(0, 5));

        assertThat(page.getContent()).hasSize(5).allMatch(e -> e.getDepartment().getId().equals(engineering));
        assertThat(page.getTotalElements()).isGreaterThan(5);
    }

    @Test
    void slicesActiveEmployeesInStableOrder() {
        var first = employees.findByActiveTrueOrderByIdAsc(PageRequest.of(0, 50));
        assertThat(first.getContent()).hasSize(50);
        assertThat(first.hasNext()).isTrue();
    }
}
