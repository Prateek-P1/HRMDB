package com.hrms.db.repositories.payroll;

/**
 * PayrollResultDTO — the final computed payroll values written back to the DB.
 *
 * The Payroll Subsystem fills this object after running all its calculators
 * and calls IPayrollRepository.savePayrollResult() to persist it.
 *
 * Maps to the 'payroll_results' table via the PayrollResult Hibernate entity.
 */
public class PayrollResultDTO {
    /** Unique employee identifier. */
    public String empID;

    /** Unique record ID for this pay result (Payroll team generates this UUID). */
    public String recordID;

    /** Total gross pay before any deductions. */
    public double finalGrossPay;

    /** Take-home pay after all deductions. */
    public double finalNetPay;

    /** Loss-of-pay deductions (leaveWithoutPay × daily rate). */
    public double penaltyAmount;

    /** Provident Fund contribution deducted. */
    public double pfAmount;

    /** Income Tax / TDS deducted. */
    public double taxDeducted;

    /** Final amount actually disbursed (net + reimbursements). */
    public double payoutAmount;
}