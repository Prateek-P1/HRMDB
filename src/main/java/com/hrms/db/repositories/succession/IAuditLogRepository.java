package com.hrms.db.repositories.succession;

import com.hrms.db.entities.SuccessionAuditLog;

import java.time.LocalDateTime;
import java.util.List;

public interface IAuditLogRepository {
    SuccessionAuditLog save(SuccessionAuditLog log);
    List<SuccessionAuditLog> findByEntityTypeAndEntityId(String entityType, Integer entityId);
    List<SuccessionAuditLog> findByDateRange(LocalDateTime from, LocalDateTime to);
    List<SuccessionAuditLog> findByDateRange(LocalDateTime from, LocalDateTime to, int offset, int limit);
}
