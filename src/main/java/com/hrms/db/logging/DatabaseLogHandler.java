package com.hrms.db.logging;

import com.hrms.db.config.DatabaseConnection;
import com.hrms.db.entities.SecurityAuditLog;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.time.LocalDateTime;

/**
 * DatabaseLogHandler — persists WARN and ERROR log entries to security_audit_log.
 *
 * DEBUG and INFO are too chatty for the DB — they go to console only.
 * WARN and ERROR are stored so the GUI Error Log tab can surface them.
 */
public class DatabaseLogHandler extends LogHandler {

    public DatabaseLogHandler(LogHandler next) {
        super(next);
    }

    @Override
    public void log(LogLevel level, String repository, String method, String message) {
        if (level.ordinal() >= LogLevel.WARN.ordinal()) {
            persistEntry(level, repository, method, message);
        }
        passToNext(level, repository, method, message);
    }

    private void persistEntry(LogLevel level, String repository, String method, String message) {
        Transaction tx = null;
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            SecurityAuditLog entry = new SecurityAuditLog();
            entry.setActionType("DB_LOG");
            entry.setAction("DB_LOG");
            entry.setOperation(repository + "." + method);
            entry.setOutcome(level.name());
            entry.setDetails(message);
            entry.setTimestamp(LocalDateTime.now());

            session.persist(entry);
            tx.commit();

        } catch (Exception e) {
            if (tx != null) {
                try { tx.rollback(); } catch (Exception r) { /* ignore */ }
            }
            System.err.println("[DatabaseLogHandler] Could not persist log: " + e.getMessage());
        }
    }
}
