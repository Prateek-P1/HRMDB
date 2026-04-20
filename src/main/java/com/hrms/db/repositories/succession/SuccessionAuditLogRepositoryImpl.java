package com.hrms.db.repositories.succession;

import com.hrms.db.config.DatabaseConnection;
import com.hrms.db.entities.SuccessionAuditLog;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

public class SuccessionAuditLogRepositoryImpl implements IAuditLogRepository {

    @Override
    public SuccessionAuditLog save(SuccessionAuditLog log) {
        if (log == null) return null;
        Transaction tx = null;
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            SuccessionAuditLog merged = session.merge(log);
            tx.commit();
            return merged;
        } catch (Exception ex) {
            if (tx != null) try { tx.rollback(); } catch (Exception ignored) {}
            throw ex;
        }
    }

    @Override
    public List<SuccessionAuditLog> findByEntityTypeAndEntityId(String entityType, Integer entityId) {
        if (entityType == null || entityId == null) return Collections.emptyList();
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            return session.createQuery(
                            "FROM SuccessionAuditLog l WHERE l.entityType = :t AND l.entityId = :id ORDER BY l.timestamp DESC",
                            SuccessionAuditLog.class)
                    .setParameter("t", entityType)
                    .setParameter("id", entityId)
                    .getResultList();
        } catch (Exception ex) {
            return Collections.emptyList();
        }
    }

    @Override
    public List<SuccessionAuditLog> findByDateRange(LocalDateTime from, LocalDateTime to) {
        if (from == null || to == null) return Collections.emptyList();
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            return session.createQuery(
                            "FROM SuccessionAuditLog l WHERE l.timestamp BETWEEN :f AND :t ORDER BY l.timestamp DESC",
                            SuccessionAuditLog.class)
                    .setParameter("f", from)
                    .setParameter("t", to)
                    .getResultList();
        } catch (Exception ex) {
            return Collections.emptyList();
        }
    }

    @Override
    public List<SuccessionAuditLog> findByDateRange(LocalDateTime from, LocalDateTime to, int offset, int limit) {
        if (from == null || to == null) return Collections.emptyList();
        if (offset < 0) offset = 0;
        if (limit <= 0) limit = 50;

        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            return session.createQuery(
                            "FROM SuccessionAuditLog l WHERE l.timestamp BETWEEN :f AND :t ORDER BY l.timestamp DESC",
                            SuccessionAuditLog.class)
                    .setParameter("f", from)
                    .setParameter("t", to)
                    .setFirstResult(offset)
                    .setMaxResults(limit)
                    .getResultList();
        } catch (Exception ex) {
            return Collections.emptyList();
        }
    }
}
