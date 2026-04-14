package com.hrms.db.repositories.performance.models;

import java.util.Date;

public class Reminder {
    private int reminderId;
    private int userId;
    private String task;
    private Date scheduledAt;
    private boolean sent;

    public Reminder() {}

    public Reminder(int reminderId, int userId, String task, Date scheduledAt) {
        this.reminderId = reminderId;
        this.userId = userId;
        this.task = task;
        this.scheduledAt = scheduledAt;
        this.sent = false;
    }

    public int getReminderId() { return reminderId; }
    public void setReminderId(int reminderId) { this.reminderId = reminderId; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getTask() { return task; }
    public void setTask(String task) { this.task = task; }
    public Date getScheduledAt() { return scheduledAt; }
    public void setScheduledAt(Date scheduledAt) { this.scheduledAt = scheduledAt; }
    public boolean isSent() { return sent; }
    public void setSent(boolean sent) { this.sent = sent; }

    @Override
    public String toString() {
        return "Reminder{id=" + reminderId + ", userId=" + userId + ", task='" + task + "', scheduledAt=" + scheduledAt + "}";
    }
}
