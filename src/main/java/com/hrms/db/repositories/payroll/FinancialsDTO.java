package com.hrms.db.repositories.payroll;

/**
 * FinancialsDTO — claims and investment declarations for a pay period.
 * Maps to fields from the 'financials' table.
 * Part of PayrollDataPackage (Group 3). Used by Reimbursement/Bonus calculators.
 */
public class FinancialsDTO {
    public double pendingClaims;
    public double approvedReimbursement;
    public double insurancePremium;
    public double declaredInvestments;
}
