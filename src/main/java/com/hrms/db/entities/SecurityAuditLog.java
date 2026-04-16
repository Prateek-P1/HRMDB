package com.hrms.db.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Security audit logs — tracks login attempts, permission changes, and DB errors.
 * Required by: Data Security team and the HRMS DB Admin GUI.
 *
 * Extended fields (operation, outcome, actionType) are used by DatabaseErrorLogger
 * to record repository-level failures for the GUI Error Log panel.
 */
@Entity
@Table(name = "security_audit_logs")
public class SecurityAuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id")
    private Long logId;

    @Column(name = "user_id", length = 20)
    private String userId;

    /** High-level action category, e.g. "LOGIN", "DB_ERROR", "PERMISSION_CHANGE". */
    @Column(name = "action_type", length = 50)
    private String actionType;

    /** Legacy 'action' column kept for Data Security team compatibility. */
    @Column(name = "action", length = 100)
    private String action;

    /** The specific repository operation that triggered this log entry. */
    @Column(name = "operation", length = 200)
    private String operation;

    /** Outcome: "SUCCESS", "WARNING", "ERROR", "CRITICAL". */
    @Column(name = "outcome", length = 20)
    private String outcome;

    @Column(name = "details", columnDefinition = "TEXT")
    private String details;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "timestamp")
    private LocalDateTime timestamp;

    @PrePersist
    protected void onCreate() {
        if (this.timestamp == null) {
            this.timestamp = LocalDateTime.now();
        }
    }

    // --- Getters & Setters ---

    public Long getLogId()  { return logId; }
    // Note: logId is auto-generated; do not expose a setter.

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getActionType() { return actionType; }
    public void setActionType(String actionType) { this.actionType = actionType; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public String getOperation() { return operation; }
    public void setOperation(String operation) { this.operation = operation; }

    public String getOutcome() { return outcome; }
    public void setOutcome(String outcome) { this.outcome = outcome; }

    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
