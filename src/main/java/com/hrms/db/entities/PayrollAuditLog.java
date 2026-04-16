package com.hrms.db.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Audit log for payroll processing — errors and actions.
 */
@Entity
@Table(name = "payroll_audit_log")
public class PayrollAuditLog {

    @Id
    @Column(name = "log_id", length = 36)
    private String logId;

    @Column(name = "batch_id", nullable = false, length = 50)
    private String batchId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emp_id")
    private Employee employee;

    @Column(name = "action_type", length = 50)
    private String actionType; // CALCULATE, SAVE, ERROR, etc.

    @Column(name = "performed_by", length = 100)
    private String performedBy;

    @Column(name = "error_msg", columnDefinition = "TEXT")
    private String errorMsg;

    @Column(name = "ts")
    private LocalDateTime ts;

    @PrePersist
    protected void onCreate() {
        this.ts = LocalDateTime.now();
    }

    // --- Getters & Setters ---

    public String getLogId() { return logId; }
    public void setLogId(String logId) { this.logId = logId; }

    public String getBatchId() { return batchId; }
    public void setBatchId(String batchId) { this.batchId = batchId; }

    public Employee getEmployee() { return employee; }
    public void setEmployee(Employee employee) { this.employee = employee; }

    public String getActionType() { return actionType; }
    public void setActionType(String actionType) { this.actionType = actionType; }

    public String getPerformedBy() { return performedBy; }
    public void setPerformedBy(String performedBy) { this.performedBy = performedBy; }

    public String getErrorMsg() { return errorMsg; }
    public void setErrorMsg(String errorMsg) { this.errorMsg = errorMsg; }

    public LocalDateTime getTs() { return ts; }
}
