package com.hrms.db.repositories.succession;

import com.hrms.db.config.DatabaseConnection;
import com.hrms.db.entities.DevelopmentPlan;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class DevelopmentPlanRepositoryImpl implements IDevelopmentPlanRepository {

    @Override
    public DevelopmentPlan save(DevelopmentPlan plan) {
        if (plan == null) return null;
        Transaction tx = null;
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            DevelopmentPlan merged = session.merge(plan);
            tx.commit();
            return merged;
        } catch (Exception ex) {
            if (tx != null) try { tx.rollback(); } catch (Exception ignored) {}
            throw ex;
        }
    }

    @Override
    public Optional<DevelopmentPlan> findById(Long planId) {
        if (planId == null) return Optional.empty();
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            return Optional.ofNullable(session.get(DevelopmentPlan.class, planId));
        }
    }

    @Override
    public List<DevelopmentPlan> findByAssignmentIdFk(Long assignmentIdFk) {
        if (assignmentIdFk == null) return Collections.emptyList();
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            return session.createQuery(
                            "FROM DevelopmentPlan p WHERE p.assignmentIdFk = :aid ORDER BY p.planId DESC",
                            DevelopmentPlan.class)
                    .setParameter("aid", assignmentIdFk)
                    .getResultList();
        } catch (Exception ex) {
            return Collections.emptyList();
        }
    }

    @Override
    public void deletePlan(Long planId) {
        if (planId == null) return;
        Transaction tx = null;
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            DevelopmentPlan existing = session.get(DevelopmentPlan.class, planId);
            if (existing != null) session.remove(existing);
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) try { tx.rollback(); } catch (Exception ignored) {}
            throw ex;
        }
    }

    @Override
    public List<DevelopmentPlan> findAllActive() {
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            return session.createQuery(
                            "FROM DevelopmentPlan p WHERE p.progressPercentage IS NULL OR p.progressPercentage < 100.0 ORDER BY p.planId DESC",
                            DevelopmentPlan.class)
                    .getResultList();
        } catch (Exception ex) {
            return Collections.emptyList();
        }
    }
}
