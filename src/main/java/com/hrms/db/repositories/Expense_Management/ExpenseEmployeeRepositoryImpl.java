package com.hrms.db.repositories.Expense_Management;

import com.hrms.db.config.DatabaseConnection;
import com.pesu.expensesubsystem.entity.Employee;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

public class ExpenseEmployeeRepositoryImpl implements EmployeeRepository {

    @Override
    public Optional<Employee> findById(String employeeId) {
        if (employeeId == null) return Optional.empty();
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            com.hrms.db.entities.Employee entity = session.get(com.hrms.db.entities.Employee.class, employeeId);
            return Optional.ofNullable(ExpenseRepoMapper.toDtoEmployee(entity));
        }
    }

    @Override
    public Employee getEmployeeOrThrow(String employeeId) {
        return findById(employeeId)
                .orElseThrow(() -> new NoSuchElementException("Employee not found: " + employeeId));
    }

    @Override
    public boolean existsById(String employeeId) {
        if (employeeId == null) return false;
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            return session.get(com.hrms.db.entities.Employee.class, employeeId) != null;
        }
    }

    @Override
    public Employee save(Employee employee) {
        if (employee == null) throw new IllegalArgumentException("employee is null");
        Transaction tx = null;
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            com.hrms.db.entities.Employee entity = ExpenseRepoMapper.toEntityEmployee(employee, session);
            session.merge(entity);
            tx.commit();
            return ExpenseRepoMapper.toDtoEmployee(entity);
        } catch (Exception ex) {
            if (tx != null) try { tx.rollback(); } catch (Exception ignored) {}
            throw ex;
        }
    }

    @Override
    public List<Employee> findAll() {
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            List<com.hrms.db.entities.Employee> entities = session
                    .createQuery("FROM Employee e ORDER BY e.empId ASC", com.hrms.db.entities.Employee.class)
                    .getResultList();
            List<Employee> out = new ArrayList<>(entities.size());
            for (com.hrms.db.entities.Employee e : entities) out.add(ExpenseRepoMapper.toDtoEmployee(e));
            return out;
        } catch (Exception ex) {
            return Collections.emptyList();
        }
    }

    @Override
    public List<Employee> findByDepartment(String department) {
        if (department == null) return Collections.emptyList();
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            List<com.hrms.db.entities.Employee> entities = session
                    .createQuery("FROM Employee e WHERE e.department = :d ORDER BY e.empId ASC", com.hrms.db.entities.Employee.class)
                    .setParameter("d", department)
                    .getResultList();
            List<Employee> out = new ArrayList<>(entities.size());
            for (com.hrms.db.entities.Employee e : entities) out.add(ExpenseRepoMapper.toDtoEmployee(e));
            return out;
        } catch (Exception ex) {
            return Collections.emptyList();
        }
    }

    @Override
    public List<Employee> findByManagerId(String managerId) {
        if (managerId == null) return Collections.emptyList();
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            List<com.hrms.db.entities.Employee> entities = session
                    .createQuery("FROM Employee e WHERE e.manager.empId = :m ORDER BY e.empId ASC", com.hrms.db.entities.Employee.class)
                    .setParameter("m", managerId)
                    .getResultList();
            List<Employee> out = new ArrayList<>(entities.size());
            for (com.hrms.db.entities.Employee e : entities) out.add(ExpenseRepoMapper.toDtoEmployee(e));
            return out;
        } catch (Exception ex) {
            return Collections.emptyList();
        }
    }
}
