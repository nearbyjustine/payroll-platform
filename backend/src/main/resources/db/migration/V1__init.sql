-- Flyway migration V1: the initial schema. Never edit a migration after it has run; add V2, V3, ...

CREATE TABLE department (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE employee (
    id              BIGSERIAL PRIMARY KEY,
    employee_no     VARCHAR(20)  NOT NULL UNIQUE,
    full_name       VARCHAR(150) NOT NULL,
    email           VARCHAR(150) NOT NULL UNIQUE,
    username        VARCHAR(50)  UNIQUE,                 -- Keycloak login, NULL if no self-service access
    employment_type VARCHAR(20)  NOT NULL CHECK (employment_type IN ('REGULAR', 'CONTRACTUAL')),
    base_salary     NUMERIC(12,2) NOT NULL CHECK (base_salary > 0),
    department_id   BIGINT NOT NULL REFERENCES department(id),
    active          BOOLEAN NOT NULL DEFAULT TRUE,
    version         BIGINT NOT NULL DEFAULT 0,          -- optimistic locking
    created_at      TIMESTAMPTZ NOT NULL,
    created_by      VARCHAR(50),
    updated_at      TIMESTAMPTZ NOT NULL,
    updated_by      VARCHAR(50)
);
CREATE INDEX idx_employee_department ON employee(department_id);
CREATE INDEX idx_employee_full_name_lower ON employee(LOWER(full_name));

CREATE TABLE payroll_run (
    id               BIGSERIAL PRIMARY KEY,
    period           VARCHAR(7) NOT NULL UNIQUE,          -- 'YYYY-MM': one run per month, enforced by the DB
    status           VARCHAR(20) NOT NULL CHECK (status IN ('PENDING', 'PROCESSING', 'COMPLETED', 'FAILED')),
    employee_count   INT NOT NULL DEFAULT 0,
    processed_count  INT NOT NULL DEFAULT 0,
    total_gross      NUMERIC(14,2) NOT NULL DEFAULT 0,
    total_deductions NUMERIC(14,2) NOT NULL DEFAULT 0,
    total_net        NUMERIC(14,2) NOT NULL DEFAULT 0,
    failure_reason   VARCHAR(500),
    requested_by     VARCHAR(50) NOT NULL,
    requested_at     TIMESTAMPTZ NOT NULL,
    completed_at     TIMESTAMPTZ,
    version          BIGINT NOT NULL DEFAULT 0
);

CREATE TABLE payslip (
    id               BIGSERIAL PRIMARY KEY,
    payroll_run_id   BIGINT NOT NULL REFERENCES payroll_run(id),
    employee_id      BIGINT NOT NULL REFERENCES employee(id),
    gross            NUMERIC(12,2) NOT NULL,
    total_deductions NUMERIC(12,2) NOT NULL,
    net              NUMERIC(12,2) NOT NULL,
    pdf_key          VARCHAR(255),
    created_at       TIMESTAMPTZ NOT NULL,
    CONSTRAINT uq_payslip_run_employee UNIQUE (payroll_run_id, employee_id)   -- makes reprocessing idempotent
);
CREATE INDEX idx_payslip_employee ON payslip(employee_id);

CREATE TABLE payslip_line (
    id          BIGSERIAL PRIMARY KEY,
    payslip_id  BIGINT NOT NULL REFERENCES payslip(id) ON DELETE CASCADE,
    code        VARCHAR(20) NOT NULL,
    label       VARCHAR(100) NOT NULL,
    amount      NUMERIC(12,2) NOT NULL
);
CREATE INDEX idx_payslip_line_payslip ON payslip_line(payslip_id);
