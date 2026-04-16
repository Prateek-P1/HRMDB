package com.hrms.db.repositories.leave;

/**
 * ILeaveAuditLogRepository — provided by Leave Management team.
 * Writes all leave-related user actions to the audit trail.
 */
public interface ILeaveAuditLogRepository {

    void writeAuditLog(String actorId, String action, String entity, String details);
}
