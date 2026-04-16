package com.hrms.db.handlers;

import com.hrms.db.interfaces.DatabaseException;

/**
 * ErrorHandler — abstract base for a Chain of Responsibility error pipeline.
 *
 * PATTERN: Chain of Responsibility (GoF)
 *   Each handler decides whether to handle an error and/or pass it along the chain.
 *   This allows us to compose: ConsoleErrorLogger → DatabaseErrorLogger → CriticalErrorEscalator
 *   without any handler needing to know about the others.
 *
 * HOW TO USE (for your team):
 *   ErrorHandler chain = new ConsoleErrorLogger(
 *                            new DatabaseErrorLogger(
 *                                new CriticalErrorEscalator(null)));
 *   chain.handle("PayrollRepo", ex, ErrorLevel.CRITICAL);
 *
 * SOLID: Open/Closed — add new handlers without touching existing ones.
 * SOLID: Single Responsibility — each handler does exactly one thing.
 */
public abstract class ErrorHandler {

    /** Severity levels. Order matters — GUI and escalation use this. */
    public enum ErrorLevel {
        INFO,     // Informational, no action needed
        WARNING,  // Something unexpected but recoverable
        ERROR,    // A repository call failed — caller got an exception
        CRITICAL  // Data-at-risk failure — escalate immediately (e.g. save failed)
    }

    /** Next handler in the chain. May be null (end of chain). */
    private ErrorHandler next;

    /** Constructor sets the next handler. Pass null if this is the last link. */
    public ErrorHandler(ErrorHandler next) {
        this.next = next;
    }

    /**
     * Handle an error. Subclasses implement their own logic, then call passToNext().
     *
     * @param operation  the repository / method that generated the error
     * @param ex         the exception (may be null for non-exception errors)
     * @param level      severity level
     */
    public abstract void handle(String operation, Exception ex, ErrorLevel level);

    /**
     * Passes the error to the next handler in the chain (if one exists).
     * Subclasses MUST call this at the end of handle() unless they intentionally stop the chain.
     */
    protected void passToNext(String operation, Exception ex, ErrorLevel level) {
        if (next != null) {
            next.handle(operation, ex, level);
        }
    }

    /** Convenience — handle a DatabaseException directly. */
    public void handle(DatabaseException ex, ErrorLevel level) {
        handle(ex.getOperation(), ex, level);
    }
}
