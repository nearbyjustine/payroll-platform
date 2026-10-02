package dev.justine.payroll.employee;

import dev.justine.payroll.common.Auditable;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
public class Employee extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String employeeNo;

    @Column(nullable = false, length = 150)
    private String fullName;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(unique = true, length = 50)
    private String username;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EmploymentType employmentType;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal baseSalary;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "department_id")
    private Department department;

    @Column(nullable = false)
    private boolean active = true;

    @Version
    private long version;

    protected Employee() {}

    public Employee(String employeeNo, String fullName, String email, String username,
                    EmploymentType employmentType, BigDecimal baseSalary, Department department) {
        this.employeeNo = employeeNo;
        this.fullName = fullName;
        this.email = email;
        this.username = username;
        this.employmentType = employmentType;
        this.baseSalary = baseSalary;
        this.department = department;
    }

    /** Behaviour lives on the entity; callers don't poke at fields one setter at a time. */
    public void update(String fullName, String email, EmploymentType type, BigDecimal baseSalary,
                       Department department, boolean active) {
        this.fullName = fullName;
        this.email = email;
        this.employmentType = type;
        this.baseSalary = baseSalary;
        this.department = department;
        this.active = active;
    }

    public Long getId() { return id; }
    public String getEmployeeNo() { return employeeNo; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getUsername() { return username; }
    public EmploymentType getEmploymentType() { return employmentType; }
    public BigDecimal getBaseSalary() { return baseSalary; }
    public Department getDepartment() { return department; }
    public boolean isActive() { return active; }
    public long getVersion() { return version; }
}
