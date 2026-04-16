package com.hrms.db.repositories.leave;

import com.hrms.db.repositories.leave.LeaveDTOs.LeaveRequestDTO;
import java.util.List;

/**
 * ILeaveRecordRepository — provided by Leave Management team.
 * Handles leave request CRUD and status management.
 */
public interface ILeaveRecordRepository {

    String saveLeaveRequest(LeaveRequestDTO request);

    LeaveRequestDTO getLeaveRequestById(String requestId);

    List<LeaveRequestDTO> getLeavesByEmployee(String empId, String status);

    List<LeaveRequestDTO> getPendingRequestsForManager(String managerId);

    boolean updateLeaveStatus(String requestId, String status, String comments);

    boolean checkLeaveOverlap(String empId, String startDate, String endDate);
}
