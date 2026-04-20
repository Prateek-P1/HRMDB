package com.hrms.db.repositories.timetracking;

import com.hrms.db.entities.Notification;

import java.util.List;

public interface INotificationRepository {
    void save(Notification notification);

    Notification findById(Long notificationId);

    List<Notification> findAll();

    void delete(Long notificationId);
}
