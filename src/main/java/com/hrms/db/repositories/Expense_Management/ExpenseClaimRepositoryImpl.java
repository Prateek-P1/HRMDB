package com.hrms.db.repositories.Expense_Management;

import com.hrms.db.config.DatabaseConnection;
import com.pesu.expensesubsystem.entity.ExpenseClaim;
import com.pesu.expensesubsystem.enums.ClaimStatus;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class ExpenseClaimRepositoryImpl implements ClaimRepository {

    @Override
    public ExpenseClaim save(ExpenseClaim claim) {
        if (claim == null) throw new IllegalArgumentException("claim is null");
        Transaction tx = null;
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            com.hrms.db.entities.ExpenseClaim entity = ExpenseRepoMapper.toEntityClaim(claim, session);
            session.merge(entity);
            tx.commit();
            return ExpenseRepoMapper.toDtoClaim(entity);
        } catch (Exception ex) {
            if (tx != null) try { tx.rollback(); } catch (Exception ignored) {}
            throw ex;
        }
    }

    @Override
    public Optional<ExpenseClaim> findById(Long claimId) {
        if (claimId == null) return Optional.empty();
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            com.hrms.db.entities.ExpenseClaim entity = session.get(com.hrms.db.entities.ExpenseClaim.class, String.valueOf(claimId));
            return Optional.ofNullable(ExpenseRepoMapper.toDtoClaim(entity));
        }
    }

    @Override
    public List<ExpenseClaim> findByEmployeeId(String employeeId) {
        if (employeeId == null) return Collections.emptyList();
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            List<com.hrms.db.entities.ExpenseClaim> entities = session
                    .createQuery("FROM ExpenseClaim c WHERE c.employee.empId = :id ORDER BY c.createdAt DESC", com.hrms.db.entities.ExpenseClaim.class)
                    .setParameter("id", employeeId)
                    .getResultList();
            List<ExpenseClaim> out = new ArrayList<>(entities.size());
            for (com.hrms.db.entities.ExpenseClaim c : entities) out.add(ExpenseRepoMapper.toDtoClaim(c));
            return out;
        } catch (Exception ex) {
            return Collections.emptyList();
        }
    }

    @Override
    public List<ExpenseClaim> findByStatus(ClaimStatus status) {
        if (status == null) return Collections.emptyList();
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            List<com.hrms.db.entities.ExpenseClaim> entities = session
                    .createQuery("FROM ExpenseClaim c WHERE c.status = :s ORDER BY c.createdAt DESC", com.hrms.db.entities.ExpenseClaim.class)
                    .setParameter("s", status.name())
                    .getResultList();
            List<ExpenseClaim> out = new ArrayList<>(entities.size());
            for (com.hrms.db.entities.ExpenseClaim c : entities) out.add(ExpenseRepoMapper.toDtoClaim(c));
            return out;
        } catch (Exception ex) {
            return Collections.emptyList();
        }
    }

    @Override
    public List<ExpenseClaim> findAll() {
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            List<com.hrms.db.entities.ExpenseClaim> entities = session
                    .createQuery("FROM ExpenseClaim c ORDER BY c.createdAt DESC", com.hrms.db.entities.ExpenseClaim.class)
                    .getResultList();
            List<ExpenseClaim> out = new ArrayList<>(entities.size());
            for (com.hrms.db.entities.ExpenseClaim c : entities) out.add(ExpenseRepoMapper.toDtoClaim(c));
            return out;
        } catch (Exception ex) {
            return Collections.emptyList();
        }
    }

    @Override
    public void update(ExpenseClaim claim) {
        save(claim);
    }

    @Override
    public void delete(Long claimId) {
        if (claimId == null) return;
        Transaction tx = null;
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            com.hrms.db.entities.ExpenseClaim entity = session.get(com.hrms.db.entities.ExpenseClaim.class, String.valueOf(claimId));
            if (entity != null) session.remove(entity);
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) try { tx.rollback(); } catch (Exception ignored) {}
            throw ex;
        }
    }

    @Override
    public List<ExpenseClaim> findPendingClaimsOlderThan(LocalDate date) {
        if (date == null) return Collections.emptyList();
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            List<com.hrms.db.entities.ExpenseClaim> entities = session
                    .createQuery(
                            "FROM ExpenseClaim c WHERE c.status = 'PENDING' AND c.expenseDate < :d ORDER BY c.expenseDate ASC",
                            com.hrms.db.entities.ExpenseClaim.class)
                    .setParameter("d", date)
                    .getResultList();
            List<ExpenseClaim> out = new ArrayList<>(entities.size());
            for (com.hrms.db.entities.ExpenseClaim c : entities) out.add(ExpenseRepoMapper.toDtoClaim(c));
            return out;
        } catch (Exception ex) {
            return Collections.emptyList();
        }
    }
}
