package com.hrms.db.entities;

import jakarta.persistence.*;

/**
 * Overtime records — tracks approved/pending overtime hours.
 * Required by: Time Tracking subsystem.
 */
@Entity
@Table(name = "overtime_records")
public class OvertimeRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "overtime_id")
    private Long overtimeId;

    @Column(name = "emp_id", nullable = false, length = 20)
    private String empId;

    @Column(name = "entry_id")
    private Long entryId;

    @Column(name = "overtime_hours")
    private Double overtimeHours;

    @Column(name = "approval_status", length = 20)
    private String approvalStatus = "PENDING"; // PENDING, APPROVED, REJECTED

    // --- Getters & Setters ---

    public Long getOvertimeId() { return overtimeId; }
    public void setOvertimeId(Long overtimeId) { this.overtimeId = overtimeId; }

    public String getEmpId() { return empId; }
    public void setEmpId(String empId) { this.empId = empId; }

    public Long getEntryId() { return entryId; }
    public void setEntryId(Long entryId) { this.entryId = entryId; }

    public Double getOvertimeHours() { return overtimeHours; }
    public void setOvertimeHours(Double overtimeHours) { this.overtimeHours = overtimeHours; }

    public String getApprovalStatus() { return approvalStatus; }
    public void setApprovalStatus(String approvalStatus) { this.approvalStatus = approvalStatus; }
}
