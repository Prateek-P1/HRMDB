package com.hrms.db.logging;

/**
 * LogHandler — abstract base for a Chain of Responsibility logging pipeline.
 *
 * This is the *operational* logging chain (what the DB team logs for their own
 * records), distinct from the error-handler chain.
 *
 * Current chain: ConsoleLogHandler → DatabaseLogHandler
 *
 * HOW TO USE:
 *   LogHandler chain = new ConsoleLogHandler(new DatabaseLogHandler(null));
 *   chain.log(LogLevel.INFO, "PayrollRepository", "fetchEmployeeData", "Fetched EMP001");
 */
public abstract class LogHandler {

    public enum LogLevel { DEBUG, INFO, WARN, ERROR }

    private LogHandler next;

    public LogHandler(LogHandler next) {
        this.next = next;
    }

    /**
     * Log a message. Subclasses implement their own sink, then call passToNext().
     *
     * @param level      severity / verbosity level
     * @param repository name of the repository class
     * @param method     name of the method being executed
     * @param message    free-form log message
     */
    public abstract void log(LogLevel level, String repository, String method, String message);

    protected void passToNext(LogLevel level, String repository, String method, String message) {
        if (next != null) {
            next.log(level, repository, method, message);
        }
    }
}
