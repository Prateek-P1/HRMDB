package com.hrms.db.entities;

import jakarta.persistence.*;
import java.time.LocalDate;

/**
 * Individual tasks within a development plan.
 */
@Entity
@Table(name = "plan_tasks")
public class PlanTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "task_id")
    private Long taskId;

    @Column(name = "plan_id", nullable = false)
    private Long planId;

    @Column(name = "task_description", columnDefinition = "TEXT")
    private String taskDescription;

    @Column(name = "task_due_date")
    private LocalDate taskDueDate;

    @Column(name = "task_status", length = 30)
    private String taskStatus = "PENDING"; // PENDING, IN_PROGRESS, COMPLETED, OVERDUE

    // --- Getters & Setters ---

    public Long getTaskId() { return taskId; }
    public void setTaskId(Long taskId) { this.taskId = taskId; }

    public Long getPlanId() { return planId; }
    public void setPlanId(Long planId) { this.planId = planId; }

    public String getTaskDescription() { return taskDescription; }
    public void setTaskDescription(String taskDescription) { this.taskDescription = taskDescription; }

    public LocalDate getTaskDueDate() { return taskDueDate; }
    public void setTaskDueDate(LocalDate taskDueDate) { this.taskDueDate = taskDueDate; }

    public String getTaskStatus() { return taskStatus; }
    public void setTaskStatus(String taskStatus) { this.taskStatus = taskStatus; }
}
