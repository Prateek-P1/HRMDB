package com.hrms.db.repositories.payroll;

/**
 * EmployeeDTO — basic identity and salary-grade data.
 * Maps to fields from the 'employees' table.
 * Part of PayrollDataPackage (Group 1).
 */
public class EmployeeDTO {
    public String empID;
    public String name;
    public String department;
    public String gradeLevel;
    public double basicPay;
    public int    yearsOfService;
}
