package com.hrms.db.repositories.benefits;

import com.hrms.db.config.DatabaseConnection;
import com.hrms.db.entities.SecurityAuditLog;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.Collections;
import java.util.List;

/**
 * Hibernate-backed implementation of {@link AuditLogDAO} for Benefits Administration.
 *
 * Notes:
 * - We store audit events in the shared {@code security_audit_logs} table via {@link SecurityAuditLog}.
 * - {@code employeeId} in the Benefits context maps to {@link SecurityAuditLog#getUserId()}.
 */
public class AuditLogDAOImpl implements AuditLogDAO {

    @Override
    public SecurityAuditLog save(SecurityAuditLog log) {
        Transaction tx = null;
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.persist(log);
            tx.commit();
            return log;
        } catch (Exception ex) {
            if (tx != null) try { tx.rollback(); } catch (Exception ignored) {}
            throw ex;
        }
    }

    @Override
    public SecurityAuditLog findById(Long logId) {
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            return session.get(SecurityAuditLog.class, logId);
        }
    }

    @Override
    public List<SecurityAuditLog> findAll() {
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            return session.createQuery(
                    "FROM SecurityAuditLog l ORDER BY l.timestamp DESC",
                    SecurityAuditLog.class
            ).getResultList();
        } catch (Exception ex) {
            return Collections.emptyList();
        }
    }

    @Override
    public List<SecurityAuditLog> findByEmployeeId(String employeeId) {
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            return session.createQuery(
                            "FROM SecurityAuditLog l WHERE l.userId = :id ORDER BY l.timestamp DESC",
                            SecurityAuditLog.class)
                    .setParameter("id", employeeId)
                    .getResultList();
        } catch (Exception ex) {
            return Collections.emptyList();
        }
    }

    @Override
    public List<SecurityAuditLog> findByActionType(String actionType) {
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            return session.createQuery(
                            "FROM SecurityAuditLog l WHERE l.actionType = :t ORDER BY l.timestamp DESC",
                            SecurityAuditLog.class)
                    .setParameter("t", actionType)
                    .getResultList();
        } catch (Exception ex) {
            return Collections.emptyList();
        }
    }
}
