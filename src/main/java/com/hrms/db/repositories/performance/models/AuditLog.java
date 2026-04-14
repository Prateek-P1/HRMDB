package com.hrms.db.repositories.performance.models;

import java.util.Date;

public class AuditLog {
    private int logId;
    private int userId;
    private String action;        // CREATE, UPDATE, DELETE, VIEW
    private String entityType;    // Goal, KPI, Appraisal, etc.
    private int entityId;
    private String details;
    private Date timestamp;
    private int cycleId;

    public AuditLog() {}

    public AuditLog(int logId, int userId, String action, String entityType, int entityId, String details, int cycleId) {
        this.logId = logId;
        this.userId = userId;
        this.action = action;
        this.entityType = entityType;
        this.entityId = entityId;
        this.details = details;
        this.cycleId = cycleId;
        this.timestamp = new Date();
    }

    public int getLogId() { return logId; }
    public void setLogId(int logId) { this.logId = logId; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public String getEntityType() { return entityType; }
    public void setEntityType(String entityType) { this.entityType = entityType; }
    public int getEntityId() { return entityId; }
    public void setEntityId(int entityId) { this.entityId = entityId; }
    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }
    public Date getTimestamp() { return timestamp; }
    public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }
    public int getCycleId() { return cycleId; }
    public void setCycleId(int cycleId) { this.cycleId = cycleId; }

    @Override
    public String toString() {
        return "AuditLog{id=" + logId + ", user=" + userId + ", action='" + action + "', entity=" + entityType + "#" + entityId + "}";
    }
}
