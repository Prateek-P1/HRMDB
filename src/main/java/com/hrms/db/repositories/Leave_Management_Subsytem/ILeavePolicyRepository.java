package com.hrms.db.repositories.Leave_Management_Subsytem;

public interface ILeavePolicyRepository {

    LeavePolicyDTO getPolicyForEmployee(String empId);

    LeavePolicyDTO getPolicyById(String policyId);
}