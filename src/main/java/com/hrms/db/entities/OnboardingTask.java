package com.hrms.db.entities;

import jakarta.persistence.*;
import java.time.LocalDate;

/**
 * Onboarding tasks — tracks new hire setup progress.
 * Required by: Employee Onboarding/Offboarding.
 */
@Entity
@Table(name = "onboarding_tasks")
public class OnboardingTask {

    @Id
    @Column(name = "task_id", length = 36)
    private String taskId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emp_id", nullable = false)
    private Employee employee;

    @Column(name = "task_name", length = 200)
    private String taskName;

    @Column(name = "task_type", length = 50)
    private String taskType; // IT_SETUP, DOCUMENT_VERIFY, TRAINING, POLICY_ACK

    @Column(name = "assigned_to", length = 100)
    private String assignedTo;

    @Column(name = "status", length = 30)
    private String status = "PENDING"; // PENDING, IN_PROGRESS, COMPLETED, DONE

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(name = "onboarding_status", length = 30)
    private String onboardingStatus; // PRE_ONBOARDING, ONBOARDING, COMPLETED, NO_SHOW

    // --- Getters & Setters ---

    public String getTaskId() { return taskId; }
    public void setTaskId(String taskId) { this.taskId = taskId; }

    public Employee getEmployee() { return employee; }
    public void setEmployee(Employee employee) { this.employee = employee; }

    public String getTaskName() { return taskName; }
    public void setTaskName(String taskName) { this.taskName = taskName; }

    public String getTaskType() { return taskType; }
    public void setTaskType(String taskType) { this.taskType = taskType; }

    public String getAssignedTo() { return assignedTo; }
    public void setAssignedTo(String assignedTo) { this.assignedTo = assignedTo; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public String getOnboardingStatus() { return onboardingStatus; }
    public void setOnboardingStatus(String onboardingStatus) { this.onboardingStatus = onboardingStatus; }
}
