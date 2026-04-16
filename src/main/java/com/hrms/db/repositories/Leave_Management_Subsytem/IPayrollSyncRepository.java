package com.hrms.db.repositories.Leave_Management_Subsytem;

public interface IPayrollSyncRepository {

    void pushLeaveDataToPayroll(String empId, LeaveSummaryDTO summary);

    String getPayrollProcessingStatus(String empId, String period);
}