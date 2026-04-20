package com.hrms.db.repositories.Expense_Management;

import com.pesu.expensesubsystem.entity.AuditLog;
import com.pesu.expensesubsystem.enums.ActionType;
import java.time.LocalDateTime;
import java.util.List;

public interface AuditRepository {
    AuditLog save(AuditLog auditLog);
    List<AuditLog> findByEmployeeId(String employeeId);
    List<AuditLog> findByClaimId(Long claimId);
    List<AuditLog> findByAction(ActionType action);
    List<AuditLog> findByDateRange(LocalDateTime from, LocalDateTime to);
    List<AuditLog> findAll();
}
