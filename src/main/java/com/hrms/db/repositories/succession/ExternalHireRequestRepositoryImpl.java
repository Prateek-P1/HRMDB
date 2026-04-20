package com.hrms.db.repositories.succession;

import com.hrms.db.config.DatabaseConnection;
import com.hrms.db.entities.ExternalHireRequest;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class ExternalHireRequestRepositoryImpl implements IExternalHireRequestRepository {

    @Override
    public ExternalHireRequest save(ExternalHireRequest request) {
        if (request == null) return null;
        Transaction tx = null;
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            ExternalHireRequest merged = session.merge(request);
            tx.commit();
            return merged;
        } catch (Exception ex) {
            if (tx != null) try { tx.rollback(); } catch (Exception ignored) {}
            throw ex;
        }
    }

    @Override
    public Optional<ExternalHireRequest> findById(Long extHireRequestId) {
        if (extHireRequestId == null) return Optional.empty();
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            return Optional.ofNullable(session.get(ExternalHireRequest.class, extHireRequestId));
        }
    }

    @Override
    public List<ExternalHireRequest> findByStatus(String requestStatus) {
        if (requestStatus == null) return Collections.emptyList();
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            return session.createQuery(
                            "FROM ExternalHireRequest r WHERE r.requestStatus = :st ORDER BY r.updatedAt DESC",
                            ExternalHireRequest.class)
                    .setParameter("st", requestStatus)
                    .getResultList();
        } catch (Exception ex) {
            return Collections.emptyList();
        }
    }

    @Override
    public void updateStatus(Long extHireRequestId, String requestStatus) {
        if (extHireRequestId == null || requestStatus == null) return;
        Transaction tx = null;
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            ExternalHireRequest r = session.get(ExternalHireRequest.class, extHireRequestId);
            if (r != null) {
                r.setRequestStatus(requestStatus);
                session.merge(r);
            }
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) try { tx.rollback(); } catch (Exception ignored) {}
            throw ex;
        }
    }

    @Override
    public void incrementRetryCount(Long extHireRequestId) {
        if (extHireRequestId == null) return;
        Transaction tx = null;
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.createMutationQuery(
                            "UPDATE ExternalHireRequest r SET r.retryCount = COALESCE(r.retryCount, 0) + 1 WHERE r.extHireRequestId = :id")
                    .setParameter("id", extHireRequestId)
                    .executeUpdate();
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) try { tx.rollback(); } catch (Exception ignored) {}
            throw ex;
        }
    }
}
