package com.hrms.db.repositories.succession;

import com.hrms.db.config.DatabaseConnection;
import com.hrms.db.entities.SuccessorAssignment;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.Collections;
import java.util.List;

public class SuccessorAssignmentRepositoryImpl implements ISuccessorAssignmentRepository {

    @Override
    public SuccessorAssignment save(SuccessorAssignment assignment) {
        if (assignment == null) return null;
        Transaction tx = null;
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            SuccessorAssignment merged = session.merge(assignment);
            tx.commit();
            return merged;
        } catch (Exception ex) {
            if (tx != null) try { tx.rollback(); } catch (Exception ignored) {}
            throw ex;
        }
    }

    @Override
    public List<SuccessorAssignment> findRankedByRole(Integer roleIdFk) {
        if (roleIdFk == null) return Collections.emptyList();
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            return session.createQuery(
                            "FROM SuccessorAssignment a WHERE a.targetRoleId = :rid ORDER BY a.successorRank ASC",
                            SuccessorAssignment.class)
                    .setParameter("rid", roleIdFk)
                    .getResultList();
        } catch (Exception ex) {
            return Collections.emptyList();
        }
    }

    @Override
    public List<SuccessorAssignment> findByEmployeeIdFk(String empIdFk) {
        if (empIdFk == null) return Collections.emptyList();
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            return session.createQuery(
                            "FROM SuccessorAssignment a WHERE a.empId = :eid ORDER BY a.assignmentDate DESC",
                            SuccessorAssignment.class)
                    .setParameter("eid", empIdFk)
                    .getResultList();
        } catch (Exception ex) {
            return Collections.emptyList();
        }
    }

    @Override
    public boolean existsConflictingAssignment(String empIdFk, Integer roleIdFk) {
        if (empIdFk == null || roleIdFk == null) return false;
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            Long count = session.createQuery(
                            "SELECT COUNT(a) FROM SuccessorAssignment a WHERE a.empId = :eid AND a.targetRoleId = :rid",
                            Long.class)
                    .setParameter("eid", empIdFk)
                    .setParameter("rid", roleIdFk)
                    .uniqueResult();
            return count != null && count > 0;
        }
    }

    @Override
    public void deleteById(Long assignmentId) {
        if (assignmentId == null) return;
        Transaction tx = null;
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            SuccessorAssignment existing = session.get(SuccessorAssignment.class, assignmentId);
            if (existing != null) session.remove(existing);
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) try { tx.rollback(); } catch (Exception ignored) {}
            throw ex;
        }
    }

    @Override
    public List<SuccessorAssignment> findApprovedByRole(Integer roleIdFk) {
        if (roleIdFk == null) return Collections.emptyList();
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            return session.createQuery(
                            "FROM SuccessorAssignment a WHERE a.targetRoleId = :rid AND (a.hrDecision = 'CONFIRMED' OR a.hrDecision = 'APPROVED') ORDER BY a.successorRank ASC",
                            SuccessorAssignment.class)
                    .setParameter("rid", roleIdFk)
                    .getResultList();
        } catch (Exception ex) {
            return Collections.emptyList();
        }
    }
}
