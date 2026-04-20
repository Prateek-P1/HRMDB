package com.hrms.db.repositories.succession;

import com.hrms.db.entities.Notification;

import java.util.List;

public interface INotificationRepository {
    Notification save(Notification notification);
    List<Notification> findUnreadByRecipient(String recipientIdFk);
    int getUnreadCount(String recipientIdFk);
    void markAsRead(Long notificationId);
    void markAllAsRead(String recipientIdFk);
}
