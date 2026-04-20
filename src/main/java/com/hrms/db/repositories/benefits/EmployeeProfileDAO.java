package com.hrms.db.repositories.benefits;

import com.hrms.db.entities.Employee;

import java.util.List;

/** DAO contract for accessing employee profiles via the shared {@link Employee} entity. */
public interface EmployeeProfileDAO {

    void save(Employee profile);

    Employee findById(String employeeId);

    List<Employee> findAll();

    void update(Employee profile);

    void delete(String employeeId);

    boolean existsById(String employeeId);
}