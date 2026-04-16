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

    @Column(name = "subject", length = 200)
    private String subject;

    @Column(name = "status", length = 30)
    private String status = "PENDING"; // PENDING, SENT, READ

    @Column(name = "scheduled_at")
    private LocalDateTime scheduledAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    // --- Core Getters & Setters ---

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

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getScheduledAt() { return scheduledAt; }
    public void setScheduledAt(LocalDateTime scheduledAt) { this.scheduledAt = scheduledAt; }

    // --- Alias getters used by OnboardingRepositoryImpl ---

    /** The body of the notification (alias for notificationMessage) */
    public String getBody() { return notificationMessage; }
    public void setBody(String body) { this.notificationMessage = body; }

    /** Alias for recipientId — used by onboarding notification flow */
    public String getRecipientEmpId() { return recipientId; }
    public void setRecipientEmpId(String empId) { this.recipientId = empId; }
}
