package com.hrms.db.repositories.employee_self_service.repository.impl;

import com.hrms.db.config.DatabaseConnection;
import com.hrms.db.entities.Employee;
import com.hrms.db.repositories.employee_self_service.model.Document;
import com.hrms.db.repositories.employee_self_service.model.DocumentType;
import com.hrms.db.repositories.employee_self_service.repository.interfaces.IDocumentRepository;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class DocumentRepositoryImpl implements IDocumentRepository {

    public DocumentRepositoryImpl() {
        EmployeeSelfServiceSchema.ensureTables();
    }

    @Override
    public boolean uploadDocument(Document document) {
        if (document == null || document.getDocumentType() == null || isBlank(document.getFileUrl())) {
            return false;
        }

        EmployeeSelfServiceSchema.ensureTables();

        Transaction tx = null;
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            String empCode = getEmployeeCodeByEssId(session, document.getEmployeeId());
            if (empCode == null) {
                tx.commit();
                return false;
            }

            Employee employee = session.get(Employee.class, empCode);
            if (employee == null) {
                tx.commit();
                return false;
            }

            com.hrms.db.entities.Document dbDocument = new com.hrms.db.entities.Document();
            String documentCode = UUID.randomUUID().toString();
            dbDocument.setDocumentId(documentCode);
            dbDocument.setEmployee(employee);
            dbDocument.setDocumentType(document.getDocumentType().name());
            dbDocument.setFilePath(document.getFileUrl());
            dbDocument.setUploadDate(parseUploadDate(document.getUploadDate()));
            dbDocument.setVerificationStatus(normalizeStatus(document.getVerificationStatus()));

            session.persist(dbDocument);

            if (document.getDocumentId() > 0) {
                session.createNativeMutationQuery(
                                "INSERT INTO ess_document_map (document_id, document_code) VALUES (:documentId, :documentCode)")
                        .setParameter("documentId", document.getDocumentId())
                        .setParameter("documentCode", documentCode)
                        .executeUpdate();
            } else {
                session.createNativeMutationQuery(
                                "INSERT INTO ess_document_map (document_code) VALUES (:documentCode)")
                        .setParameter("documentCode", documentCode)
                        .executeUpdate();
            }

            Integer createdId = findEssDocumentIdByCode(session, documentCode);
            if (createdId != null) {
                document.setDocumentId(createdId);
            }

            tx.commit();
            return true;
        } catch (Exception ex) {
            rollback(tx);
            return false;
        }
    }

    @Override
    public Document getDocumentById(int documentId) {
        if (documentId <= 0) {
            return null;
        }

        EmployeeSelfServiceSchema.ensureTables();

        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            String documentCode = findDocumentCodeByEssId(session, documentId);
            if (documentCode == null) {
                return null;
            }

            com.hrms.db.entities.Document dbDocument =
                    session.get(com.hrms.db.entities.Document.class, documentCode);
            if (dbDocument == null) {
                return null;
            }

            return toModel(session, dbDocument, documentId);
        } catch (Exception ex) {
            return null;
        }
    }

    @Override
    public List<Document> getDocumentsByEmployee(int employeeId) {
        if (employeeId <= 0) {
            return Collections.emptyList();
        }

        EmployeeSelfServiceSchema.ensureTables();

        Transaction tx = null;
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            String empCode = getEmployeeCodeByEssId(session, employeeId);
            if (empCode == null) {
                tx.commit();
                return Collections.emptyList();
            }

            List<com.hrms.db.entities.Document> dbDocuments = session.createQuery(
                            "FROM Document d WHERE d.employee.empId = :empCode ORDER BY d.uploadDate DESC, d.documentId DESC",
                            com.hrms.db.entities.Document.class)
                    .setParameter("empCode", empCode)
                    .getResultList();

            List<Document> documents = new ArrayList<>();
            for (com.hrms.db.entities.Document dbDocument : dbDocuments) {
                int essDocumentId = getOrCreateEssDocumentId(session, dbDocument.getDocumentId());
                documents.add(toModel(session, dbDocument, essDocumentId));
            }

            tx.commit();
            return documents;
        } catch (Exception ex) {
            rollback(tx);
            return Collections.emptyList();
        }
    }

    @Override
    public boolean deleteDocument(int documentId) {
        if (documentId <= 0) {
            return false;
        }

        EmployeeSelfServiceSchema.ensureTables();

        Transaction tx = null;
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            String documentCode = findDocumentCodeByEssId(session, documentId);
            if (documentCode == null) {
                tx.commit();
                return false;
            }

            com.hrms.db.entities.Document dbDocument =
                    session.get(com.hrms.db.entities.Document.class, documentCode);
            if (dbDocument != null) {
                session.remove(dbDocument);
            }

            int removedMapRows = session.createNativeMutationQuery(
                            "DELETE FROM ess_document_map WHERE document_id = :documentId")
                    .setParameter("documentId", documentId)
                    .executeUpdate();

            tx.commit();
            return dbDocument != null || removedMapRows > 0;
        } catch (Exception ex) {
            rollback(tx);
            return false;
        }
    }

    @Override
    public boolean updateDocumentStatus(int documentId, String status) {
        if (documentId <= 0 || isBlank(status)) {
            return false;
        }

        EmployeeSelfServiceSchema.ensureTables();

        Transaction tx = null;
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            String documentCode = findDocumentCodeByEssId(session, documentId);
            if (documentCode == null) {
                tx.commit();
                return false;
            }

            int updated = session.createNativeMutationQuery(
                            "UPDATE documents SET verification_status = :status WHERE document_id = :documentCode")
                    .setParameter("status", status.trim().toUpperCase(Locale.ROOT))
                    .setParameter("documentCode", documentCode)
                    .executeUpdate();

            tx.commit();
            return updated > 0;
        } catch (Exception ex) {
            rollback(tx);
            return false;
        }
    }

    private Document toModel(Session session, com.hrms.db.entities.Document dbDocument, int documentId) {
        Document model = new Document();
        model.setDocumentId(documentId);
        model.setEmployeeId(resolveEssEmployeeId(session, dbDocument));
        model.setDocumentType(toEnumType(dbDocument.getDocumentType()));
        model.setFileUrl(dbDocument.getFilePath());
        model.setUploadDate(dbDocument.getUploadDate() != null ? dbDocument.getUploadDate().toString() : null);
        model.setVerificationStatus(dbDocument.getVerificationStatus());
        return model;
    }

    private int resolveEssEmployeeId(Session session, com.hrms.db.entities.Document dbDocument) {
        if (dbDocument.getEmployee() == null || isBlank(dbDocument.getEmployee().getEmpId())) {
            return -1;
        }

        Object employeeId = session.createNativeQuery(
                        "SELECT employee_id FROM ess_auth_credentials WHERE emp_code = :empCode")
                .setParameter("empCode", dbDocument.getEmployee().getEmpId())
                .setMaxResults(1)
                .uniqueResult();

        if (employeeId != null) {
            return asInt(employeeId);
        }

        try {
            return Integer.parseInt(dbDocument.getEmployee().getEmpId());
        } catch (NumberFormatException ex) {
            return -1;
        }
    }

    private String getEmployeeCodeByEssId(Session session, int employeeId) {
        Object empCode = session.createNativeQuery(
                        "SELECT emp_code FROM ess_auth_credentials WHERE employee_id = :employeeId")
                .setParameter("employeeId", employeeId)
                .setMaxResults(1)
                .uniqueResult();

        if (empCode != null) {
            return empCode.toString();
        }

        String fallbackEmpCode = String.valueOf(employeeId);
        Employee fallbackEmployee = session.get(Employee.class, fallbackEmpCode);
        return fallbackEmployee != null ? fallbackEmpCode : null;
    }

    private String findDocumentCodeByEssId(Session session, int documentId) {
        Object documentCode = session.createNativeQuery(
                        "SELECT document_code FROM ess_document_map WHERE document_id = :documentId")
                .setParameter("documentId", documentId)
                .setMaxResults(1)
                .uniqueResult();
        return documentCode != null ? documentCode.toString() : null;
    }

    private Integer findEssDocumentIdByCode(Session session, String documentCode) {
        Object documentId = session.createNativeQuery(
                        "SELECT document_id FROM ess_document_map WHERE document_code = :documentCode")
                .setParameter("documentCode", documentCode)
                .setMaxResults(1)
                .uniqueResult();
        return documentId != null ? asInt(documentId) : null;
    }

    private int getOrCreateEssDocumentId(Session session, String documentCode) {
        Integer existingId = findEssDocumentIdByCode(session, documentCode);
        if (existingId != null) {
            return existingId;
        }

        session.createNativeMutationQuery(
                        "INSERT OR IGNORE INTO ess_document_map (document_code) VALUES (:documentCode)")
                .setParameter("documentCode", documentCode)
                .executeUpdate();

        Integer createdId = findEssDocumentIdByCode(session, documentCode);
        if (createdId != null) {
            return createdId;
        }

        try {
            return Integer.parseInt(documentCode);
        } catch (NumberFormatException ex) {
            return -1;
        }
    }

    private LocalDate parseUploadDate(String uploadDate) {
        if (isBlank(uploadDate)) {
            return LocalDate.now();
        }
        try {
            return LocalDate.parse(uploadDate);
        } catch (DateTimeParseException ex) {
            return LocalDate.now();
        }
    }

    private DocumentType toEnumType(String rawType) {
        if (isBlank(rawType)) {
            return DocumentType.PDF;
        }
        try {
            return DocumentType.valueOf(rawType.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            return DocumentType.PDF;
        }
    }

    private String normalizeStatus(String status) {
        if (isBlank(status)) {
            return "PENDING";
        }
        return status.trim().toUpperCase(Locale.ROOT);
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private int asInt(Object value) {
        if (value == null) {
            return 0;
        }
        if (value instanceof Number number) {
            return number.intValue();
        }
        try {
            return Integer.parseInt(value.toString());
        } catch (NumberFormatException ex) {
            return 0;
        }
    }

    private void rollback(Transaction tx) {
        if (tx != null) {
            try {
                tx.rollback();
            } catch (Exception ignored) {
                // Ignore rollback errors after the main failure.
            }
        }
    }
}
