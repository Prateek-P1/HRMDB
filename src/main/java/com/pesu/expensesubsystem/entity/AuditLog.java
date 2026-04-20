package com.pesu.expensesubsystem.entity;

import com.pesu.expensesubsystem.enums.ActionType;

import java.time.LocalDateTime;

/**
 * Expense audit log DTO.
 */
public class AuditLog {

    private Long logId;
    private Long claimId;
    private String employeeId;
    private ActionType action;
    private String details;
    private LocalDateTime timestamp;
    private String exceptionName;

    public AuditLog() {
    }

    public Long getLogId() { return logId; }
    public void setLogId(Long logId) { this.logId = logId; }

    public Long getClaimId() { return claimId; }
    public void setClaimId(Long claimId) { this.claimId = claimId; }

    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }

    public ActionType getAction() { return action; }
    public void setAction(ActionType action) { this.action = action; }

    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public String getExceptionName() { return exceptionName; }
    public void setExceptionName(String exceptionName) { this.exceptionName = exceptionName; }
}
