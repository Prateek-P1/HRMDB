package com.hrms.db.repositories.succession;

import com.hrms.db.config.DatabaseConnection;
import com.hrms.db.entities.RiskLog;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

public class RiskLogRepositoryImpl implements IRiskLogRepository {

    @Override
    public RiskLog save(RiskLog riskLog) {
        if (riskLog == null) return null;
        Transaction tx = null;
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            RiskLog merged = session.merge(riskLog);
            tx.commit();
            return merged;
        } catch (Exception ex) {
            if (tx != null) try { tx.rollback(); } catch (Exception ignored) {}
            throw ex;
        }
    }

    @Override
    public List<RiskLog> findByRoleIdFk(Integer roleIdFk) {
        if (roleIdFk == null) return Collections.emptyList();
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            return session.createQuery(
                            "FROM RiskLog r WHERE r.roleIdFk = :rid ORDER BY r.createdAt DESC",
                            RiskLog.class)
                    .setParameter("rid", roleIdFk)
                    .getResultList();
        } catch (Exception ex) {
            return Collections.emptyList();
        }
    }

    @Override
    public Optional<RiskLog> findLatestByRole(Integer roleIdFk) {
        if (roleIdFk == null) return Optional.empty();
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            RiskLog latest = session.createQuery(
                            "FROM RiskLog r WHERE r.roleIdFk = :rid ORDER BY r.createdAt DESC",
                            RiskLog.class)
                    .setParameter("rid", roleIdFk)
                    .setMaxResults(1)
                    .uniqueResult();
            return Optional.ofNullable(latest);
        }
    }

    @Override
    public List<RiskLog> findLatestRiskPerRole() {
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            List<RiskLog> ordered = session.createQuery(
                            "FROM RiskLog r ORDER BY r.roleIdFk ASC, r.createdAt DESC",
                            RiskLog.class)
                    .getResultList();

            LinkedHashMap<Integer, RiskLog> latestByRole = new LinkedHashMap<>();
            for (RiskLog r : ordered) {
                Integer roleId = r != null ? r.getRoleIdFk() : null;
                if (roleId != null && !latestByRole.containsKey(roleId)) {
                    latestByRole.put(roleId, r);
                }
            }
            return new ArrayList<>(latestByRole.values());
        } catch (Exception ex) {
            return Collections.emptyList();
        }
    }
}
