package com.hrms.db.repositories.docu_management;

import com.hrms.db.config.DatabaseConnection;
import com.hrms.db.entities.DocumentMetadataRecord;
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

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class DocumentRepositoryImpl implements DocumentRepository {

    private static final String REPO = "DocumentRepositoryImpl";

    private final ErrorHandler errorChain = new ConsoleErrorLogger(
            new DatabaseErrorLogger(new CriticalErrorEscalator(null)));
    private final LogHandler log = new ConsoleLogHandler(new DatabaseLogHandler(null));

    @Override
    public void save(Document doc) {
        Transaction tx = null;
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            DocumentMetadataRecord record = new DocumentMetadataRecord();
            record.setId(doc.getId() != null ? doc.getId() : UUID.randomUUID().toString());
            record.setEmployeeId(doc.getEmployeeId());
            record.setName(doc.getName());
            record.setFilePath(doc.getFilePath());
            record.setVersion(doc.getVersion() > 0 ? doc.getVersion() : 1);
            record.setCreatedAt(doc.getCreatedAt() > 0 ? doc.getCreatedAt() : System.currentTimeMillis());
            record.setExpiryAt(doc.getExpiryAt());
            record.setType(doc.getType() != null ? doc.getType() : DocumentType.OTHER);

            session.persist(record);
            tx.commit();

            log.log(LogHandler.LogLevel.INFO, REPO, "save",
                    "Saved document metadata for employeeId=" + doc.getEmployeeId());
        } catch (Exception ex) {
            rollback(tx);
            handleError("save", ex, ErrorLevel.ERROR);
        }
    }

    @Override
    public List<Document> findByEmployeeId(String empId) {
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            return session.createQuery(
                            "FROM DocumentMetadataRecord d WHERE d.employeeId = :empId ORDER BY d.createdAt DESC, d.version DESC",
                            DocumentMetadataRecord.class)
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

    @Override
    public List<Document> findByEmployeeIdAndName(String empId, String name) {
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            return session.createQuery(
                            "FROM DocumentMetadataRecord d WHERE d.employeeId = :empId AND d.name = :name " +
                                    "ORDER BY d.version DESC, d.createdAt DESC",
                            DocumentMetadataRecord.class)
                    .setParameter("empId", empId)
                    .setParameter("name", name)
                    .getResultList()
                    .stream()
                    .map(this::toModel)
                    .collect(Collectors.toList());
        } catch (Exception ex) {
            handleError("findByEmployeeIdAndName", ex, ErrorLevel.ERROR);
            return Collections.emptyList();
        }
    }

    @Override
    public List<Document> findAll() {
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            return session.createQuery(
                            "FROM DocumentMetadataRecord d ORDER BY d.createdAt DESC, d.version DESC",
                            DocumentMetadataRecord.class)
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
    public Document findById(String id) {
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            DocumentMetadataRecord record = session.get(DocumentMetadataRecord.class, id);
            return record != null ? toModel(record) : null;
        } catch (Exception ex) {
            handleError("findById", ex, ErrorLevel.ERROR);
            return null;
        }
    }

    private Document toModel(DocumentMetadataRecord record) {
        Document document = new Document();
        document.setId(record.getId());
        document.setEmployeeId(record.getEmployeeId());
        document.setName(record.getName());
        document.setFilePath(record.getFilePath());
        document.setVersion(record.getVersion());
        document.setCreatedAt(record.getCreatedAt());
        document.setExpiryAt(record.getExpiryAt());
        document.setType(record.getType());
        return document;
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
