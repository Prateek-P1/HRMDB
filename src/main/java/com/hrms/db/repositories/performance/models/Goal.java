package com.hrms.db.repositories.performance.models;

import java.util.Date;

public class Goal {
    private int goalId;
    private int employeeId;
    private String title;
    private String description;
    private String cycle;          // e.g. "2025-Q1", "2025-Annual"
    private float progressPercent; // 0.0 to 100.0
    private Date dueDate;
    private String status;         // OPEN, IN_PROGRESS, COMPLETED, CANCELLED
    private boolean deleted;

    public Goal() {}

    public Goal(int goalId, int employeeId, String title, String description, String cycle, Date dueDate) {
        this.goalId = goalId;
        this.employeeId = employeeId;
        this.title = title;
        this.description = description;
        this.cycle = cycle;
        this.dueDate = dueDate;
        this.progressPercent = 0.0f;
        this.status = "OPEN";
        this.deleted = false;
    }

    public int getGoalId() { return goalId; }
    public void setGoalId(int goalId) { this.goalId = goalId; }
    public int getEmployeeId() { return employeeId; }
    public void setEmployeeId(int employeeId) { this.employeeId = employeeId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getCycle() { return cycle; }
    public void setCycle(String cycle) { this.cycle = cycle; }
    public float getProgressPercent() { return progressPercent; }
    public void setProgressPercent(float progressPercent) { this.progressPercent = progressPercent; }
    public Date getDueDate() { return dueDate; }
    public void setDueDate(Date dueDate) { this.dueDate = dueDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public boolean isDeleted() { return deleted; }
    public void setDeleted(boolean deleted) { this.deleted = deleted; }

    @Override
    public String toString() {
        return "Goal{id=" + goalId + ", empId=" + employeeId + ", title='" + title + "', progress=" + progressPercent + "%, status='" + status + "'}";
    }
}
