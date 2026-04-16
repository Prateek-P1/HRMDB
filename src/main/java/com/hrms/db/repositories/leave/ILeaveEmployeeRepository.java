package com.hrms.db.repositories.leave;

import com.hrms.db.repositories.leave.LeaveDTOs.EmployeeInfoDTO;
import java.util.List;

/**
 * ILeaveEmployeeRepository — provided by Leave Management team.
 * Read-only employee data needed for leave processing.
 */
public interface ILeaveEmployeeRepository {

    EmployeeInfoDTO getEmployeeById(String empId);

    EmployeeInfoDTO getEmployeeByUserId(String userId);

    double getLeaveBalance(String empId, String leaveType);

    void updateLeaveBalance(String empId, String leaveType, double delta);

    EmployeeInfoDTO getReportingManager(String empId);

    List<EmployeeInfoDTO> getEmployeesByDepartment(String dept);
}
