package com.hrms.db.repositories.benefits;

import com.hrms.db.entities.Notification;

import java.util.List;

/** Notifications used by Benefits Administration. Backed by the central notifications table. */
public interface NotificationDAO {
    void save(Notification notification);
    Notification findById(Long notificationId);
    List<Notification> findAll();
    List<Notification> findByEmployeeId(String employeeId);
    List<Notification> findByStatus(String status);
    void update(Notification notification);
}
