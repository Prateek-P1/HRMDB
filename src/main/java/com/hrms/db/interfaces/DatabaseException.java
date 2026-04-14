package com.hrms.db.interfaces;

/**
 * DatabaseException — the standard checked exception thrown by all repository implementations.
 *
 * DESIGN RATIONALE:
 *   Instead of letting raw Hibernate / SQL exceptions leak out to other subsystems,
 *   every repository wraps them in this type. This means subsystem teams only need
 *   to catch *one* exception type and they get a meaningful message + the original cause.
 *
 * SOLID: Open/Closed — subclasses can extend without changing caller code.
 * GRASP: Information Expert — the repository that throws knows the context best.
 */
public class DatabaseException extends RuntimeException {

    /** Which repository/operation threw this (e.g. "PayrollRepository.fetchEmployeeData"). */
    private final String operation;

    /**
     * Create a DatabaseException with operation context and a cause.
     *
     * @param operation  short description of what was being done (used in logs and GUI)
     * @param message    human-readable description of the failure
     * @param cause      the original exception (Hibernate, SQL, NPE, etc.)
     */
    public DatabaseException(String operation, String message, Throwable cause) {
        super("[DB:" + operation + "] " + message, cause);
        this.operation = operation;
    }

    /**
     * Create a DatabaseException with operation context but no caught cause.
     * Use this for logic-level failures (e.g. "employee not found").
     *
     * @param operation  short description of what was being done
     * @param message    human-readable description of the failure
     */
    public DatabaseException(String operation, String message) {
        super("[DB:" + operation + "] " + message);
        this.operation = operation;
    }

    /** Returns the operation string set at construction time. */
    public String getOperation() {
        return operation;
    }
}
