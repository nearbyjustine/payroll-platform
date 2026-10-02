package dev.justine.payroll.payroll;

/** Domain event: published inside the "request run" transaction, handled after it commits. */
public record PayrollRunRequested(Long runId) {}
