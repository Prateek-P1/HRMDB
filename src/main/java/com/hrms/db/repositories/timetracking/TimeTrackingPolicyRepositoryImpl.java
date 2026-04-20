package com.hrms.db.repositories.timetracking;

import com.hrms.db.config.DatabaseConnection;
import com.hrms.db.entities.WorkPolicy;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.Collections;
import java.util.List;

public class TimeTrackingPolicyRepositoryImpl implements IPolicyRepository {

    @Override
    public void save(WorkPolicy policy) {
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
    public WorkPolicy findById(Integer policyId) {
        if (policyId == null) return null;
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            return session.get(WorkPolicy.class, policyId);
        }
    }

    @Override
    public List<WorkPolicy> findAll() {
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            return session.createQuery(
                            "FROM WorkPolicy p ORDER BY p.policyName ASC",
                            WorkPolicy.class)
                    .getResultList();
        } catch (Exception ex) {
            return Collections.emptyList();
        }
    }

    @Override
    public void delete(Integer policyId) {
        if (policyId == null) return;
        Transaction tx = null;
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            WorkPolicy existing = session.get(WorkPolicy.class, policyId);
            if (existing != null) session.remove(existing);
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) try { tx.rollback(); } catch (Exception ignored) {}
            throw ex;
        }
    }
}
