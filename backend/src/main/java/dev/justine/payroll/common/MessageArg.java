package dev.justine.payroll.common;

/** A message argument that is itself a message key, e.g. "entity.employee" -> "Employee" / "Empleyado". */
public record MessageArg(String key) {}
