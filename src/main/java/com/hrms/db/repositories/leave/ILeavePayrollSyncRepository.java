package com.hrms.db.repositories.leave;

import com.hrms.db.repositories.leave.LeaveDTOs.LeaveSummaryDTO;

/**
 * ILeavePayrollSyncRepository — provided by Leave Management team.
 * Pushes leave data into the payroll pipeline at month-end.
 */
public interface ILeavePayrollSyncRepository {

    void pushLeaveDataToPayroll(String empId, LeaveSummaryDTO summary);

    String getPayrollProcessingStatus(String empId, String period);
}
