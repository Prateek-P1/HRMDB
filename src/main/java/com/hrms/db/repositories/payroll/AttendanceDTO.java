package com.hrms.db.repositories.payroll;

/**
 * AttendanceDTO — attendance, leave, and hours data for a pay period.
 * Maps to fields from the 'attendance' table.
 * Part of PayrollDataPackage (Group 2). Used by LossOfPayTracker.
 */
public class AttendanceDTO {
    public int    workingDaysInMonth;
    public int    leaveWithPay;
    public int    leaveWithoutPay;   // "lopDays" — causes salary deduction
    public double hoursWorked;
    public double overtimeHours;
}
