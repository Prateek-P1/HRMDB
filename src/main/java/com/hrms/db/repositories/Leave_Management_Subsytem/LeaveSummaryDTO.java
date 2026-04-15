package com.hrms.db.repositories.Leave_Management_Subsytem;

/**
 * DTO: LeaveSummaryDTO
 *
 * Team-provided data contract for Payroll sync events.
 */
public class LeaveSummaryDTO {
    public String empId;
    public String period; // e.g. "2026-04"
    public double totalLeaveDaysTaken;
    public double unpaidDays;
    public double paidDays;
}
