# App 1: HR & Payroll Platform: Plan

## 1. Goal

A production-shaped HR & Payroll system: HR maintains employees, payroll admins run monthly payroll, and employees download their own payslips. It mirrors the Core HR + Payroll modules of Centhris (RSB Consulting), rebuilt in Java/Spring so every Spring concept has a real use.

**What "production-ready" means here** (definition of done for the whole app):

- [ ] Auth via OIDC (Keycloak) with roles; the API is a stateless OAuth2 resource server
- [ ] PostgreSQL with Flyway migrations (no `ddl-auto` schema generation)
- [ ] Validation, ProblemDetail errors, pagination, consistent DTOs
- [ ] Long-running payroll processing off the request thread, idempotent and observable
- [ ] Files in S3 (LocalStack), downloaded through short-lived pre-signed URLs
- [ ] Unit, slice, and Testcontainers integration tests, all run in CI
- [ ] One-command local stack via Docker Compose
- [ ] Health checks, structured logs with a correlation ID
- [ ] README with architecture, run steps, and decisions

## 2. Architecture

```
 Browser (Vue 3 SPA)
   │  1. login: Authorization Code + PKCE
   ▼
 Keycloak (OIDC provider) ──── issues JWT access token (roles in realm_access.roles)
   │
   │  2. API calls with  Authorization: Bearer <JWT>
   ▼
 Spring Boot API (resource server, validates JWT against Keycloak JWKS)
   ├── employee module   (Department, Employee)           ──► PostgreSQL
   ├── payroll module    (PayrollRun, Payslip, rules)     ──► PostgreSQL
   │      └─ PayrollRunRequested event ─(after commit)─► @Async PayrollProcessor
   │                                                        ├─ DeductionEngine (Strategy)
   │                                                        ├─ PayslipPdfRenderer (OpenPDF)
   │                                                        └─ PayslipStorage ─► S3 (LocalStack)
   └── common            (errors, auditing, correlation id, i18n messages)
```

Style: a **modular monolith**. Packages are split by business module (`employee`, `payroll`), not by layer, and modules talk through services and events. See handbook ch. 12 for why this beats microservices at this size.

## 3. Domain model

| Entity | Key fields | Notes |
|---|---|---|
| `Department` | id, name (unique) | |
| `Employee` | id, employeeNo (unique), fullName, email (unique), username (Keycloak login, unique), type `REGULAR/CONTRACTUAL`, baseSalary `NUMERIC(12,2)`, department, active, version | `@Version` optimistic locking; audit columns |
| `PayrollRun` | id, period `YYYY-MM` (unique), status `PENDING→PROCESSING→COMPLETED/FAILED`, employeeCount, totalGross, totalDeductions, totalNet, failureReason, requestedBy, timestamps | one run per period, enforced by a unique constraint |
| `Payslip` | id, run, employee, gross, totalDeductions, net, pdfKey | unique (run, employee) |
| `PayslipLine` | id, payslip, code (`SSS`, `PHILHEALTH`, `PAGIBIG`, `WTAX`), label, amount | itemised deductions |

Money is always `BigDecimal` and `NUMERIC(12,2)`, rounded `HALF_UP`. The rule rates are **simplified for learning, not legal advice**.

## 4. API

| Method & path | Role | Result |
|---|---|---|
| `GET /api/departments` | HR, PAYROLL_ADMIN | list (cached) |
| `GET /api/employees?q=&department=&page=&size=&sort=` | HR, PAYROLL_ADMIN | paged list |
| `GET /api/employees/{id}` | HR, PAYROLL_ADMIN | 200 / 404 |
| `POST /api/employees` | HR | 201 + Location / 400 / 409 duplicate |
| `PUT /api/employees/{id}` | HR | 200 / 409 version conflict |
| `POST /api/payroll-runs` `{period}` | PAYROLL_ADMIN | **202 Accepted** + Location; 409 if the period already exists |
| `GET /api/payroll-runs` / `{id}` | PAYROLL_ADMIN | status + totals (the UI polls this) |
| `GET /api/payroll-runs/{id}/payslips` | PAYROLL_ADMIN | payslips of a run |
| `GET /api/me` | any signed-in user | profile + roles |
| `GET /api/me/payslips` | EMPLOYEE | own payslips |
| `GET /api/payslips/{id}/download-url` | owner or PAYROLL_ADMIN | `{url, expiresAt}`, a 5-minute pre-signed S3 URL |
| `GET /actuator/health` | public | liveness / readiness |

## 5. Security

- Keycloak realm `payroll`, public client `payroll-web` (PKCE), roles `EMPLOYEE`, `HR`, `PAYROLL_ADMIN`.
- Demo users (password = username + `123`): `ana` (EMPLOYEE), `hana` (HR + EMPLOYEE), `paolo` (PAYROLL_ADMIN + EMPLOYEE).
- The API maps `realm_access.roles` → `ROLE_*` authorities with a custom JWT converter.
- URL rules live in the `SecurityFilterChain`; ownership rules ("only my payslip") live in the service, since they need the data.

## 6. Milestones & tickets

### M1: Foundation
- **P-01 Project + infra.** Spring Boot 4.1 project; Compose runs Postgres, Keycloak (realm import), and LocalStack (bucket created by an init script). *AC:* `docker compose up` brings up all three healthy.
- **P-02 Schema.** Flyway `V1__init.sql` creates all tables, constraints, and indexes. *AC:* the app boots against an empty DB; `ddl-auto=validate` passes.
- **P-03 Errors & conventions.** `ApiExceptionHandler` (ProblemDetail), i18n messages (en, fil), correlation ID filter + MDC. *AC:* every error response has `type/title/status/detail` and a `correlationId`.

### M2: Core HR
- **P-04 Departments & employees CRUD.** DTO records, validation, search plus pagination via a JPA `Specification`. *AC:* create returns 201; a duplicate email returns 409; a stale `version` returns 409.
- **P-05 Auditing.** `createdAt/updatedAt/createdBy/updatedBy`, with the current user taken from the JWT. *AC:* columns are filled with the Keycloak username.

### M3: Payroll
- **P-06 Deduction rules.** `DeductionRule` Strategy with SSS, PhilHealth, Pag-IBIG, and withholding tax; `DeductionEngine` builds itemised lines. *AC:* parameterized tests at each bracket boundary.
- **P-07 Payroll run lifecycle.** POST creates a PENDING run → after commit, the async processor computes payslips in chunks of 50 (each chunk in its own transaction) → COMPLETED with totals, or FAILED with a reason. *AC:* 202 on start; a duplicate period gives 409; the status endpoint shows progress; a failure leaves no half-finished run marked as completed.
- **P-08 Payslip PDFs on S3.** Render with OpenPDF, upload to `payslips/{period}/{employeeNo}.pdf`, store the key. *AC:* the object exists in the LocalStack bucket.
- **P-09 Self-service download.** Pre-signed URL that expires in 5 minutes; ownership check. *AC:* `ana` can't fetch `hana`'s payslip (403).

### M4: Frontend (Vue 3)
- **P-10 Shell + auth.** Vite + TS + Pinia + router + vue-i18n; oidc-client-ts login with PKCE; route guards by role; an axios interceptor adds the bearer token.
- **P-11 Employees UI.** Searchable, paged table; create/edit form showing server field errors.
- **P-12 Payroll UI.** Start a run, poll its status, show totals and payslips.
- **P-13 My payslips.** List and download; EN/Filipino language switcher.

### M5: Hardening
- **P-14 Tests.** Unit (rules, engine, services with Mockito), `@WebMvcTest` + JWT, `@DataJpaTest` on Testcontainers Postgres, full integration with Postgres + LocalStack containers.
- **P-15 Containers & CI.** Multi-stage Dockerfiles (API, web with nginx), a full `docker compose` profile, and a GitHub Actions workflow (build, test, image build).
- **P-16 Docs.** README, architecture diagram, ADRs → handbook.

## 7. Out of scope (and what we'd do in a real deployment)

| Not built | Production approach |
|---|---|
| Real government contribution tables | Rate tables in the DB, versioned by effective date |
| Email payslip notifications | SES (or SNS → email), via the outbox pattern |
| Multi-instance scheduling | ShedLock / a queue so only one node processes a run |
| Real AWS deployment | ECS Fargate or Elastic Beanstalk + RDS + S3 + Secrets Manager; Terraform/CDK |
