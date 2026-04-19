package com.hrms.db.repositories.docu_management;

import com.hrms.db.config.DatabaseConnection;
import com.hrms.db.entities.DocumentAuditLogRecord;
import com.hrms.db.handlers.ConsoleErrorLogger;
import com.hrms.db.handlers.CriticalErrorEscalator;
import com.hrms.db.handlers.DatabaseErrorLogger;
import com.hrms.db.handlers.ErrorHandler;
import com.hrms.db.handlers.ErrorHandler.ErrorLevel;
import com.hrms.db.interfaces.DatabaseException;
import com.hrms.db.logging.ConsoleLogHandler;
import com.hrms.db.logging.DatabaseLogHandler;
import com.hrms.db.logging.LogHandler;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class AuditRepositoryImpl implements AuditRepository {

    private static final String REPO = "AuditRepositoryImpl";

    private final ErrorHandler errorChain = new ConsoleErrorLogger(
            new DatabaseErrorLogger(new CriticalErrorEscalator(null)));
    private final LogHandler log = new ConsoleLogHandler(new DatabaseLogHandler(null));

    @Override
    public void save(AuditLog logEntry) {
        Transaction tx = null;
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            DocumentAuditLogRecord record = new DocumentAuditLogRecord();
            record.setAction(logEntry.getAction());
            record.setDocumentId(logEntry.getDocumentId());
            record.setEmployeeId(logEntry.getEmployeeId());
            record.setPerformedBy(logEntry.getPerformedBy());
            record.setTimestamp(logEntry.getTimestamp() != null ? logEntry.getTimestamp() : LocalDateTime.now());

            session.persist(record);
            tx.commit();

            log.log(LogHandler.LogLevel.INFO, REPO, "save",
                    "Saved audit log for employeeId=" + logEntry.getEmployeeId());
        } catch (Exception ex) {
            rollback(tx);
            handleError("save", ex, ErrorLevel.ERROR);
        }
    }

    @Override
    public List<AuditLog> findAll() {
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            return session.createQuery(
                            "FROM DocumentAuditLogRecord a ORDER BY a.timestamp DESC, a.id DESC",
                            DocumentAuditLogRecord.class)
                    .getResultList()
                    .stream()
                    .map(this::toModel)
                    .collect(Collectors.toList());
        } catch (Exception ex) {
            handleError("findAll", ex, ErrorLevel.ERROR);
            return Collections.emptyList();
        }
    }

    @Override
    public List<AuditLog> findByEmployeeId(String empId) {
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            return session.createQuery(
                            "FROM DocumentAuditLogRecord a WHERE a.employeeId = :empId ORDER BY a.timestamp DESC, a.id DESC",
                            DocumentAuditLogRecord.class)
                    .setParameter("empId", empId)
                    .getResultList()
                    .stream()
                    .map(this::toModel)
                    .collect(Collectors.toList());
        } catch (Exception ex) {
            handleError("findByEmployeeId", ex, ErrorLevel.ERROR);
            return Collections.emptyList();
        }
    }

    private AuditLog toModel(DocumentAuditLogRecord record) {
        AuditLog logEntry = new AuditLog();
        logEntry.setId(record.getId());
        logEntry.setAction(record.getAction());
        logEntry.setDocumentId(record.getDocumentId());
        logEntry.setEmployeeId(record.getEmployeeId());
        logEntry.setPerformedBy(record.getPerformedBy());
        logEntry.setTimestamp(record.getTimestamp());
        return logEntry;
    }

    private void rollback(Transaction tx) {
        if (tx != null) {
            try {
                tx.rollback();
            } catch (Exception ignored) {
                // Ignore rollback failure after the original failure has already been captured.
            }
        }
    }

    private void handleError(String method, Exception ex, ErrorLevel level) {
        errorChain.handle(REPO + "." + method,
                new DatabaseException(REPO + "." + method, ex.getMessage(), ex),
                level);
    }
}
