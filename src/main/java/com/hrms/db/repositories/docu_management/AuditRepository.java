package com.hrms.db.repositories.docu_management;

import java.util.List;

public interface AuditRepository {

    void save(AuditLog log);

    List<AuditLog> findAll();

    List<AuditLog> findByEmployeeId(String empId);
}
