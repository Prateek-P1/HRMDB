package com.hrms.db.repositories.Expense_Management;

import com.hrms.db.config.DatabaseConnection;
import com.pesu.expensesubsystem.entity.DepartmentBudget;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.math.BigDecimal;
import java.util.Optional;

public class ExpenseBudgetRepositoryImpl implements BudgetRepository {

    @Override
    public Optional<DepartmentBudget> findByDepartment(String departmentName) {
        if (departmentName == null) return Optional.empty();
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            com.hrms.db.entities.DepartmentBudget entity = session
                    .createQuery(
                            "FROM DepartmentBudget b WHERE b.departmentName = :d",
                            com.hrms.db.entities.DepartmentBudget.class)
                    .setParameter("d", departmentName)
                    .uniqueResult();
            return Optional.ofNullable(ExpenseRepoMapper.toDtoBudget(entity));
        }
    }

    @Override
    public DepartmentBudget save(DepartmentBudget budget) {
        if (budget == null) throw new IllegalArgumentException("budget is null");
        if (budget.getDepartmentName() == null) throw new IllegalArgumentException("departmentName is null");

        Transaction tx = null;
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            com.hrms.db.entities.DepartmentBudget entity = ExpenseRepoMapper.findBudgetEntity(session, budget.getDepartmentName());
            if (entity == null) entity = new com.hrms.db.entities.DepartmentBudget();

            entity.setDepartmentName(budget.getDepartmentName());
            entity.setBudgetLimit(ExpenseRepoMapper.toDouble(budget.getBudgetLimit()));
            entity.setCurrentSpent(ExpenseRepoMapper.toDouble(budget.getCurrentSpent()));

            session.merge(entity);
            tx.commit();
            return ExpenseRepoMapper.toDtoBudget(entity);
        } catch (Exception ex) {
            if (tx != null) try { tx.rollback(); } catch (Exception ignored) {}
            throw ex;
        }
    }

    @Override
    public void updateBudget(String departmentName, BigDecimal newSpent) {
        if (departmentName == null) return;
        Transaction tx = null;
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            com.hrms.db.entities.DepartmentBudget entity = ExpenseRepoMapper.findBudgetEntity(session, departmentName);
            if (entity != null) {
                entity.setCurrentSpent(ExpenseRepoMapper.toDouble(newSpent));
                session.merge(entity);
            }
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) try { tx.rollback(); } catch (Exception ignored) {}
            throw ex;
        }
    }

    @Override
    public boolean existsByDepartment(String departmentName) {
        if (departmentName == null) return false;
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            Long count = session.createQuery(
                            "SELECT COUNT(b) FROM DepartmentBudget b WHERE b.departmentName = :d",
                            Long.class)
                    .setParameter("d", departmentName)
                    .uniqueResult();
            return count != null && count > 0;
        }
    }
}
