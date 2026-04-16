package com.hrms.db.handlers;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * ConsoleErrorLogger — first link in the error-handler chain.
 *
 * Prints all errors to stderr with a timestamp, severity tag, and operation name.
 * Always passes the error to the next handler.
 *
 * Output format:
 *   [2025-06-01 14:23:01] [ERROR ] [PayrollRepository.fetchEmployeeData] Employee EMP999 not found
 */
public class ConsoleErrorLogger extends ErrorHandler {

    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /** Pad level names so columns align in the console. */
    private static final int LEVEL_WIDTH = 8;

    public ConsoleErrorLogger(ErrorHandler next) {
        super(next);
    }

    @Override
    public void handle(String operation, Exception ex, ErrorLevel level) {
        String timestamp = LocalDateTime.now().format(FMT);
        String levelTag  = padRight("[" + level.name() + "]", LEVEL_WIDTH + 2);
        String msg       = (ex != null) ? ex.getMessage() : "(no exception)";

        System.err.printf("[%s] %s [%s] %s%n", timestamp, levelTag, operation, msg);

        // For ERROR and above, also print the stack trace to help during development
        if (ex != null && level.ordinal() >= ErrorLevel.ERROR.ordinal()) {
            ex.printStackTrace(System.err);
        }

        passToNext(operation, ex, level);
    }

    private static String padRight(String s, int width) {
        return String.format("%-" + width + "s", s);
    }
}
