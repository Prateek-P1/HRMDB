package com.hrms.db.repositories.payroll;

import java.util.List;

/**
 * INTERFACE: IPayrollRepository
 * The formal contract between the Payroll Subsystem and the Database Team.
 *
 * GRASP: Polymorphism — allows switching between Mock and Real DB implementations.
 * SOLID: Dependency Inversion — Payroll logic depends on this abstraction, not the DB impl.
 *
 * USAGE (by Payroll team):
 *   IPayrollRepository repo = RepositoryFactory.getPayrollRepository();
 *   PayrollDataPackage data = repo.fetchEmployeeData("EMP001", "2025-06");
 */
public interface IPayrollRepository {

    /**
     * READ: Fetches all data needed to process payroll for one employee in one pay period.
     * Aggregates Employee, Attendance, Financial, and Tax data.
     *
     * @param empID     the unique employee identifier (e.g. "EMP001")
     * @param payPeriod the pay period in YYYY-MM format (e.g. "2025-06")
     * @return a filled PayrollDataPackage, never null — throws DatabaseException if not found
     */
    PayrollDataPackage fetchEmployeeData(String empID, String payPeriod);

    /**
     * READ: Returns IDs of all employees currently in ACTIVE employment status.
     * Used by the batch payroll run to determine who to process.
     *
     * @return list of empId strings; empty list if none found
     */
    List<String> getAllActiveEmployeeIDs();

    /**
     * WRITE: Persists the payroll results calculated by the Payroll Subsystem.
     * Maps PayrollResultDTO fields to the PayrollResult entity.
     *
     * @param batchID  the batch run identifier (links all results from one run)
     * @param result   the calculated values for one employee
     * @return true if persisted successfully, false otherwise
     */
    boolean savePayrollResult(String batchID, PayrollResultDTO result);

    /**
     * EXCEPTION HANDLING: Logs a processing error that occurred in the Payroll Subsystem.
     * Writes to the payroll_audit_log table with action_type = "ERROR".
     *
     * @param batchID  the batch ID in which the error occurred
     * @param empID    the employee being processed when the error occurred
     * @param errorMsg the error description from the Payroll team's exception
     */
    void logProcessingError(String batchID, String empID, String errorMsg);
}