package com.hrms.db.handlers;

import com.hrms.db.config.DatabaseConnection;
import com.hrms.db.entities.SecurityAuditLog;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.time.LocalDateTime;

/**
 * DatabaseErrorLogger — second link in the error-handler chain.
 *
 * Persists ERROR and CRITICAL events to the security_audit_log table so the
 * GUI's Error Log tab can display them. INFO and WARNING events are skipped
 * (they are console-only) to keep the DB clean.
 *
 * NOTE: This handler uses its own fresh Hibernate session, completely separate
 * from the session that failed. This ensures the error log is always written
 * even when the calling transaction has been rolled back.
 */
public class DatabaseErrorLogger extends ErrorHandler {

    public DatabaseErrorLogger(ErrorHandler next) {
        super(next);
    }

    @Override
    public void handle(String operation, Exception ex, ErrorLevel level) {
        // Only persist ERROR-level and above to avoid spam
        if (level.ordinal() >= ErrorLevel.ERROR.ordinal()) {
            persistToDb(operation, ex, level);
        }
        passToNext(operation, ex, level);
    }

    private void persistToDb(String operation, Exception ex, ErrorLevel level) {
        Transaction tx = null;
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            SecurityAuditLog log = new SecurityAuditLog();
            log.setActionType("DB_ERROR");
            log.setAction("DB_ERROR");          // legacy field
            log.setOperation(operation);
            log.setOutcome(level.name());
            log.setDetails(ex != null ? truncate(ex.getMessage(), 1000) : "No exception info.");
            log.setTimestamp(LocalDateTime.now());

            session.persist(log);
            tx.commit();

        } catch (Exception writeEx) {
            // Cannot write the error log — at least print to stderr.
            // DO NOT call passToNext here — that would cause infinite recursion.
            if (tx != null) {
                try { tx.rollback(); } catch (Exception rb) { /* ignore */ }
            }
            System.err.println("[DatabaseErrorLogger] FAILED to persist error log: "
                    + writeEx.getMessage());
        }
    }

    private static String truncate(String s, int maxLen) {
        if (s == null) return "";
        return s.length() <= maxLen ? s : s.substring(0, maxLen) + "...";
    }
}
