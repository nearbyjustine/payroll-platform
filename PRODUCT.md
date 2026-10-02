# Product

<!-- impeccable:product-schema 1 -->

## Platform

web

## Users
Confirmed: design for real daily users first, while it must still demo well in a screen share.
- **HR staff** (role HR) maintain employee records: search, filter by department, create and edit employees.
- **Payroll admins** (role PAYROLL_ADMIN) run a monthly payroll for the whole company, watch it progress, and check totals and individual payslips.
- **Employees** (role EMPLOYEE) open their own payslips and download the PDF.
All work happens in a Philippine company (amounts in PHP, Manila months, English and Filipino UI).

## Product Purpose
An HR and payroll system modelled on the Centhris Core HR + Payroll modules: keep employee records correct, run monthly payroll reliably (asynchronous, chunked, idempotent), and give employees self-service access to their payslips. Success: HR and payroll staff trust the numbers and finish routine work quickly; employees get their payslip without asking anyone.

## Positioning
Itemised, explainable payroll: every payslip shows each deduction line (SSS, PhilHealth, Pag-IBIG, withholding tax) computed by pluggable rules, and a payroll run is a visible, auditable job with progress and totals, not a black box.

## Operating Context
Monthly payroll cut-off rhythm; office desktop use for HR/payroll staff; employees may open payslips on phones. Sign-in through Keycloak (OIDC). Payslip PDFs are stored in S3 and downloaded through short-lived links.

## Capabilities and Constraints
- Employees: paged search by name/number, department filter, create, edit with optimistic locking (409 on stale edits), active/inactive.
- Payroll runs: one per month (duplicate = 409), starts as 202 and processes in the background; UI polls status, progress (processed/total) and totals; failures carry a reason.
- Payslips: itemised lines, net pay, PDF download.
- Errors are ProblemDetail with field errors and a correlation ID that must stay visible to users for support.
- Locales: English, Filipino. Money: two decimals, PHP. Rates are simplified for learning (must remain disclosed).
- Stack: Vue 3 + TypeScript + Vite; adding Tailwind CSS and Motion for Vue (confirmed by user request).

## Brand Commitments
Product name "Payroll". No logo or brand assets exist.

## Evidence on Hand
Synthetic demo data only (60 seeded employees, demo users ana/hana/paolo). No customers, testimonials or real company data; none may be invented.

## Product Principles
- Numbers are the interface: amounts, deductions and totals are always visible and precisely aligned, never hidden behind clicks.
- State is honest: a run's status, progress and failures are shown as they are.
- Role-shaped: each role sees only the work that is theirs.
- Motion explains change (a run progressing, a row arriving), never decorates.

## Accessibility & Inclusion
Must support light and dark mode (confirmed). Keyboard-operable forms and tables; respect reduced-motion preferences; bilingual EN/FIL text lengths.
