package com.hrms.db.repositories.timetracking;

import com.hrms.db.config.DatabaseConnection;
import com.hrms.db.entities.Employee;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.Collections;
import java.util.List;

public class TimeTrackingEmployeeRepositoryImpl implements IEmployeeRepository {

    @Override
    public void save(Employee employee) {
        Transaction tx = null;
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.merge(employee);
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) try { tx.rollback(); } catch (Exception ignored) {}
            throw ex;
        }
    }

    @Override
    public Employee findById(String empId) {
        if (empId == null) return null;
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            return session.get(Employee.class, empId);
        }
    }

    @Override
    public List<Employee> findAll() {
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            return session.createQuery(
                            "FROM Employee e ORDER BY e.empId ASC",
                            Employee.class)
                    .getResultList();
        } catch (Exception ex) {
            return Collections.emptyList();
        }
    }

    @Override
    public void delete(String empId) {
        if (empId == null) return;
        Transaction tx = null;
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            Employee existing = session.get(Employee.class, empId);
            if (existing != null) session.remove(existing);
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) try { tx.rollback(); } catch (Exception ignored) {}
            throw ex;
        }
    }
}
