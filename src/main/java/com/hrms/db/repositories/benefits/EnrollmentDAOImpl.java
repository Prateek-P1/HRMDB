package com.hrms.db.repositories.benefits;

import com.hrms.db.config.DatabaseConnection;
import com.hrms.db.entities.BenefitEnrollment;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.Collections;
import java.util.List;

/**
 * In-memory implementation of EnrollmentDAO.
 *
 * Uses a HashMap to store enrollments during the program's runtime.
 * This allows the system to run and be demonstrated without a real
 * database connection, consistent with how Poorav structured
 * EmployeeProfileDAOImpl and BenefitPlanDAOImpl.
 *
 * ─────────────────────────────────────────────────────────────────
 * NOTE TO DATABASE TEAM:
 * When the real DB is ready, replace this implementation with a
 * JDBC/JPA version. The EnrollmentDAO interface contract does not
 * change — just the implementation body.
 *
 * Do NOT change method signatures — our service depends on them.
 * ─────────────────────────────────────────────────────────────────
 *
 * SOLID - Open/Closed Principle (OCP):
 * EnrollmentServiceImpl is closed for modification — it depends on
 * EnrollmentDAO. The DB team can swap this impl freely.
 *
 * SOLID - Single Responsibility Principle (SRP):
 * This class is solely responsible for in-memory data storage
 * and retrieval of Enrollment objects.
 */
public class EnrollmentDAOImpl implements EnrollmentDAO {

    // ── Save ─────────────────────────────────────────────────────────────────

    @Override
    public void save(BenefitEnrollment enrollment) {
        Transaction tx = null;
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.persist(enrollment);
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) try { tx.rollback(); } catch (Exception ignored) {}
            throw ex;
        }
    }

    // ── Find by ID ───────────────────────────────────────────────────────────

    @Override
    public BenefitEnrollment findById(Long enrollmentId) {
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            return session.get(BenefitEnrollment.class, enrollmentId);
        }
    }

    // ── Find by Employee ─────────────────────────────────────────────────────

    @Override
    public List<BenefitEnrollment> findByEmployeeId(String employeeId) {
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            return session.createQuery(
                            "FROM BenefitEnrollment e WHERE e.empId = :id ORDER BY e.enrollmentDate DESC",
                            BenefitEnrollment.class)
                    .setParameter("id", employeeId)
                    .getResultList();
        } catch (Exception ex) {
            return Collections.emptyList();
        }
    }

    // ── Find All ─────────────────────────────────────────────────────────────

    @Override
    public List<BenefitEnrollment> findAll() {
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            return session.createQuery(
                    "FROM BenefitEnrollment e ORDER BY e.enrollmentDate DESC",
                    BenefitEnrollment.class
            ).getResultList();
        } catch (Exception ex) {
            return Collections.emptyList();
        }
    }

    // ── Update ───────────────────────────────────────────────────────────────

    @Override
    public void update(BenefitEnrollment enrollment) {
        Transaction tx = null;
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.merge(enrollment);
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) try { tx.rollback(); } catch (Exception ignored) {}
            throw ex;
        }
    }

    // ── Exists by Employee + Plan ─────────────────────────────────────────────

    @Override
    public boolean existsByEmployeeAndPlan(String employeeId, Integer planId) {
        if (employeeId == null || planId == null) return false;
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            Long count = session.createQuery(
                            "SELECT COUNT(e) FROM BenefitEnrollment e WHERE e.empId = :id AND e.planId = :pid",
                            Long.class)
                    .setParameter("id", employeeId)
                    .setParameter("pid", planId)
                    .uniqueResult();
            return count != null && count > 0;
        } catch (Exception ex) {
            return false;
        }
    }
}
