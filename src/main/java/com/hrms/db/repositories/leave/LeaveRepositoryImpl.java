package com.hrms.db.repositories.leave;

import com.hrms.db.config.DatabaseConnection;
import com.hrms.db.entities.*;
import com.hrms.db.handlers.*;
import com.hrms.db.handlers.ErrorHandler.ErrorLevel;
import com.hrms.db.interfaces.DatabaseException;
import com.hrms.db.interfaces.RecordNotFoundException;
import com.hrms.db.logging.*;
import com.hrms.db.repositories.leave.LeaveDTOs.*;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * LeaveRepositoryImpl — Unified Hibernate implementation for all Leave Management interfaces.
 *
 * Implements:
 *   ILeaveRecordRepository    — leave request lifecycle (submit, approve, reject)
 *   ILeaveEmployeeRepository  — employee lookup and leave balance management
 *   ILeaveHolidayRepository   — holiday calendar queries
 *   ILeavePolicyRepository    — leave policy lookup by employee/grade
 *   ILeavePayrollSyncRepository — push leave summary data to payroll
 *   ILeaveAuditLogRepository  — leave action audit trail
 *
 * Entity Mapping:
 *   LeaveRecord      → leave_records table
 *   LeaveBalance     → leave_balances table
 *   LeavePolicy      → leave_policies table
 *   Holiday          → holidays table
 *   Employee         → employees table
 *   SecurityAuditLog → security_audit_logs table (for audit)
 *   PayrollAuditLog  → payroll_audit_logs table (for payroll sync)
 */
public class LeaveRepositoryImpl
        implements ILeaveRecordRepository,
                   ILeaveEmployeeRepository,
                   ILeaveHolidayRepository,
                   ILeavePolicyRepository,
                   ILeavePayrollSyncRepository,
                   ILeaveAuditLogRepository {

    private static final String REPO = "LeaveRepositoryImpl";

    private final ErrorHandler errorChain = new ConsoleErrorLogger(
            new DatabaseErrorLogger(new CriticalErrorEscalator(null)));
    private final LogHandler log = new ConsoleLogHandler(new DatabaseLogHandler(null));

    // ════════════════════════════════════════════════════════════
    // ILeaveRecordRepository
    // ════════════════════════════════════════════════════════════

    @Override
    public String saveLeaveRequest(LeaveRequestDTO request) {
        Transaction tx = null;
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            Employee emp = session.get(Employee.class, request.empId);
            if (emp == null) throw new RecordNotFoundException(REPO, "Employee", request.empId);

            LeaveRecord record = new LeaveRecord();
            String id = request.requestId != null ? request.requestId : UUID.randomUUID().toString();
            record.setLeaveId(id);
            record.setEmployee(emp);
            record.setLeaveType(request.leaveType);
            record.setStartDate(LocalDate.parse(request.startDate));
            record.setEndDate(LocalDate.parse(request.endDate));
            record.setStatus(request.status != null ? request.status : "PENDING");

            session.persist(record);
            tx.commit();

            log.log(LogHandler.LogLevel.INFO, REPO, "saveLeaveRequest",
                    "Saved leave request " + id + " for emp " + request.empId);
            return id;

        } catch (RecordNotFoundException e) {
            if (tx != null) try { tx.rollback(); } catch (Exception ignored) {}
            errorChain.handle(REPO + ".saveLeaveRequest", e, ErrorLevel.ERROR);
            throw e;
        } catch (Exception ex) {
            if (tx != null) try { tx.rollback(); } catch (Exception ignored) {}
            handleError("saveLeaveRequest", ex, ErrorLevel.ERROR);
            return null;
        }
    }

    @Override
    public LeaveRequestDTO getLeaveRequestById(String requestId) {
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            LeaveRecord record = session.get(LeaveRecord.class, requestId);
            if (record == null) throw new RecordNotFoundException(REPO, "LeaveRecord", requestId);
            return toLeaveRequestDTO(record);
        } catch (RecordNotFoundException e) {
            errorChain.handle(REPO + ".getLeaveRequestById", e, ErrorLevel.ERROR);
            throw e;
        } catch (Exception ex) {
            handleError("getLeaveRequestById", ex, ErrorLevel.ERROR);
            return null;
        }
    }

    @Override
    public List<LeaveRequestDTO> getLeavesByEmployee(String empId, String status) {
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            String hql = (status != null && !status.isBlank())
                    ? "FROM LeaveRecord l WHERE l.employee.empId = :id AND l.status = :status ORDER BY l.startDate DESC"
                    : "FROM LeaveRecord l WHERE l.employee.empId = :id ORDER BY l.startDate DESC";

            var query = session.createQuery(hql, LeaveRecord.class).setParameter("id", empId);
            if (status != null && !status.isBlank()) query.setParameter("status", status);

            return query.getResultList().stream().map(this::toLeaveRequestDTO).collect(Collectors.toList());
        } catch (Exception ex) {
            handleError("getLeavesByEmployee", ex, ErrorLevel.ERROR);
            return Collections.emptyList();
        }
    }

    @Override
    public List<LeaveRequestDTO> getPendingRequestsForManager(String managerId) {
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            // Get all employees who report to this manager, then get their pending leaves
            List<String> empIds = session.createQuery(
                    "SELECT e.empId FROM Employee e WHERE e.managerId = :mid", String.class)
                    .setParameter("mid", managerId)
                    .getResultList();

            if (empIds.isEmpty()) return Collections.emptyList();

            return session.createQuery(
                    "FROM LeaveRecord l WHERE l.employee.empId IN :ids AND l.status = 'PENDING' ORDER BY l.startDate",
                    LeaveRecord.class)
                    .setParameter("ids", empIds)
                    .getResultList().stream().map(this::toLeaveRequestDTO).collect(Collectors.toList());
        } catch (Exception ex) {
            handleError("getPendingRequestsForManager", ex, ErrorLevel.ERROR);
            return Collections.emptyList();
        }
    }

    @Override
    public boolean updateLeaveStatus(String requestId, String status, String comments) {
        try {
            executeUpdate("updateLeaveStatus",
                    "UPDATE LeaveRecord l SET l.status = :status WHERE l.leaveId = :id",
                    Map.of("status", status, "id", requestId));
            log.log(LogHandler.LogLevel.INFO, REPO, "updateLeaveStatus",
                    "Leave " + requestId + " → " + status);
            return true;
        } catch (Exception ex) {
            handleError("updateLeaveStatus", ex, ErrorLevel.ERROR);
            return false;
        }
    }

    @Override
    public boolean checkLeaveOverlap(String empId, String startDate, String endDate) {
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            LocalDate start = LocalDate.parse(startDate);
            LocalDate end   = LocalDate.parse(endDate);

            Long count = session.createQuery(
                    "SELECT COUNT(l) FROM LeaveRecord l " +
                    "WHERE l.employee.empId = :id " +
                    "AND l.status IN ('PENDING','APPROVED') " +
                    "AND l.startDate <= :end AND l.endDate >= :start",
                    Long.class)
                    .setParameter("id", empId)
                    .setParameter("start", start)
                    .setParameter("end", end)
                    .uniqueResult();

            return count != null && count > 0;
        } catch (Exception ex) {
            handleError("checkLeaveOverlap", ex, ErrorLevel.ERROR);
            return false;
        }
    }

    // ════════════════════════════════════════════════════════════
    // ILeaveEmployeeRepository
    // ════════════════════════════════════════════════════════════

    @Override
    public EmployeeInfoDTO getEmployeeById(String empId) {
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            Employee emp = session.get(Employee.class, empId);
            if (emp == null) throw new RecordNotFoundException(REPO, "Employee", empId);
            return toEmployeeInfoDTO(session, emp);
        } catch (RecordNotFoundException e) {
            errorChain.handle(REPO + ".getEmployeeById", e, ErrorLevel.ERROR);
            throw e;
        } catch (Exception ex) {
            handleError("getEmployeeById", ex, ErrorLevel.ERROR);
            return null;
        }
    }

    @Override
    public EmployeeInfoDTO getEmployeeByUserId(String userId) {
        // Employee entity has no userId field — searching by email as a reasonable proxy
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            Employee emp = session.createQuery(
                    "FROM Employee e WHERE e.email = :email", Employee.class)
                    .setParameter("email", userId)
                    .setMaxResults(1).uniqueResult();
            if (emp == null) throw new RecordNotFoundException(REPO, "Employee", "email=" + userId);
            return toEmployeeInfoDTO(session, emp);
        } catch (RecordNotFoundException e) {
            errorChain.handle(REPO + ".getEmployeeByUserId", e, ErrorLevel.ERROR);
            throw e;
        } catch (Exception ex) {
            handleError("getEmployeeByUserId", ex, ErrorLevel.ERROR);
            return null;
        }
    }

    @Override
    public double getLeaveBalance(String empId, String leaveType) {
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            Double balance = session.createQuery(
                    "SELECT lb.balance FROM LeaveBalance lb " +
                    "WHERE lb.employee.empId = :id AND lb.leaveType = :type",
                    Double.class)
                    .setParameter("id", empId)
                    .setParameter("type", leaveType)
                    .uniqueResult();
            return balance != null ? balance : 0.0;
        } catch (Exception ex) {
            handleError("getLeaveBalance", ex, ErrorLevel.ERROR);
            return 0.0;
        }
    }

    @Override
    public void updateLeaveBalance(String empId, String leaveType, double delta) {
        // delta is negative when leave is consumed, positive when credited/returned
        Transaction tx = null;
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            int updated = session.createMutationQuery(
                    "UPDATE LeaveBalance lb SET lb.balance = lb.balance + :delta, lb.used = lb.used - :delta " +
                    "WHERE lb.employee.empId = :id AND lb.leaveType = :type")
                    .setParameter("delta", delta)
                    .setParameter("id", empId)
                    .setParameter("type", leaveType)
                    .executeUpdate();

            if (updated == 0) {
                log.log(LogHandler.LogLevel.WARN, REPO, "updateLeaveBalance",
                        "No LeaveBalance row found for emp=" + empId + " type=" + leaveType);
            }
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) try { tx.rollback(); } catch (Exception ignored) {}
            handleError("updateLeaveBalance", ex, ErrorLevel.ERROR);
        }
    }

    @Override
    public EmployeeInfoDTO getReportingManager(String empId) {
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            Employee emp = session.get(Employee.class, empId);
            if (emp == null || emp.getManager() == null) return null;
            // manager is already loaded from the ManyToOne relationship
            return toEmployeeInfoDTO(session, emp.getManager());
        } catch (Exception ex) {
            handleError("getReportingManager", ex, ErrorLevel.ERROR);
            return null;
        }
    }

    @Override
    public List<EmployeeInfoDTO> getEmployeesByDepartment(String dept) {
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            return session.createQuery(
                    "FROM Employee e WHERE e.department = :dept", Employee.class)
                    .setParameter("dept", dept)
                    .getResultList().stream()
                    .map(e -> toEmployeeInfoDTO(session, e))
                    .collect(Collectors.toList());
        } catch (Exception ex) {
            handleError("getEmployeesByDepartment", ex, ErrorLevel.ERROR);
            return Collections.emptyList();
        }
    }

    // ════════════════════════════════════════════════════════════
    // ILeaveHolidayRepository
    // ════════════════════════════════════════════════════════════

    @Override
    public List<HolidayDTO> getHolidaysByYearAndLocation(int year, String location) {
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            return session.createQuery(
                    "FROM Holiday h WHERE h.holidayYear = :year AND " +
                    "(h.applicableLocation = :loc OR h.applicableLocation = 'ALL')",
                    Holiday.class)
                    .setParameter("year", year)
                    .setParameter("loc", location)
                    .getResultList().stream().map(this::toHolidayDTO).collect(Collectors.toList());
        } catch (Exception ex) {
            handleError("getHolidaysByYearAndLocation", ex, ErrorLevel.ERROR);
            return Collections.emptyList();
        }
    }

    @Override
    public boolean isHoliday(String date, String location) {
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            LocalDate d = LocalDate.parse(date);
            Long count = session.createQuery(
                    "SELECT COUNT(h) FROM Holiday h " +
                    "WHERE h.holidayDate = :date AND (h.applicableLocation = :loc OR h.applicableLocation = 'ALL')",
                    Long.class)
                    .setParameter("date", d)
                    .setParameter("loc", location)
                    .uniqueResult();
            return count != null && count > 0;
        } catch (Exception ex) {
            handleError("isHoliday", ex, ErrorLevel.ERROR);
            return false;
        }
    }

    // ════════════════════════════════════════════════════════════
    // ILeavePolicyRepository
    // ════════════════════════════════════════════════════════════

    @Override
    public LeavePolicyDTO getPolicyForEmployee(String empId) {
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            Employee emp = session.get(Employee.class, empId);
            if (emp == null) return null;

            int year = LocalDate.now().getYear();
            // Try to find a policy matching the employee's department, then fall back to ALL
            LeavePolicy policy = session.createQuery(
                    "FROM LeavePolicy p WHERE p.effectiveYear = :year AND " +
                    "(p.policyAppliesTo = :dept OR p.policyAppliesTo = 'ALL') ORDER BY p.policyAppliesTo DESC",
                    LeavePolicy.class)
                    .setParameter("year", year)
                    .setParameter("dept", emp.getDepartment() != null ? emp.getDepartment() : "ALL")
                    .setMaxResults(1).uniqueResult();

            return policy != null ? toPolicyDTO(policy) : null;
        } catch (Exception ex) {
            handleError("getPolicyForEmployee", ex, ErrorLevel.ERROR);
            return null;
        }
    }

    @Override
    public LeavePolicyDTO getPolicyById(String policyId) {
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            LeavePolicy policy = session.get(LeavePolicy.class, policyId);
            if (policy == null) throw new RecordNotFoundException(REPO, "LeavePolicy", policyId);
            return toPolicyDTO(policy);
        } catch (RecordNotFoundException e) {
            errorChain.handle(REPO + ".getPolicyById", e, ErrorLevel.ERROR);
            throw e;
        } catch (Exception ex) {
            handleError("getPolicyById", ex, ErrorLevel.ERROR);
            return null;
        }
    }

    // ════════════════════════════════════════════════════════════
    // ILeavePayrollSyncRepository
    // ════════════════════════════════════════════════════════════

    @Override
    public void pushLeaveDataToPayroll(String empId, LeaveSummaryDTO summary) {
        // Writes a PayrollAuditLog record flagging the leave sync event for the payroll pipeline
        Transaction tx = null;
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            Employee emp = session.get(Employee.class, empId);

            PayrollAuditLog auditLog = new PayrollAuditLog();
            auditLog.setLogId(UUID.randomUUID().toString());
            auditLog.setBatchId("LEAVE-SYNC-" + summary.period);
            auditLog.setEmployee(emp);
            auditLog.setActionType("LEAVE_SYNC");
            auditLog.setPerformedBy("LeaveRepository");
            auditLog.setErrorMsg(String.format(
                    "Period=%s | TotalDays=%.1f | Unpaid=%.1f | Paid=%.1f",
                    summary.period, summary.totalLeaveDaysTaken, summary.unpaidDays, summary.paidDays));

            session.persist(auditLog);
            tx.commit();

            log.log(LogHandler.LogLevel.INFO, REPO, "pushLeaveDataToPayroll",
                    "Synced leave for emp=" + empId + " period=" + summary.period);
        } catch (Exception ex) {
            if (tx != null) try { tx.rollback(); } catch (Exception ignored) {}
            handleError("pushLeaveDataToPayroll", ex, ErrorLevel.CRITICAL);
        }
    }

    @Override
    public String getPayrollProcessingStatus(String empId, String period) {
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            PayrollAuditLog entry = session.createQuery(
                    "FROM PayrollAuditLog p WHERE p.employee.empId = :id AND p.errorMsg LIKE :period ORDER BY p.ts DESC",
                    PayrollAuditLog.class)
                    .setParameter("id", empId)
                    .setParameter("period", "%" + period + "%")
                    .setMaxResults(1).uniqueResult();

            return entry != null ? "SYNCED" : "NOT_SYNCED";
        } catch (Exception ex) {
            handleError("getPayrollProcessingStatus", ex, ErrorLevel.ERROR);
            return "UNKNOWN";
        }
    }

    // ════════════════════════════════════════════════════════════
    // ILeaveAuditLogRepository
    // ════════════════════════════════════════════════════════════

    @Override
    public void writeAuditLog(String actorId, String action, String entity, String details) {
        Transaction tx = null;
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            SecurityAuditLog entry = new SecurityAuditLog();
            entry.setUserId(actorId);
            entry.setActionType("LEAVE_" + action.toUpperCase());
            entry.setOperation(entity);
            entry.setDetails(details);
            entry.setOutcome("SUCCESS");

            session.persist(entry);
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) try { tx.rollback(); } catch (Exception ignored) {}
            handleError("writeAuditLog", ex, ErrorLevel.WARNING);
        }
    }

    // ════════════════════════════════════════════════════════════
    // Private Helpers
    // ════════════════════════════════════════════════════════════

    private void executeUpdate(String method, String hql, Map<String, Object> params) {
        Transaction tx = null;
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            var q = session.createMutationQuery(hql);
            params.forEach(q::setParameter);
            q.executeUpdate();
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) try { tx.rollback(); } catch (Exception ignored) {}
            handleError(method, ex, ErrorLevel.ERROR);
        }
    }

    private void handleError(String method, Exception ex, ErrorLevel level) {
        errorChain.handle(REPO + "." + method,
                new DatabaseException(REPO + "." + method, ex.getMessage(), ex), level);
    }

    // ════════════════════════════════════════════════════════════
    // Mapping Helpers
    // ════════════════════════════════════════════════════════════

    private LeaveRequestDTO toLeaveRequestDTO(LeaveRecord r) {
        LeaveRequestDTO dto = new LeaveRequestDTO();
        dto.requestId = r.getLeaveId();
        dto.empId     = r.getEmployee() != null ? r.getEmployee().getEmpId() : null;
        dto.leaveType = r.getLeaveType();
        dto.startDate = r.getStartDate() != null ? r.getStartDate().toString() : null;
        dto.endDate   = r.getEndDate()   != null ? r.getEndDate().toString()   : null;
        dto.status    = r.getStatus();
        return dto;
    }

    private EmployeeInfoDTO toEmployeeInfoDTO(Session session, Employee e) {
        EmployeeInfoDTO dto = new EmployeeInfoDTO();
        dto.empId      = e.getEmpId();
        dto.name       = e.getName();
        dto.email      = e.getEmail();
        dto.department = e.getDepartment();
        // managerId is stored as a relationship; extract the empId from it
        dto.managerId  = e.getManager() != null ? e.getManager().getEmpId() : null;

        // Fetch leave balances
        dto.annualLeaveBalance  = fetchBalance(session, e.getEmpId(), "ANNUAL");
        dto.sickLeaveBalance    = fetchBalance(session, e.getEmpId(), "SICK");
        dto.casualLeaveBalance  = fetchBalance(session, e.getEmpId(), "CASUAL");
        return dto;
    }

    private double fetchBalance(Session session, String empId, String type) {
        try {
            Double b = session.createQuery(
                    "SELECT lb.balance FROM LeaveBalance lb WHERE lb.employee.empId = :id AND lb.leaveType = :type",
                    Double.class)
                    .setParameter("id", empId).setParameter("type", type).uniqueResult();
            return b != null ? b : 0.0;
        } catch (Exception ex) {
            return 0.0;
        }
    }

    private HolidayDTO toHolidayDTO(Holiday h) {
        HolidayDTO dto = new HolidayDTO();
        dto.holidayId   = h.getHolidayId();
        dto.holidayName = h.getHolidayName();
        dto.date        = h.getHolidayDate() != null ? h.getHolidayDate().toString() : null;
        dto.location    = h.getApplicableLocation();
        dto.year        = h.getHolidayYear() != null ? h.getHolidayYear() : 0;
        return dto;
    }

    private LeavePolicyDTO toPolicyDTO(LeavePolicy p) {
        LeavePolicyDTO dto = new LeavePolicyDTO();
        dto.policyId                   = p.getPolicyId();
        dto.policyAppliesTo            = p.getPolicyAppliesTo();
        dto.effectiveYear              = p.getEffectiveYear() != null ? p.getEffectiveYear() : 0;
        dto.annualLeaveMaxDays         = p.getAnnualLeaveMaxDays() != null ? p.getAnnualLeaveMaxDays() : 0;
        dto.sickLeaveMaxDays           = p.getSickLeaveMaxDays()   != null ? p.getSickLeaveMaxDays()   : 0;
        dto.casualLeaveMaxDays         = p.getCasualLeaveMaxDays() != null ? p.getCasualLeaveMaxDays() : 0;
        dto.sickLeaveCarryForwardAllowed   = Boolean.TRUE.equals(p.getSickLeaveCarryForwardAllowed());
        dto.casualLeaveCarryForwardAllowed = Boolean.TRUE.equals(p.getCasualLeaveCarryForwardAllowed());
        return dto;
    }
}
