package dev.justine.payroll.config;

/** Realm roles defined in Keycloak (realm "payroll"). */
public final class Roles {
    private Roles() {}
    public static final String EMPLOYEE = "EMPLOYEE";
    public static final String HR = "HR";
    public static final String PAYROLL_ADMIN = "PAYROLL_ADMIN";
}
