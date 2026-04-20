package com.hrms.db.repositories.benefits;

import com.hrms.db.entities.Notification;

import java.util.List;

/** DAO contract for {@link Notification} persistence used by Benefits Administration. */
public interface NotificationDAO {

    void save(Notification notification);

    Notification findById(Long notificationId);

    List<Notification> findAll();

    List<Notification> findByEmployeeId(String employeeId);

    List<Notification> findByStatus(String status);

    void update(Notification notification);
}
