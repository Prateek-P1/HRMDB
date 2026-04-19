package com.hrms.db.entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * Audit log for Expense Management actions.
 */
@Entity
@Table(name = "expense_audit_log")
public class ExpenseAuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id")
    private Long logId;

    @Column(name = "claim_id", length = 36)
    private String claimId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emp_id")
    private Employee employee;

    @Column(name = "action_type", length = 50)
    private String actionType;

    @Column(name = "details", columnDefinition = "TEXT")
    private String details;

    @Column(name = "ts")
    private LocalDateTime ts;

    @Column(name = "exception_name", length = 200)
    private String exceptionName;

    @PrePersist
    protected void onCreate() {
        if (this.ts == null) this.ts = LocalDateTime.now();
    }

    public Long getLogId() { return logId; }
    public void setLogId(Long logId) { this.logId = logId; }

    public String getClaimId() { return claimId; }
    public void setClaimId(String claimId) { this.claimId = claimId; }

    public Employee getEmployee() { return employee; }
    public void setEmployee(Employee employee) { this.employee = employee; }

    public String getActionType() { return actionType; }
    public void setActionType(String actionType) { this.actionType = actionType; }

    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }

    public LocalDateTime getTs() { return ts; }
    public void setTs(LocalDateTime ts) { this.ts = ts; }

    public String getExceptionName() { return exceptionName; }
    public void setExceptionName(String exceptionName) { this.exceptionName = exceptionName; }
}
