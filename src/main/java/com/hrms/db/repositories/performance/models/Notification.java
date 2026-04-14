package com.hrms.db.repositories.performance.models;

import java.util.Date;

public class Notification {
    private int notifId;
    private int userId;
    private String message;
    private String type;   // INFO, WARNING, ACTION_REQUIRED
    private boolean read;
    private Date createdAt;

    public Notification() {}

    public Notification(int notifId, int userId, String message, String type) {
        this.notifId = notifId;
        this.userId = userId;
        this.message = message;
        this.type = type;
        this.read = false;
        this.createdAt = new Date();
    }

    public int getNotifId() { return notifId; }
    public void setNotifId(int notifId) { this.notifId = notifId; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public boolean isRead() { return read; }
    public void setRead(boolean read) { this.read = read; }
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "Notification{id=" + notifId + ", userId=" + userId + ", type='" + type + "', read=" + read + ", msg='" + message + "'}";
    }
}
