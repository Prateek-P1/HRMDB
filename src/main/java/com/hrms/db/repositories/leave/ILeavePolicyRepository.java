package com.hrms.db.repositories.leave;

import com.hrms.db.repositories.leave.LeaveDTOs.LeavePolicyDTO;

/**
 * ILeavePolicyRepository — provided by Leave Management team.
 * Reads configured leave policies per employee/grade.
 */
public interface ILeavePolicyRepository {

    LeavePolicyDTO getPolicyForEmployee(String empId);

    LeavePolicyDTO getPolicyById(String policyId);
}
