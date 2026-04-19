package com.hrms.db.repositories.benefits;

import com.hrms.db.config.DatabaseConnection;
import com.hrms.db.entities.BenefitPolicy;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.Collections;
import java.util.List;

/**
 * Stub implementation of BenefitPolicyDAO.
 *
 * ─────────────────────────────────────────────────────────────────
 * TO THE DATABASE TEAM:
 * This file is yours to implement. Replace each method body
 * with your actual database queries.
 * (JDBC, Hibernate, JPA, or any ORM of your choice)
 *
 * We have defined the full contract in BenefitPolicyDAO.java.
 * You only need to fulfill it here.
 *
 * Key notes:
 *   → findById()     : return null if record not found
 *   → findAll()      : return empty collection if no records
 *   → findAllActive(): return only policies where active = true
 *   → existsById()   : return true/false only, no exceptions
 *
 * Do NOT change method signatures under any circumstance.
 * Our business logic layer depends on them exactly as written.
 * ─────────────────────────────────────────────────────────────────
 *
 * SOLID - Open/Closed Principle (OCP):
 * Our policy facade is completely closed for modification.
 * The DB team fills this in without touching anything else.
 *
 * SOLID - Dependency Inversion Principle (DIP):
 * We depend on BenefitPolicyDAO (abstraction), not this class.
 * This class is injected at runtime via the facade constructor.
 */
public class BenefitPolicyDAOImpl implements BenefitPolicyDAO {

    @Override
    public void save(BenefitPolicy policy) {
        Transaction tx = null;
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.persist(policy);
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) try { tx.rollback(); } catch (Exception ignored) {}
            throw ex;
        }
    }

    @Override
    public BenefitPolicy findById(Integer policyId) {
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            return session.get(BenefitPolicy.class, policyId);
        }
    }

    @Override
    public List<BenefitPolicy> findAll() {
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            return session.createQuery("FROM BenefitPolicy p ORDER BY p.policyName ASC", BenefitPolicy.class)
                    .getResultList();
        } catch (Exception ex) {
            return Collections.emptyList();
        }
    }

    @Override
    public List<BenefitPolicy> findAllActive() {
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            return session.createQuery(
                            "FROM BenefitPolicy p WHERE p.isActive = true ORDER BY p.policyName ASC",
                            BenefitPolicy.class)
                    .getResultList();
        } catch (Exception ex) {
            return Collections.emptyList();
        }
    }

    @Override
    public void update(BenefitPolicy policy) {
        Transaction tx = null;
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.merge(policy);
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) try { tx.rollback(); } catch (Exception ignored) {}
            throw ex;
        }
    }

    @Override
    public void delete(Integer policyId) {
        if (policyId == null) return;
        Transaction tx = null;
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            BenefitPolicy p = session.get(BenefitPolicy.class, policyId);
            if (p != null) session.remove(p);
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) try { tx.rollback(); } catch (Exception ignored) {}
            throw ex;
        }
    }

    @Override
    public boolean existsById(Integer policyId) {
        if (policyId == null) return false;
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            return session.get(BenefitPolicy.class, policyId) != null;
        }
    }
}
