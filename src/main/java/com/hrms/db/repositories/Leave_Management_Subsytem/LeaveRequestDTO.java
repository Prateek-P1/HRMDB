package com.hrms.db.repositories.Leave_Management_Subsytem;

/**
 * DTO: LeaveRequestDTO
 *
 * Team-provided data contract for Leave Management Subsystem.
 */
public class LeaveRequestDTO {
    public String requestId;
    public String empId;
    public String startDate;   // ISO: yyyy-MM-dd
    public String endDate;     // ISO: yyyy-MM-dd
    public String leaveType;   // ANNUAL, SICK, CASUAL, UNPAID
    public String status;      // PENDING, APPROVED, REJECTED
    public String comments;
}
