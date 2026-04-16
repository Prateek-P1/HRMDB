package com.hrms.db.repositories.Leave_Management_Subsytem;

public interface IAuditLogRepository {

    void writeAuditLog(String actorId, String action, String entity, String details);
}