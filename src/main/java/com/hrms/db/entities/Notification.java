package com.hrms.db.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Notifications — system alerts and messages to employees.
 * Used across all subsystems.
 */
@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long notificationId;

    @Column(name = "recipient_id", nullable = false, length = 20)
    private String recipientId;

    @Column(name = "notification_message", columnDefinition = "TEXT")
    private String notificationMessage;

    @Column(name = "notification_type", length = 50)
    private String notificationType; // LEAVE, PAYROLL, SUCCESSION, GENERAL

    @Column(name = "is_read")
    private Boolean isRead = false;

    @Column(name = "triggered_by", length = 100)
    private String triggeredBy;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    // --- Getters & Setters ---

    public Long getNotificationId() { return notificationId; }
    public void setNotificationId(Long notificationId) { this.notificationId = notificationId; }

    public String getRecipientId() { return recipientId; }
    public void setRecipientId(String recipientId) { this.recipientId = recipientId; }

    public String getNotificationMessage() { return notificationMessage; }
    public void setNotificationMessage(String notificationMessage) { this.notificationMessage = notificationMessage; }

    public String getNotificationType() { return notificationType; }
    public void setNotificationType(String notificationType) { this.notificationType = notificationType; }

    public Boolean getIsRead() { return isRead; }
    public void setIsRead(Boolean isRead) { this.isRead = isRead; }

    public String getTriggeredBy() { return triggeredBy; }
    public void setTriggeredBy(String triggeredBy) { this.triggeredBy = triggeredBy; }

    public LocalDateTime getCreatedAt() { return createdAt; }
}
