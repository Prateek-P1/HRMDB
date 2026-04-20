package com.hrms.db.repositories.hranalytics;

import com.hrms.db.config.DatabaseConnection;
import com.hrms.db.entities.Employee;
import org.hibernate.Session;

import java.util.Collections;
import java.util.List;

public class EmployeeServiceImpl implements EmployeeService {

    @Override
    public List<Employee> getAllEmployees() {
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            return session.createQuery("FROM Employee e ORDER BY e.empId ASC", Employee.class)
                    .getResultList();
        } catch (Exception ex) {
            return Collections.emptyList();
        }
    }

    @Override
    public Employee getEmployeeById(String employeeId) {
        if (employeeId == null) return null;
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            return session.get(Employee.class, employeeId);
        }
    }

    @Override
    public List<Employee> getEmployeesByDepartment(String department) {
        if (department == null) return Collections.emptyList();
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            return session.createQuery(
                            "FROM Employee e WHERE e.department = :d ORDER BY e.empId ASC",
                            Employee.class)
                    .setParameter("d", department)
                    .getResultList();
        } catch (Exception ex) {
            return Collections.emptyList();
        }
    }

    @Override
    public List<Employee> getActiveEmployees() {
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            return session.createQuery(
                            "FROM Employee e WHERE e.employmentStatus = 'ACTIVE' ORDER BY e.empId ASC",
                            Employee.class)
                    .getResultList();
        } catch (Exception ex) {
            return Collections.emptyList();
        }
    }
}
