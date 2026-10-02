package dev.justine.payroll.employee;

import jakarta.persistence.*;

@Entity
public class Department {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    protected Department() {}

    public Department(String name) { this.name = name; }

    public Long getId() { return id; }
    public String getName() { return name; }
}
