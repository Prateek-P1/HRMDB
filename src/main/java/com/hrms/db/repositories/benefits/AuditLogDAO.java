package com.hrms.db.repositories.benefits;

import com.hrms.db.entities.SecurityAuditLog;

import java.util.List;

/** Benefits audit log access. Backed by {@link SecurityAuditLog}. */
public interface AuditLogDAO {
    SecurityAuditLog save(SecurityAuditLog log);
    SecurityAuditLog findById(Long logId);
    List<SecurityAuditLog> findAll();
    List<SecurityAuditLog> findByEmployeeId(String employeeId);
    List<SecurityAuditLog> findByActionType(String actionType);
}
