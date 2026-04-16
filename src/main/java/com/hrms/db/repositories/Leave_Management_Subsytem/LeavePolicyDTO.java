package com.hrms.db.repositories.Leave_Management_Subsytem;

/**
 * DTO: LeavePolicyDTO
 *
 * Team-provided data contract for Leave policy reads.
 */
public class LeavePolicyDTO {
    public String policyId;
    public String policyAppliesTo;
    public int effectiveYear;
    public int annualLeaveMaxDays;
    public int sickLeaveMaxDays;
    public int casualLeaveMaxDays;
    public boolean sickLeaveCarryForwardAllowed;
    public boolean casualLeaveCarryForwardAllowed;
}
