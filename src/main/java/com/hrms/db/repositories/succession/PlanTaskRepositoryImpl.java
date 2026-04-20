package com.hrms.db.repositories.succession;

import com.hrms.db.config.DatabaseConnection;
import com.hrms.db.entities.PlanTask;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class PlanTaskRepositoryImpl implements IPlanTaskRepository {

    @Override
    public PlanTask save(PlanTask task) {
        if (task == null) return null;
        Transaction tx = null;
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            PlanTask merged = session.merge(task);
            tx.commit();
            return merged;
        } catch (Exception ex) {
            if (tx != null) try { tx.rollback(); } catch (Exception ignored) {}
            throw ex;
        }
    }

    @Override
    public List<PlanTask> findByPlanId(Long planIdFk) {
        if (planIdFk == null) return Collections.emptyList();
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            return session.createQuery(
                            "FROM PlanTask t WHERE t.planId = :pid ORDER BY t.taskDueDate ASC",
                            PlanTask.class)
                    .setParameter("pid", planIdFk)
                    .getResultList();
        } catch (Exception ex) {
            return Collections.emptyList();
        }
    }

    @Override
    public Optional<PlanTask> findById(Long planTaskId) {
        if (planTaskId == null) return Optional.empty();
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            return Optional.ofNullable(session.get(PlanTask.class, planTaskId));
        }
    }

    @Override
    public void updateTaskStatus(Long planTaskId, String taskStatus) {
        if (planTaskId == null || taskStatus == null) return;
        Transaction tx = null;
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            PlanTask task = session.get(PlanTask.class, planTaskId);
            if (task != null) {
                task.setTaskStatus(taskStatus);
                session.merge(task);
            }
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) try { tx.rollback(); } catch (Exception ignored) {}
            throw ex;
        }
    }

    @Override
    public void markTaskOverdue(Long planTaskId) {
        updateTaskStatus(planTaskId, "OVERDUE");
    }

    @Override
    public List<PlanTask> findOverdueTasks() {
        LocalDate today = LocalDate.now();
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            return session.createQuery(
                            "FROM PlanTask t WHERE t.taskStatus = 'OVERDUE' OR (t.taskDueDate < :today AND t.taskStatus <> 'COMPLETED') ORDER BY t.taskDueDate ASC",
                            PlanTask.class)
                    .setParameter("today", today)
                    .getResultList();
        } catch (Exception ex) {
            return Collections.emptyList();
        }
    }
}
