package com.hrms.db.repositories.Expense_Management;

import com.pesu.expensesubsystem.entity.Employee;
import java.util.List;
import java.util.Optional;

public interface EmployeeRepository {
    Optional<Employee> findById(String employeeId);
    Employee getEmployeeOrThrow(String employeeId);
    boolean existsById(String employeeId);
    Employee save(Employee employee);
    List<Employee> findAll();
    List<Employee> findByDepartment(String department);
    List<Employee> findByManagerId(String managerId);
}
