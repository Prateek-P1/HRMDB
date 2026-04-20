package com.hrms.db.repositories.succession;

import com.hrms.db.config.DatabaseConnection;
import com.hrms.db.entities.SuccessionPoolEntry;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class SuccessionPoolRepositoryImpl implements ISuccessionPoolRepository {

    @Override
    public SuccessionPoolEntry save(SuccessionPoolEntry entry) {
        if (entry == null) return null;
        Transaction tx = null;
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            SuccessionPoolEntry merged = session.merge(entry);
            tx.commit();
            return merged;
        } catch (Exception ex) {
            if (tx != null) try { tx.rollback(); } catch (Exception ignored) {}
            throw ex;
        }
    }

    @Override
    public Optional<SuccessionPoolEntry> findByEmployeeId(String employeeId) {
        if (employeeId == null) return Optional.empty();
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            SuccessionPoolEntry found = session.createQuery(
                            "FROM SuccessionPoolEntry e WHERE e.empId = :id",
                            SuccessionPoolEntry.class)
                    .setParameter("id", employeeId)
                    .setMaxResults(1)
                    .uniqueResult();
            return Optional.ofNullable(found);
        }
    }

    @Override
    public List<SuccessionPoolEntry> findAllEligible() {
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            return session.createQuery(
                            "FROM SuccessionPoolEntry e WHERE e.eligible = true ORDER BY e.updatedAt DESC",
                            SuccessionPoolEntry.class)
                    .getResultList();
        } catch (Exception ex) {
            return Collections.emptyList();
        }
    }

    @Override
    public List<SuccessionPoolEntry> findAll() {
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            return session.createQuery(
                            "FROM SuccessionPoolEntry e ORDER BY e.updatedAt DESC",
                            SuccessionPoolEntry.class)
                    .getResultList();
        } catch (Exception ex) {
            return Collections.emptyList();
        }
    }
}
