package com.hrms.db.repositories.benefits;

import com.hrms.db.entities.SecurityAuditLog;

import java.util.List;

/** DAO contract for benefits-related audit events stored in {@link SecurityAuditLog}. */
public interface AuditLogDAO {

    SecurityAuditLog save(SecurityAuditLog log);

    SecurityAuditLog findById(Long logId);

    List<SecurityAuditLog> findAll();

    List<SecurityAuditLog> findByEmployeeId(String employeeId);

    List<SecurityAuditLog> findByActionType(String actionType);
}
