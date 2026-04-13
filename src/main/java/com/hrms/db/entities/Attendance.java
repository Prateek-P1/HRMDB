package com.hrms.db.entities;

import jakarta.persistence.*;

/**
 * Attendance and leave hours per pay period.
 * Used by: Payroll, Leave Management, Time Tracking.
 */
@Entity
@Table(name = "attendance", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"emp_id", "pay_period"})
})
public class Attendance {

    @Id
    @Column(name = "record_id", length = 36)
    private String recordId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emp_id", nullable = false)
    private Employee employee;

    @Column(name = "pay_period", nullable = false, length = 10)
    private String payPeriod;

    @Column(name = "working_days_in_month")
    private Integer workingDaysInMonth;

    @Column(name = "leave_with_pay")
    private Integer leaveWithPay = 0;

    @Column(name = "leave_without_pay")
    private Integer leaveWithoutPay = 0;

    @Column(name = "hours_worked")
    private Double hoursWorked = 0.0;

    @Column(name = "overtime_hours")
    private Double overtimeHours = 0.0;

    // --- Getters & Setters ---

    public String getRecordId() { return recordId; }
    public void setRecordId(String recordId) { this.recordId = recordId; }

    public Employee getEmployee() { return employee; }
    public void setEmployee(Employee employee) { this.employee = employee; }

    public String getPayPeriod() { return payPeriod; }
    public void setPayPeriod(String payPeriod) { this.payPeriod = payPeriod; }

    public Integer getWorkingDaysInMonth() { return workingDaysInMonth; }
    public void setWorkingDaysInMonth(Integer workingDaysInMonth) { this.workingDaysInMonth = workingDaysInMonth; }

    public Integer getLeaveWithPay() { return leaveWithPay; }
    public void setLeaveWithPay(Integer leaveWithPay) { this.leaveWithPay = leaveWithPay; }

    public Integer getLeaveWithoutPay() { return leaveWithoutPay; }
    public void setLeaveWithoutPay(Integer leaveWithoutPay) { this.leaveWithoutPay = leaveWithoutPay; }

    public Double getHoursWorked() { return hoursWorked; }
    public void setHoursWorked(Double hoursWorked) { this.hoursWorked = hoursWorked; }

    public Double getOvertimeHours() { return overtimeHours; }
    public void setOvertimeHours(Double overtimeHours) { this.overtimeHours = overtimeHours; }
}
