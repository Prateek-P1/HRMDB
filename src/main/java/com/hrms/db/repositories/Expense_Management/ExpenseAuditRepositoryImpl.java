package com.hrms.db.repositories.Expense_Management;

import com.hrms.db.config.DatabaseConnection;
import com.pesu.expensesubsystem.entity.AuditLog;
import com.pesu.expensesubsystem.enums.ActionType;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

public class ExpenseAuditRepositoryImpl implements AuditRepository {

    @Override
    public AuditLog save(AuditLog auditLog) {
        if (auditLog == null) throw new IllegalArgumentException("auditLog is null");
        Transaction tx = null;
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            com.hrms.db.entities.ExpenseAuditLog entity = ExpenseRepoMapper.toEntityAuditLog(auditLog, session);
            session.persist(entity);
            tx.commit();
            return ExpenseRepoMapper.toDtoAuditLog(entity);
        } catch (Exception ex) {
            if (tx != null) try { tx.rollback(); } catch (Exception ignored) {}
            throw ex;
        }
    }

    @Override
    public List<AuditLog> findByEmployeeId(String employeeId) {
        if (employeeId == null) return Collections.emptyList();
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            List<com.hrms.db.entities.ExpenseAuditLog> entities = session
                    .createQuery(
                            "FROM ExpenseAuditLog a WHERE a.employee.empId = :id ORDER BY a.ts DESC",
                            com.hrms.db.entities.ExpenseAuditLog.class)
                    .setParameter("id", employeeId)
                    .getResultList();
            return ExpenseRepoMapper.mapAuditLogs(entities);
        } catch (Exception ex) {
            return Collections.emptyList();
        }
    }

    @Override
    public List<AuditLog> findByClaimId(Long claimId) {
        if (claimId == null) return Collections.emptyList();
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            List<com.hrms.db.entities.ExpenseAuditLog> entities = session
                    .createQuery(
                            "FROM ExpenseAuditLog a WHERE a.claimId = :cid ORDER BY a.ts DESC",
                            com.hrms.db.entities.ExpenseAuditLog.class)
                    .setParameter("cid", String.valueOf(claimId))
                    .getResultList();
            return ExpenseRepoMapper.mapAuditLogs(entities);
        } catch (Exception ex) {
            return Collections.emptyList();
        }
    }

    @Override
    public List<AuditLog> findByAction(ActionType action) {
        if (action == null) return Collections.emptyList();
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            List<com.hrms.db.entities.ExpenseAuditLog> entities = session
                    .createQuery(
                            "FROM ExpenseAuditLog a WHERE a.actionType = :t ORDER BY a.ts DESC",
                            com.hrms.db.entities.ExpenseAuditLog.class)
                    .setParameter("t", action.name())
                    .getResultList();
            return ExpenseRepoMapper.mapAuditLogs(entities);
        } catch (Exception ex) {
            return Collections.emptyList();
        }
    }

    @Override
    public List<AuditLog> findByDateRange(LocalDateTime from, LocalDateTime to) {
        if (from == null || to == null) return Collections.emptyList();
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            List<com.hrms.db.entities.ExpenseAuditLog> entities = session
                    .createQuery(
                            "FROM ExpenseAuditLog a WHERE a.ts >= :f AND a.ts <= :t ORDER BY a.ts DESC",
                            com.hrms.db.entities.ExpenseAuditLog.class)
                    .setParameter("f", from)
                    .setParameter("t", to)
                    .getResultList();
            return ExpenseRepoMapper.mapAuditLogs(entities);
        } catch (Exception ex) {
            return Collections.emptyList();
        }
    }

    @Override
    public List<AuditLog> findAll() {
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            List<com.hrms.db.entities.ExpenseAuditLog> entities = session
                    .createQuery("FROM ExpenseAuditLog a ORDER BY a.ts DESC", com.hrms.db.entities.ExpenseAuditLog.class)
                    .getResultList();
            return ExpenseRepoMapper.mapAuditLogs(entities);
        } catch (Exception ex) {
            return Collections.emptyList();
        }
    }
}
