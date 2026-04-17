package com.hrms.db.repositories.security;

import com.hrms.db.repositories.security.models.AuditEntry;

import java.time.LocalDateTime;
import java.util.List;

public interface IAuditService {
    void logAccess(String userId, String resource, String action, String status, LocalDateTime timestamp);

    List<AuditEntry> getAuditLogs(LocalDateTime fromDate, LocalDateTime toDate, String userId);
}
