package com.hrms.db.repositories.benefits;

import com.hrms.db.config.DatabaseConnection;
import com.hrms.db.entities.Employee;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.Collections;
import java.util.List;

/**
 * In-Memory DAO Implementation (Mock DB)
 *
 * PURPOSE:
 * This class acts as a temporary/mock database using in-memory storage.
 * It replaces actual database interactions so that the system can run
 * without requiring a real DB connection.
 *
 * DESIGN PATTERN:
 * - DAO (Data Access Object) Pattern → Separates persistence logic from business logic
 *
 * GRASP PRINCIPLE:
 * - Information Expert → This class is responsible for managing EmployeeProfile data
 *
 * SOLID PRINCIPLES:
 * - SRP (Single Responsibility Principle):
 *   This class is only responsible for data access operations.
 *
 * - DIP (Dependency Inversion Principle):
 *   Higher-level modules depend on the EmployeeProfileDAO interface,
 *   not this concrete implementation.
 *
 * NOTE:
 * This implementation is mainly for testing/development purposes and
 * can later be replaced with a real database-backed DAO.
 */
public class EmployeeProfileDAOImpl implements EmployeeProfileDAO {

    /**
     * Saves a new EmployeeProfile into the mock database.
     *
     * FUNCTIONALITY:
     * - Inserts a new profile OR overwrites if ID already exists
     *
     * EXCEPTION HANDLING NOTE:
     * - No validation is done here (assumed to be handled in service layer)
     */
    @Override
    public void save(Employee profile) {
        Transaction tx = null;
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.merge(profile);
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) try { tx.rollback(); } catch (Exception ignored) {}
            throw ex;
        }
    }

    /**
     * Fetches an EmployeeProfile by employeeId.
     *
     * RETURNS:
     * - EmployeeProfile if found
     * - null if not found
     *
     * DESIGN NOTE:
     * - Caller must handle null (avoids forcing exception at DAO layer)
     */
    @Override
    public Employee findById(String employeeId) {
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            return session.get(Employee.class, employeeId);
        }
    }

    /**
     * Retrieves all EmployeeProfiles.
     *
     * RETURNS:
     * - Collection of all stored profiles
     *
     * STRUCTURAL NOTE:
     * - Returning Collection instead of specific List → promotes flexibility
     */
    @Override
    public List<Employee> findAll() {
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            return session.createQuery("FROM Employee e ORDER BY e.empId ASC", Employee.class)
                    .getResultList();
        } catch (Exception ex) {
            return Collections.emptyList();
        }
    }

    /**
     * Updates an existing EmployeeProfile.
     *
     * FUNCTIONALITY:
     * - Overwrites the existing profile with the same employeeId
     *
     * ASSUMPTION:
     * - Profile exists (validation should be done before calling this method)
     */
    @Override
    public void update(Employee profile) {
        Transaction tx = null;
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.merge(profile);
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) try { tx.rollback(); } catch (Exception ignored) {}
            throw ex;
        }
    }

    /**
     * Deletes an EmployeeProfile using employeeId.
     *
     * BEHAVIOR:
     * - If ID does not exist → no action (safe delete)
     */
    @Override
    public void delete(String employeeId) {
        if (employeeId == null) return;
        Transaction tx = null;
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            Employee e = session.get(Employee.class, employeeId);
            if (e != null) session.remove(e);
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) try { tx.rollback(); } catch (Exception ignored) {}
            throw ex;
        }
    }

    /**
     * Checks whether an EmployeeProfile exists.
     *
     * RETURNS:
     * - true  → if employee exists
     * - false → otherwise
     *
     * USE CASE:
     * - Validation before update/delete operations
     */
    @Override
    public boolean existsById(String employeeId) {
        if (employeeId == null) return false;
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            return session.get(Employee.class, employeeId) != null;
        }
    }
}