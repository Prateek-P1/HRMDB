package com.hrms.db.repositories.docu_management;

import java.util.List;

public interface DocumentRepository {

    void save(Document doc);

    List<Document> findByEmployeeId(String empId);

    List<Document> findByEmployeeIdAndName(String empId, String name);

    List<Document> findAll();

    Document findById(String id);
}
