package com.hrms.db.repositories.employee_self_service.repository.interfaces;

import com.hrms.db.repositories.employee_self_service.model.Document;

import java.util.List;

public interface IDocumentRepository {

    boolean uploadDocument(Document document);

    Document getDocumentById(int documentId);

    List<Document> getDocumentsByEmployee(int employeeId);

    boolean deleteDocument(int documentId);

    boolean updateDocumentStatus(int documentId, String status);
}
