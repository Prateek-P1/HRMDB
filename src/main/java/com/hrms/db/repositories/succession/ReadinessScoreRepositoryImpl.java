package com.hrms.db.repositories.succession;

import com.hrms.db.config.DatabaseConnection;
import com.hrms.db.entities.ReadinessScore;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class ReadinessScoreRepositoryImpl implements IReadinessScoreRepository {

    @Override
    public ReadinessScore save(ReadinessScore score) {
        if (score == null) return null;
        Transaction tx = null;
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            ReadinessScore merged = session.merge(score);
            tx.commit();
            return merged;
        } catch (Exception ex) {
            if (tx != null) try { tx.rollback(); } catch (Exception ignored) {}
            throw ex;
        }
    }

    @Override
    public Optional<ReadinessScore> findByEmployeeAndRole(String empIdFk, Integer roleIdFk) {
        if (empIdFk == null || roleIdFk == null) return Optional.empty();
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            ReadinessScore found = session.createQuery(
                            "FROM ReadinessScore r WHERE r.empId = :eid AND r.roleId = :rid",
                            ReadinessScore.class)
                    .setParameter("eid", empIdFk)
                    .setParameter("rid", roleIdFk)
                    .setMaxResults(1)
                    .uniqueResult();
            return Optional.ofNullable(found);
        }
    }

    @Override
    public List<ReadinessScore> findByEmployeeIdFk(String empIdFk) {
        if (empIdFk == null) return Collections.emptyList();
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            return session.createQuery(
                            "FROM ReadinessScore r WHERE r.empId = :eid ORDER BY r.scoreId DESC",
                            ReadinessScore.class)
                    .setParameter("eid", empIdFk)
                    .getResultList();
        } catch (Exception ex) {
            return Collections.emptyList();
        }
    }

    @Override
    public List<ReadinessScore> findByRoleIdFk(Integer roleIdFk) {
        if (roleIdFk == null) return Collections.emptyList();
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            return session.createQuery(
                            "FROM ReadinessScore r WHERE r.roleId = :rid ORDER BY r.scoreId DESC",
                            ReadinessScore.class)
                    .setParameter("rid", roleIdFk)
                    .getResultList();
        } catch (Exception ex) {
            return Collections.emptyList();
        }
    }

    @Override
    public void deleteByEmployeeAndRole(String empIdFk, Integer roleIdFk) {
        if (empIdFk == null || roleIdFk == null) return;
        Transaction tx = null;
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.createMutationQuery(
                            "DELETE FROM ReadinessScore r WHERE r.empId = :eid AND r.roleId = :rid")
                    .setParameter("eid", empIdFk)
                    .setParameter("rid", roleIdFk)
                    .executeUpdate();
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) try { tx.rollback(); } catch (Exception ignored) {}
            throw ex;
        }
    }
}
