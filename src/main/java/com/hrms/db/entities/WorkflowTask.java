package com.hrms.db.entities;

import jakarta.persistence.*;

/**
 * Workflow tasks — individual steps within a workflow.
 * Required by: Customization subsystem.
 */
@Entity
@Table(name = "workflow_tasks")
public class WorkflowTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "task_id")
    private Integer taskId;

    @Column(name = "workflow_id", nullable = false)
    private Integer workflowId;

    @Column(name = "task_name", length = 200)
    private String taskName;

    @Column(name = "sequence_order")
    private Integer sequenceOrder;

    @Column(name = "task_status", length = 30)
    private String taskStatus; // PENDING, IN_PROGRESS, COMPLETED

    // --- Getters & Setters ---

    public Integer getTaskId() { return taskId; }
    public void setTaskId(Integer taskId) { this.taskId = taskId; }

    public Integer getWorkflowId() { return workflowId; }
    public void setWorkflowId(Integer workflowId) { this.workflowId = workflowId; }

    public String getTaskName() { return taskName; }
    public void setTaskName(String taskName) { this.taskName = taskName; }

    public Integer getSequenceOrder() { return sequenceOrder; }
    public void setSequenceOrder(Integer sequenceOrder) { this.sequenceOrder = sequenceOrder; }

    public String getTaskStatus() { return taskStatus; }
    public void setTaskStatus(String taskStatus) { this.taskStatus = taskStatus; }
}
