package com.hrms.db.repositories.leave;

import java.util.List;

/**
 * DTOs used by the Leave Management subsystem.
 * All Leave Management interfaces operate on these objects.
 */
public class LeaveDTOs {

    /** Represents a single leave request. */
    public static class LeaveRequestDTO {
        public String requestId;
        public String empId;
        public String startDate;   // ISO format: yyyy-MM-dd
        public String endDate;     // ISO format: yyyy-MM-dd
        public String leaveType;   // ANNUAL, SICK, CASUAL, UNPAID
        public String status;      // PENDING, APPROVED, REJECTED
        public String comments;
    }

    /** Represents an employee summary needed by Leave Management. */
    public static class EmployeeInfoDTO {
        public String empId;
        public String name;
        public String email;
        public String department;
        public String managerId;
        public double annualLeaveBalance;
        public double sickLeaveBalance;
        public double casualLeaveBalance;
    }

    /** Represents a holiday entry. */
    public static class HolidayDTO {
        public String holidayId;
        public String holidayName;
        public String date;           // ISO format: yyyy-MM-dd
        public String location;
        public int year;
    }

    /** Represents the configured leave policy for an employee. */
    public static class LeavePolicyDTO {
        public String policyId;
        public String policyAppliesTo;
        public int effectiveYear;
        public int annualLeaveMaxDays;
        public int sickLeaveMaxDays;
        public int casualLeaveMaxDays;
        public boolean sickLeaveCarryForwardAllowed;
        public boolean casualLeaveCarryForwardAllowed;
    }

    /** Represents a summary of leave usage pushed to Payroll. */
    public static class LeaveSummaryDTO {
        public String empId;
        public String period;      // e.g. "2025-04"
        public double totalLeaveDaysTaken;
        public double unpaidDays;
        public double paidDays;
    }
}
