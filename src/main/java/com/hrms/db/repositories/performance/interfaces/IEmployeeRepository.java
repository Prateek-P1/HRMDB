package com.hrms.db.repositories.performance.interfaces;

import com.hrms.db.repositories.performance.models.Employee;
import java.util.List;

/**
 * IEmployeeRepository
 * Component: Employee Profile Component
 *
 * Provides read access to employee master data.
 * NOTE TO DB TEAM: This is read-only from Performance Management's perspective.
 * Employee creation/deletion is handled by the HR Core subsystem.
 */
public interface IEmployeeRepository {

    /**
     * Fetch a full employee profile by their unique ID.
     * @param employeeId unique employee identifier
     * @return Employee object, or null if not found
     */
    Employee getEmployeeById(int employeeId);

    /**
     * Retrieve all active employees in a given department.
     * @param deptId department identifier
     * @return list of employees; empty list if none found
     */
    List<Employee> getEmployeesByDept(int deptId);

    /**
     * Return the direct manager of an employee.
     * @param employeeId the employee whose manager is needed
     * @return Employee object for the manager, or null if no manager assigned
     */
    Employee getManagerOf(int employeeId);

    /**
     * Return all direct reports of a manager.
     * @param managerId the manager's employee ID
     * @return list of direct reportees; empty list if none
     */
    List<Employee> getReportees(int managerId);
}
