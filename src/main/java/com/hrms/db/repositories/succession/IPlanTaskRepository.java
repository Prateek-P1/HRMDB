package com.hrms.db.repositories.succession;

import com.hrms.db.entities.PlanTask;

import java.util.List;
import java.util.Optional;

public interface IPlanTaskRepository {
    PlanTask save(PlanTask task);
    List<PlanTask> findByPlanId(Long planIdFk);
    Optional<PlanTask> findById(Long planTaskId);
    void updateTaskStatus(Long planTaskId, String taskStatus);
    void markTaskOverdue(Long planTaskId);
    List<PlanTask> findOverdueTasks();
}
