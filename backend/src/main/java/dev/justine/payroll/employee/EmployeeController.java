package dev.justine.payroll.employee;

import dev.justine.payroll.employee.EmployeeDtos.*;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class EmployeeController {

    private final EmployeeService service;

    public EmployeeController(EmployeeService service) {
        this.service = service;
    }

    @GetMapping("/departments")
    public List<DepartmentResponse> departments() {
        return service.departments();
    }

    @GetMapping("/employees")
    public PagedModel<EmployeeResponse> search(@RequestParam(required = false) String q,
                                               @RequestParam(required = false) Long department,
                                               @PageableDefault(size = 20, sort = "fullName", direction = Sort.Direction.ASC) Pageable pageable) {
        return new PagedModel<>(service.search(q, department, pageable));
    }

    @GetMapping("/employees/{id}")
    public EmployeeResponse get(@PathVariable Long id) {
        return service.get(id);
    }

    @PostMapping("/employees")
    public ResponseEntity<EmployeeResponse> create(@Valid @RequestBody CreateEmployeeRequest req) {
        EmployeeResponse created = service.create(req);
        return ResponseEntity.created(URI.create("/api/employees/" + created.id())).body(created);
    }

    @PutMapping("/employees/{id}")
    public EmployeeResponse update(@PathVariable Long id, @Valid @RequestBody UpdateEmployeeRequest req) {
        return service.update(id, req);
    }
}
