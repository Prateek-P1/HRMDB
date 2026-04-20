package com.hrms.db.repositories.timetracking;

import com.hrms.db.entities.Employee;

import java.util.List;

public interface IEmployeeRepository {
    void save(Employee employee);

    Employee findById(String empId);

    List<Employee> findAll();

    void delete(String empId);
}
