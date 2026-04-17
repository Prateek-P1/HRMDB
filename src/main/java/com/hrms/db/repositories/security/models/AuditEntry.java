package com.hrms.db.repositories.security.models;

import java.time.LocalDateTime;

public class AuditEntry {
    private String userId;
    private String resource;
    private String action;
    private String status;
    private LocalDateTime timestamp;

    public AuditEntry() {}

    public AuditEntry(String userId, String resource, String action, String status, LocalDateTime timestamp) {
        this.userId = userId;
        this.resource = resource;
        this.action = action;
        this.status = status;
        this.timestamp = timestamp;
    }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getResource() { return resource; }
    public void setResource(String resource) { this.resource = resource; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    @Override
    public String toString() {
        return "AuditEntry{userId='" + userId + "', resource='" + resource + "', action='" + action + "', status='" + status + "', timestamp=" + timestamp + "}";
    }
}
