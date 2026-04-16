package com.hrms.db.repositories.payroll;

/**
 * PayrollDataPackage — master container for all data required to start payroll calculations.
 *
 * Returned by IPayrollRepository.fetchEmployeeData().
 * The Payroll Subsystem unpacks the four inner packages and feeds them to its calculators.
 *
 * NOTE: All inner DTO classes use public fields (no getters/setters) for simplicity
 * and to match the Payroll team's original design decision.
 */
public class PayrollDataPackage {

    /** The pay period this data package covers (YYYY-MM format, e.g. "2025-06"). */
    public String payPeriod;

    /** Group 1: Basic employee identity and salary grade. */
    public EmployeeDTO employee;

    /** Group 2: Attendance, leave, and hours — used by LossOfPayTracker. */
    public AttendanceDTO attendance;

    /** Group 3: Claims and investments — used by Reimbursement/Bonus calculators. */
    public FinancialsDTO financials;

    /** Group 4: Region/tax context — used by IncomeTaxTDS calculator. */
    public TaxContextDTO tax;
}