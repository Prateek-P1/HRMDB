package com.hrms.db.repositories.Leave_Management_Subsytem;

import java.util.List;

public interface ILeaveRecordRepository {

    String saveLeaveRequest(LeaveRequestDTO request);

    LeaveRequestDTO getLeaveRequestById(String requestId);

    List<LeaveRequestDTO> getLeavesByEmployee(String empId, String status);

    List<LeaveRequestDTO> getPendingRequestsForManager(String managerId);

    boolean updateLeaveStatus(String requestId, String status, String comments);

    boolean checkLeaveOverlap(String empId, String startDate, String endDate);
}