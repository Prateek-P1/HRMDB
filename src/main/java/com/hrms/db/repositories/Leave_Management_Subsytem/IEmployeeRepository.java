package com.hrms.db.repositories.Leave_Management_Subsytem;

import java.util.List;

public interface IEmployeeRepository {

    EmployeeInfoDTO getEmployeeById(String empId);

    EmployeeInfoDTO getEmployeeByUserId(String userId);

    double getLeaveBalance(String empId, String leaveType);

    void updateLeaveBalance(String empId, String leaveType, double delta);

    EmployeeInfoDTO getReportingManager(String empId);

    List<EmployeeInfoDTO> getEmployeesByDepartment(String dept);
}