-- Demo data. Usernames match the Keycloak realm users: ana, hana, paolo.
INSERT INTO department (name) VALUES ('Human Resources'), ('Finance'), ('Engineering'), ('Operations');

INSERT INTO employee (employee_no, full_name, email, username, employment_type, base_salary, department_id, created_at, created_by, updated_at, updated_by) VALUES
 ('E-0001', 'Ana Santos',   'ana@demo.ph',   'ana',   'REGULAR',     30000.00, (SELECT id FROM department WHERE name='Engineering'),     now(), 'seed', now(), 'seed'),
 ('E-0002', 'Hana Reyes',   'hana@demo.ph',  'hana',  'REGULAR',     42000.00, (SELECT id FROM department WHERE name='Human Resources'), now(), 'seed', now(), 'seed'),
 ('E-0003', 'Paolo Cruz',   'paolo@demo.ph', 'paolo', 'REGULAR',     55000.00, (SELECT id FROM department WHERE name='Finance'),         now(), 'seed', now(), 'seed');

-- 57 more employees so pagination and chunked payroll processing have something to do
INSERT INTO employee (employee_no, full_name, email, employment_type, base_salary, department_id, created_at, created_by, updated_at, updated_by)
SELECT 'E-' || LPAD((n + 3)::text, 4, '0'),
       'Demo Employee ' || n,
       'employee' || n || '@demo.ph',
       CASE WHEN n % 5 = 0 THEN 'CONTRACTUAL' ELSE 'REGULAR' END,
       18000 + (n * 750),
       (SELECT id FROM department ORDER BY id OFFSET (n % 4) LIMIT 1),
       now(), 'seed', now(), 'seed'
FROM generate_series(1, 57) AS n;
