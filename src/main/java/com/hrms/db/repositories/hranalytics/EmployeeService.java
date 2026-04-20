package com.hrms.db.repositories.hranalytics;

import com.hrms.db.entities.Employee;

import java.util.List;

public interface EmployeeService {
    List<Employee> getAllEmployees();
    Employee getEmployeeById(String employeeId);
    List<Employee> getEmployeesByDepartment(String department);
    List<Employee> getActiveEmployees();
}
