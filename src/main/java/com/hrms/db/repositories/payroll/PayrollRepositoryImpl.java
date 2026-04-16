package com.hrms.db.repositories.payroll;

import com.hrms.db.config.DatabaseConnection;
import com.hrms.db.entities.Attendance;
import com.hrms.db.entities.Employee;
import com.hrms.db.entities.Financial;
import com.hrms.db.entities.PayrollAuditLog;
import com.hrms.db.entities.PayrollResult;
import com.hrms.db.handlers.ErrorHandler;
import com.hrms.db.handlers.ErrorHandler.ErrorLevel;
import com.hrms.db.handlers.ConsoleErrorLogger;
import com.hrms.db.handlers.DatabaseErrorLogger;
import com.hrms.db.handlers.CriticalErrorEscalator;
import com.hrms.db.interfaces.DatabaseException;
import com.hrms.db.interfaces.RecordNotFoundException;
import com.hrms.db.logging.ConsoleLogHandler;
import com.hrms.db.logging.DatabaseLogHandler;
import com.hrms.db.logging.LogHandler;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * PayrollRepositoryImpl — concrete Hibernate implementation of IPayrollRepository.
 *
 * ═══════════════════════════════════════════════════════════════════════
 * HOW TO UNDERSTAND THIS FILE (learning guide for your team)
 * ═══════════════════════════════════════════════════════════════════════
 *
 * 1. EVERY public method follows the same structure:
 *      a) log.log(INFO, ...) — record that we started
 *      b) Try to open a Hibernate session
 *      c) Do the query using HQL (Hibernate Query Language, like SQL but with class names)
 *      d) Map the entity result → DTO (so the Payroll team never sees our Hibernate entities)
 *      e) Catch any exception → wrap in DatabaseException → pass to the error handler chain
 *
 * 2. HQL vs SQL:
 *      SQL:  SELECT * FROM employees WHERE emp_id = 'EMP001'
 *      HQL:  FROM Employee e WHERE e.empId = :empId
 *      The difference: HQL uses Java class names (Employee) and field names (empId),
 *      not table/column names. Hibernate translates to SQL automatically.
 *
 * 3. The 'session' object is the Hibernate equivalent of a JDBC Connection.
 *      Always open it in a try-with-resources block so it auto-closes.
 *
 * 4. Transactions are only needed for WRITE operations (INSERT/UPDATE/DELETE).
 *      READ operations (SELECT) don't need a transaction, but it doesn't hurt.
 *
 * 5. The error handler chain:
 *      ConsoleErrorLogger → DatabaseErrorLogger → CriticalErrorEscalator
 *      Each one handles the error and passes it forward automatically.
 *
 * PATTERNS USED:
 *   - GRASP: Information Expert (this class owns all payroll DB logic)
 *   - GRASP: Low Coupling (Payroll team only depends on IPayrollRepository interface)
 *   - GoF: Chain of Responsibility (error handler chain)
 *   - SOLID: Single Responsibility (this class only does DB access, no calculations)
 *   - SOLID: Dependency Inversion (depends on interface, not concrete DB driver)
 */
public class PayrollRepositoryImpl implements IPayrollRepository {

    // ── Infrastructure ───────────────────────────────────────────────
    private static final String REPO = "PayrollRepositoryImpl";

    /**
     * The error handler CHAIN. When we call errorChain.handle(...), it automatically:
     *   1. Logs to console (ConsoleErrorLogger)
     *   2. Persists to DB (DatabaseErrorLogger)
     *   3. Shows dialog if CRITICAL (CriticalErrorEscalator)
     */
    private final ErrorHandler errorChain = new ConsoleErrorLogger(
            new DatabaseErrorLogger(
                    new CriticalErrorEscalator(null)));  // null = end of chain

    /**
     * The log chain for normal operational messages (INFO, DEBUG, WARN).
     */
    private final LogHandler log = new ConsoleLogHandler(
            new DatabaseLogHandler(null));

    // ── Interface Implementation ─────────────────────────────────────

    /**
     * Fetches all data required to process payroll for one employee.
     *
     * WHAT THIS DOES IN THE DB:
     *   1. Load Employee entity by empId
     *   2. Load the Attendance record for this employee + pay period
     *   3. Load the Financial record for this employee + pay period
     *   4. Map all three into a PayrollDataPackage (the contract object)
     *
     * NOTE: If attendance or financial records don't exist for this period,
     * we return zero-filled DTOs rather than throwing — because the payroll
     * team's calculators must handle "no data" cases gracefully.
     */
    @Override
    public PayrollDataPackage fetchEmployeeData(String empID, String payPeriod) {
        log.log(LogHandler.LogLevel.INFO, REPO, "fetchEmployeeData",
                "Fetching data for empID=" + empID + ", period=" + payPeriod);

        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {

            // ── Step 1: Load employee ────────────────────────────────
            Employee emp = session.get(Employee.class, empID);
            if (emp == null) {
                // Employee doesn't exist — throw a specific RecordNotFoundException
                throw new RecordNotFoundException(REPO + ".fetchEmployeeData", "Employee", empID);
            }

            // ── Step 2: Load attendance record for this period ────────
            // HQL query: find the attendance row for this employee + pay period
            Query<Attendance> attQuery = session.createQuery(
                    "FROM Attendance a WHERE a.employee.empId = :empId AND a.payPeriod = :period",
                    Attendance.class);
            attQuery.setParameter("empId", empID);
            attQuery.setParameter("period", payPeriod);
            Attendance att = attQuery.uniqueResult(); // returns null if no row found

            // ── Step 3: Load financial record for this period ─────────
            Query<Financial> finQuery = session.createQuery(
                    "FROM Financial f WHERE f.employee.empId = :empId AND f.payPeriod = :period",
                    Financial.class);
            finQuery.setParameter("empId", empID);
            finQuery.setParameter("period", payPeriod);
            Financial fin = finQuery.uniqueResult();

            // ── Step 4: Map everything to the PayrollDataPackage ─────
            PayrollDataPackage pkg = new PayrollDataPackage();
            pkg.payPeriod  = payPeriod;
            pkg.employee   = mapEmployee(emp);
            pkg.attendance = mapAttendance(att);   // handles null → zero-filled
            pkg.financials = mapFinancials(fin);   // handles null → zero-filled
            pkg.tax        = mapTax(emp);

            log.log(LogHandler.LogLevel.INFO, REPO, "fetchEmployeeData",
                    "Successfully fetched data for empID=" + empID);
            return pkg;

        } catch (RecordNotFoundException rnfe) {
            // Not-found is an ERROR (the payroll run will skip this employee)
            errorChain.handle(REPO + ".fetchEmployeeData", rnfe, ErrorLevel.ERROR);
            throw rnfe; // re-throw so the Payroll team knows which employee failed

        } catch (Exception ex) {
            // Unexpected DB failure — wrap and escalate
            DatabaseException dbe = new DatabaseException(
                    REPO + ".fetchEmployeeData",
                    "Unexpected error fetching data for empID=" + empID, ex);
            errorChain.handle(REPO + ".fetchEmployeeData", dbe, ErrorLevel.CRITICAL);
            throw dbe;
        }
    }

    /**
     * Returns IDs of all ACTIVE employees.
     *
     * HQL note: we SELECT only e.empId (a single String field) rather than
     * loading full Employee entities, keeping this query fast and lightweight.
     */
    @Override
    public List<String> getAllActiveEmployeeIDs() {
        log.log(LogHandler.LogLevel.INFO, REPO, "getAllActiveEmployeeIDs", "Fetching active employee IDs");

        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {

            // This HQL projects just the empId string, not the full entity
            Query<String> query = session.createQuery(
                    "SELECT e.empId FROM Employee e WHERE e.employmentStatus = 'ACTIVE'",
                    String.class);

            List<String> ids = query.getResultList();
            log.log(LogHandler.LogLevel.INFO, REPO, "getAllActiveEmployeeIDs",
                    "Found " + ids.size() + " active employees");
            return ids;

        } catch (Exception ex) {
            DatabaseException dbe = new DatabaseException(
                    REPO + ".getAllActiveEmployeeIDs",
                    "Failed to retrieve active employee IDs", ex);
            errorChain.handle(REPO + ".getAllActiveEmployeeIDs", dbe, ErrorLevel.ERROR);
            return new ArrayList<>();  // return empty list so batch run doesn't crash
        }
    }

    /**
     * Persists a PayrollResultDTO into the payroll_results table.
     *
     * WRITE OPERATIONS always need:
     *   a) Begin a transaction
     *   b) Do the work (persist / merge / delete)
     *   c) Commit the transaction
     *   d) If anything goes wrong → rollback the transaction
     *
     * Rolling back undoes all changes in the transaction atomically.
     */
    @Override
    public boolean savePayrollResult(String batchID, PayrollResultDTO dto) {
        log.log(LogHandler.LogLevel.INFO, REPO, "savePayrollResult",
                "Saving result for empID=" + dto.empID + ", batchID=" + batchID);

        Transaction tx = null;
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {

            tx = session.beginTransaction(); // start the transaction

            // Look up the Employee entity (needed for the @ManyToOne relationship)
            Employee emp = session.get(Employee.class, dto.empID);
            if (emp == null) {
                throw new RecordNotFoundException(REPO + ".savePayrollResult", "Employee", dto.empID);
            }

            // Build the entity from the DTO
            PayrollResult result = new PayrollResult();
            result.setRecordId(dto.recordID != null ? dto.recordID : UUID.randomUUID().toString());
            result.setEmployee(emp);
            result.setBatchId(batchID);
            result.setPayPeriod(""); // Payroll team should set this in PayrollResultDTO if needed
            result.setFinalGrossPay(dto.finalGrossPay);
            result.setFinalNetPay(dto.finalNetPay);
            result.setPenaltyAmount(dto.penaltyAmount);
            result.setPfAmount(dto.pfAmount);
            result.setMonthlyTdsAmount(dto.taxDeducted);
            result.setPayoutAmount(dto.payoutAmount);

            session.persist(result);   // INSERT INTO payroll_results ...
            tx.commit();               // commit makes the change permanent

            log.log(LogHandler.LogLevel.INFO, REPO, "savePayrollResult",
                    "Saved result recordID=" + result.getRecordId());
            return true;

        } catch (Exception ex) {
            // CRITICAL — if we can't save payroll results, that's a data-loss risk
            if (tx != null) {
                try { tx.rollback(); } catch (Exception rb) { /* ignore rollback failure */ }
            }
            DatabaseException dbe = new DatabaseException(
                    REPO + ".savePayrollResult",
                    "Failed to save payroll result for empID=" + dto.empID, ex);
            errorChain.handle(REPO + ".savePayrollResult", dbe, ErrorLevel.CRITICAL);
            return false;
        }
    }

    /**
     * Logs a payroll processing error from the Payroll Subsystem.
     *
     * This is called when the Payroll team catches an error during calculation
     * (e.g., divide-by-zero in tax calculation) and wants us to record it.
     * Written to payroll_audit_log with action_type = "ERROR".
     */
    @Override
    public void logProcessingError(String batchID, String empID, String errorMsg) {
        log.log(LogHandler.LogLevel.WARN, REPO, "logProcessingError",
                "Payroll error for empID=" + empID + ": " + errorMsg);

        Transaction tx = null;
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {

            tx = session.beginTransaction();

            // Look up employee (may be null if empID is unknown — still log it)
            Employee emp = session.get(Employee.class, empID);

            PayrollAuditLog auditLog = new PayrollAuditLog();
            auditLog.setLogId(UUID.randomUUID().toString());
            auditLog.setBatchId(batchID);
            auditLog.setEmployee(emp); // null is OK — it just means we can't link to an employee
            auditLog.setActionType("ERROR");
            auditLog.setPerformedBy("PayrollSubsystem");
            auditLog.setErrorMsg(errorMsg);

            session.persist(auditLog);
            tx.commit();

        } catch (Exception ex) {
            if (tx != null) {
                try { tx.rollback(); } catch (Exception rb) { /* ignore */ }
            }
            // Don't throw — we don't want a logging failure to crash the payroll run
            errorChain.handle(REPO + ".logProcessingError",
                    new DatabaseException(REPO + ".logProcessingError",
                            "Failed to log payroll error", ex),
                    ErrorLevel.ERROR);
        }
    }

    // ── Private mapping helpers ───────────────────────────────────────────
    // These convert Hibernate entities → DTOs (the contract objects the Payroll team uses).
    // This is the "mapping layer" — it means if we change our entities, we only
    // update these helpers, not the interface or the Payroll team's code.

    private EmployeeDTO mapEmployee(Employee e) {
        EmployeeDTO dto = new EmployeeDTO();
        dto.empID          = e.getEmpId();
        dto.name           = e.getName();
        dto.department     = e.getDepartment();
        dto.gradeLevel     = e.getGradeLevel();
        dto.basicPay       = e.getBasicPay()       != null ? e.getBasicPay()       : 0.0;
        dto.yearsOfService = e.getYearsOfService()  != null ? e.getYearsOfService() : 0;
        return dto;
    }

    private AttendanceDTO mapAttendance(Attendance a) {
        AttendanceDTO dto = new AttendanceDTO();
        if (a == null) return dto; // zero-filled defaults are fine
        dto.workingDaysInMonth = a.getWorkingDaysInMonth() != null ? a.getWorkingDaysInMonth() : 0;
        dto.leaveWithPay       = a.getLeaveWithPay()       != null ? a.getLeaveWithPay()       : 0;
        dto.leaveWithoutPay    = a.getLeaveWithoutPay()    != null ? a.getLeaveWithoutPay()    : 0;
        dto.hoursWorked        = a.getHoursWorked()        != null ? a.getHoursWorked()        : 0.0;
        dto.overtimeHours      = a.getOvertimeHours()      != null ? a.getOvertimeHours()      : 0.0;
        return dto;
    }

    private FinancialsDTO mapFinancials(Financial f) {
        FinancialsDTO dto = new FinancialsDTO();
        if (f == null) return dto;
        dto.pendingClaims          = f.getPendingClaims()          != null ? f.getPendingClaims()          : 0.0;
        dto.approvedReimbursement  = f.getApprovedReimbursement()  != null ? f.getApprovedReimbursement()  : 0.0;
        dto.insurancePremium       = f.getInsurancePremium()       != null ? f.getInsurancePremium()       : 0.0;
        dto.declaredInvestments    = f.getDeclaredInvestments()    != null ? f.getDeclaredInvestments()    : 0.0;
        return dto;
    }

    private TaxContextDTO mapTax(Employee e) {
        TaxContextDTO dto = new TaxContextDTO();
        dto.countryCode     = e.getCountryCode();
        dto.currencyCode    = e.getCurrencyCode();
        dto.taxRegime       = e.getTaxRegime();
        dto.stateName       = e.getStateName();
        dto.filingStatus    = e.getFilingStatus();
        dto.taxCode         = e.getTaxCode();
        dto.nationalIDNumber = e.getNationalIdNumber();
        return dto;
    }
}
