package com.hrms.db.repositories.Leave_Management_Subsytem;

import com.hrms.db.repositories.leave.LeaveDTOs;
import com.hrms.db.repositories.leave.LeaveRepositoryImpl;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * LeaveManagementSubsystemRepositoryImpl
 *
 * Concrete DB implementation for the Leave_Management_Subsytem team's interfaces.
 * Delegates to the unified Hibernate implementation in {@link LeaveRepositoryImpl}.
 */
public class LeaveManagementSubsystemRepositoryImpl
        implements ILeaveRecordRepository,
                   ILeavePolicyRepository,
                   IHolidayRepository,
                   IEmployeeRepository,
                   IAuditLogRepository,
                   IPayrollSyncRepository {

    private final LeaveRepositoryImpl delegate;

    public LeaveManagementSubsystemRepositoryImpl() {
        this(new LeaveRepositoryImpl());
    }

    public LeaveManagementSubsystemRepositoryImpl(LeaveRepositoryImpl delegate) {
        this.delegate = Objects.requireNonNull(delegate, "delegate");
    }

    // ── ILeaveRecordRepository ─────────────────────────────────────

    @Override
    public String saveLeaveRequest(LeaveRequestDTO request) {
        return delegate.saveLeaveRequest(toInternal(request));
    }

    @Override
    public LeaveRequestDTO getLeaveRequestById(String requestId) {
        return toExternal(delegate.getLeaveRequestById(requestId));
    }

    @Override
    public List<LeaveRequestDTO> getLeavesByEmployee(String empId, String status) {
        List<LeaveDTOs.LeaveRequestDTO> list = delegate.getLeavesByEmployee(empId, status);
        if (list == null || list.isEmpty()) return Collections.emptyList();
        return list.stream().map(LeaveManagementSubsystemRepositoryImpl::toExternal).collect(Collectors.toList());
    }

    @Override
    public List<LeaveRequestDTO> getPendingRequestsForManager(String managerId) {
        List<LeaveDTOs.LeaveRequestDTO> list = delegate.getPendingRequestsForManager(managerId);
        if (list == null || list.isEmpty()) return Collections.emptyList();
        return list.stream().map(LeaveManagementSubsystemRepositoryImpl::toExternal).collect(Collectors.toList());
    }

    @Override
    public boolean updateLeaveStatus(String requestId, String status, String comments) {
        return delegate.updateLeaveStatus(requestId, status, comments);
    }

    @Override
    public boolean checkLeaveOverlap(String empId, String startDate, String endDate) {
        return delegate.checkLeaveOverlap(empId, startDate, endDate);
    }

    // ── ILeavePolicyRepository ─────────────────────────────────────

    @Override
    public LeavePolicyDTO getPolicyForEmployee(String empId) {
        return toExternal(delegate.getPolicyForEmployee(empId));
    }

    @Override
    public LeavePolicyDTO getPolicyById(String policyId) {
        return toExternal(delegate.getPolicyById(policyId));
    }

    // ── IHolidayRepository ─────────────────────────────────────────

    @Override
    public List<HolidayDTO> getHolidaysByYearAndLocation(int year, String location) {
        List<LeaveDTOs.HolidayDTO> list = delegate.getHolidaysByYearAndLocation(year, location);
        if (list == null || list.isEmpty()) return Collections.emptyList();
        return list.stream().map(LeaveManagementSubsystemRepositoryImpl::toExternal).collect(Collectors.toList());
    }

    @Override
    public boolean isHoliday(String date, String location) {
        return delegate.isHoliday(date, location);
    }

    // ── IEmployeeRepository ────────────────────────────────────────

    @Override
    public EmployeeInfoDTO getEmployeeById(String empId) {
        return toExternal(delegate.getEmployeeById(empId));
    }

    @Override
    public EmployeeInfoDTO getEmployeeByUserId(String userId) {
        return toExternal(delegate.getEmployeeByUserId(userId));
    }

    @Override
    public double getLeaveBalance(String empId, String leaveType) {
        return delegate.getLeaveBalance(empId, leaveType);
    }

    @Override
    public void updateLeaveBalance(String empId, String leaveType, double delta) {
        delegate.updateLeaveBalance(empId, leaveType, delta);
    }

    @Override
    public EmployeeInfoDTO getReportingManager(String empId) {
        return toExternal(delegate.getReportingManager(empId));
    }

    @Override
    public List<EmployeeInfoDTO> getEmployeesByDepartment(String dept) {
        List<LeaveDTOs.EmployeeInfoDTO> list = delegate.getEmployeesByDepartment(dept);
        if (list == null || list.isEmpty()) return Collections.emptyList();
        return list.stream().map(LeaveManagementSubsystemRepositoryImpl::toExternal).collect(Collectors.toList());
    }

    // ── IAuditLogRepository ────────────────────────────────────────

    @Override
    public void writeAuditLog(String actorId, String action, String entity, String details) {
        delegate.writeAuditLog(actorId, action, entity, details);
    }

    // ── IPayrollSyncRepository ─────────────────────────────────────

    @Override
    public void pushLeaveDataToPayroll(String empId, LeaveSummaryDTO summary) {
        delegate.pushLeaveDataToPayroll(empId, toInternal(summary));
    }

    @Override
    public String getPayrollProcessingStatus(String empId, String period) {
        return delegate.getPayrollProcessingStatus(empId, period);
    }

    // ── DTO Mapping ────────────────────────────────────────────────

    private static LeaveDTOs.LeaveRequestDTO toInternal(LeaveRequestDTO dto) {
        if (dto == null) return null;
        LeaveDTOs.LeaveRequestDTO i = new LeaveDTOs.LeaveRequestDTO();
        i.requestId = dto.requestId;
        i.empId = dto.empId;
        i.startDate = dto.startDate;
        i.endDate = dto.endDate;
        i.leaveType = dto.leaveType;
        i.status = dto.status;
        i.comments = dto.comments;
        return i;
    }

    private static LeaveRequestDTO toExternal(LeaveDTOs.LeaveRequestDTO dto) {
        if (dto == null) return null;
        LeaveRequestDTO o = new LeaveRequestDTO();
        o.requestId = dto.requestId;
        o.empId = dto.empId;
        o.startDate = dto.startDate;
        o.endDate = dto.endDate;
        o.leaveType = dto.leaveType;
        o.status = dto.status;
        o.comments = dto.comments;
        return o;
    }

    private static LeavePolicyDTO toExternal(LeaveDTOs.LeavePolicyDTO dto) {
        if (dto == null) return null;
        LeavePolicyDTO o = new LeavePolicyDTO();
        o.policyId = dto.policyId;
        o.policyAppliesTo = dto.policyAppliesTo;
        o.effectiveYear = dto.effectiveYear;
        o.annualLeaveMaxDays = dto.annualLeaveMaxDays;
        o.sickLeaveMaxDays = dto.sickLeaveMaxDays;
        o.casualLeaveMaxDays = dto.casualLeaveMaxDays;
        o.sickLeaveCarryForwardAllowed = dto.sickLeaveCarryForwardAllowed;
        o.casualLeaveCarryForwardAllowed = dto.casualLeaveCarryForwardAllowed;
        return o;
    }

    private static HolidayDTO toExternal(LeaveDTOs.HolidayDTO dto) {
        if (dto == null) return null;
        HolidayDTO o = new HolidayDTO();
        o.holidayId = dto.holidayId;
        o.holidayName = dto.holidayName;
        o.date = dto.date;
        o.location = dto.location;
        o.year = dto.year;
        return o;
    }

    private static EmployeeInfoDTO toExternal(LeaveDTOs.EmployeeInfoDTO dto) {
        if (dto == null) return null;
        EmployeeInfoDTO o = new EmployeeInfoDTO();
        o.empId = dto.empId;
        o.name = dto.name;
        o.email = dto.email;
        o.department = dto.department;
        o.managerId = dto.managerId;
        o.annualLeaveBalance = dto.annualLeaveBalance;
        o.sickLeaveBalance = dto.sickLeaveBalance;
        o.casualLeaveBalance = dto.casualLeaveBalance;
        return o;
    }

    private static LeaveDTOs.LeaveSummaryDTO toInternal(LeaveSummaryDTO dto) {
        if (dto == null) return null;
        LeaveDTOs.LeaveSummaryDTO i = new LeaveDTOs.LeaveSummaryDTO();
        i.empId = dto.empId;
        i.period = dto.period;
        i.totalLeaveDaysTaken = dto.totalLeaveDaysTaken;
        i.unpaidDays = dto.unpaidDays;
        i.paidDays = dto.paidDays;
        return i;
    }
}
