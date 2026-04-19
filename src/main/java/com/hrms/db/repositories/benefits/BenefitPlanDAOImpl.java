package com.hrms.db.repositories.benefits;

import com.hrms.db.config.DatabaseConnection;
import com.hrms.db.entities.BenefitPlan;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.Collections;
import java.util.List;

/** Hibernate-backed BenefitPlan DAO using the central {@link BenefitPlan} entity. */
public class BenefitPlanDAOImpl implements BenefitPlanDAO {

    @Override
    public void save(BenefitPlan plan) {
        Transaction tx = null;
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.persist(plan);
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) try { tx.rollback(); } catch (Exception ignored) {}
            throw ex;
        }
    }

    @Override
    public BenefitPlan findById(Integer planId) {
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            return session.get(BenefitPlan.class, planId);
        }
    }

    @Override
    public List<BenefitPlan> findAll() {
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            return session.createQuery("FROM BenefitPlan p ORDER BY p.planName ASC", BenefitPlan.class)
                    .getResultList();
        } catch (Exception ex) {
            return Collections.emptyList();
        }
    }

    @Override
    public List<BenefitPlan> findByType(String planType) {
        if (planType == null) return Collections.emptyList();
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            return session.createQuery(
                            "FROM BenefitPlan p WHERE p.planType = :t ORDER BY p.planName ASC",
                            BenefitPlan.class)
                    .setParameter("t", planType)
                    .getResultList();
        } catch (Exception ex) {
            return Collections.emptyList();
        }
    }

    @Override
    public void update(BenefitPlan plan) {
        Transaction tx = null;
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.merge(plan);
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) try { tx.rollback(); } catch (Exception ignored) {}
            throw ex;
        }
    }

    @Override
    public void delete(Integer planId) {
        if (planId == null) return;
        Transaction tx = null;
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            BenefitPlan plan = session.get(BenefitPlan.class, planId);
            if (plan != null) session.remove(plan);
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) try { tx.rollback(); } catch (Exception ignored) {}
            throw ex;
        }
    }

    @Override
    public boolean existsById(Integer planId) {
        if (planId == null) return false;
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            return session.get(BenefitPlan.class, planId) != null;
        }
    }
}