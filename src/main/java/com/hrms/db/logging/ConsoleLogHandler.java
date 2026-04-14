package com.hrms.db.logging;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * ConsoleLogHandler — prints structured log lines to stdout.
 * Format: [2025-06-01 14:23:01] [INFO ] [PayrollRepository.fetchEmployeeData] Fetched EMP001
 */
public class ConsoleLogHandler extends LogHandler {

    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /** Minimum level to actually print. Set to DEBUG to see everything. */
    private final LogLevel minLevel;

    public ConsoleLogHandler(LogHandler next) {
        super(next);
        this.minLevel = LogLevel.INFO; // default — hide DEBUG noise
    }

    public ConsoleLogHandler(LogHandler next, LogLevel minLevel) {
        super(next);
        this.minLevel = minLevel;
    }

    @Override
    public void log(LogLevel level, String repository, String method, String message) {
        if (level.ordinal() >= minLevel.ordinal()) {
            String ts  = LocalDateTime.now().format(FMT);
            String tag = String.format("[%-5s]", level.name());
            System.out.printf("[%s] %s [%s.%s] %s%n", ts, tag, repository, method, message);
        }
        passToNext(level, repository, method, message);
    }
}
