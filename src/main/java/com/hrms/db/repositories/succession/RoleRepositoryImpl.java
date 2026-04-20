package com.hrms.db.repositories.succession;

import com.hrms.db.config.DatabaseConnection;
import com.hrms.db.entities.CriticalRole;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class RoleRepositoryImpl implements IRoleRepository {

    @Override
    public CriticalRole save(CriticalRole role) {
        if (role == null) return null;

        Transaction tx = null;
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            CriticalRole merged = session.merge(role);
            tx.commit();
            return merged;
        } catch (Exception ex) {
            if (tx != null) try { tx.rollback(); } catch (Exception ignored) {}
            throw ex;
        }
    }

    @Override
    public Optional<CriticalRole> findById(Integer roleId) {
        if (roleId == null) return Optional.empty();
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            return Optional.ofNullable(session.get(CriticalRole.class, roleId));
        }
    }

    @Override
    public List<CriticalRole> findAll() {
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            return session.createQuery("FROM CriticalRole r ORDER BY r.roleName ASC", CriticalRole.class)
                    .getResultList();
        } catch (Exception ex) {
            return Collections.emptyList();
        }
    }

    @Override
    public List<CriticalRole> findByCriticality(String criticality) {
        if (criticality == null) return Collections.emptyList();
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            return session.createQuery(
                            "FROM CriticalRole r WHERE r.criticality = :c ORDER BY r.roleName ASC",
                            CriticalRole.class)
                    .setParameter("c", criticality)
                    .getResultList();
        } catch (Exception ex) {
            return Collections.emptyList();
        }
    }

    @Override
    public boolean existsByRoleName(String roleName) {
        if (roleName == null || roleName.isBlank()) return false;
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            Long count = session.createQuery(
                            "SELECT COUNT(r) FROM CriticalRole r WHERE lower(r.roleName) = lower(:n)",
                            Long.class)
                    .setParameter("n", roleName)
                    .uniqueResult();
            return count != null && count > 0;
        }
    }

    @Override
    public void deleteById(Integer roleId) {
        if (roleId == null) return;
        Transaction tx = null;
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            CriticalRole existing = session.get(CriticalRole.class, roleId);
            if (existing != null) session.remove(existing);
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) try { tx.rollback(); } catch (Exception ignored) {}
            throw ex;
        }
    }
}
