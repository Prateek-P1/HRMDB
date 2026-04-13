package com.hrms.db.entities;

import jakarta.persistence.*;
import java.time.LocalDate;

/**
 * Leave records — used by Leave Management, Expense, Payroll.
 */
@Entity
@Table(name = "leave_records")
public class LeaveRecord {

    @Id
    @Column(name = "leave_id", length = 36)
    private String leaveId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emp_id", nullable = false)
    private Employee employee;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "leave_type", length = 30)
    private String leaveType; // SICK, CASUAL, EARNED, UNPAID

    @Column(name = "status", length = 20)
    private String status = "PENDING"; // PENDING, APPROVED, REJECTED

    // --- Getters & Setters ---

    public String getLeaveId() { return leaveId; }
    public void setLeaveId(String leaveId) { this.leaveId = leaveId; }

    public Employee getEmployee() { return employee; }
    public void setEmployee(Employee employee) { this.employee = employee; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public String getLeaveType() { return leaveType; }
    public void setLeaveType(String leaveType) { this.leaveType = leaveType; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
