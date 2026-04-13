package com.hrms.db.entities;

import jakarta.persistence.*;

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

    @Column(name = "emp_id", nullable = false, length = 20)
    private String empId;

    @Column(name = "task_name", length = 200)
    private String taskName;

    @Column(name = "task_type", length = 50)
    private String taskType; // IT_SETUP, DOCUMENT_VERIFY, TRAINING, POLICY_ACK

    @Column(name = "status", length = 30)
    private String status = "PENDING"; // PENDING, IN_PROGRESS, COMPLETED

    @Column(name = "onboarding_status", length = 30)
    private String onboardingStatus; // PRE_ONBOARDING, ONBOARDING, COMPLETED, NO_SHOW

    // --- Getters & Setters ---

    public String getTaskId() { return taskId; }
    public void setTaskId(String taskId) { this.taskId = taskId; }

    public String getEmpId() { return empId; }
    public void setEmpId(String empId) { this.empId = empId; }

    public String getTaskName() { return taskName; }
    public void setTaskName(String taskName) { this.taskName = taskName; }

    public String getTaskType() { return taskType; }
    public void setTaskType(String taskType) { this.taskType = taskType; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getOnboardingStatus() { return onboardingStatus; }
    public void setOnboardingStatus(String onboardingStatus) { this.onboardingStatus = onboardingStatus; }
}
