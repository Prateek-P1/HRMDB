package com.hrms.db.repositories.Leave_Management_Subsytem;

/**
 * DTO: EmployeeInfoDTO
 *
 * Team-provided data contract for employee data needed by Leave Management.
 */
public class EmployeeInfoDTO {
    public String empId;
    public String name;
    public String email;
    public String department;
    public String managerId;
    public double annualLeaveBalance;
    public double sickLeaveBalance;
    public double casualLeaveBalance;
}
