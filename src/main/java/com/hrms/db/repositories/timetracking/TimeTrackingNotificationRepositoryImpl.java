package com.hrms.db.repositories.timetracking;

import com.hrms.db.config.DatabaseConnection;
import com.hrms.db.entities.Notification;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.Collections;
import java.util.List;

public class TimeTrackingNotificationRepositoryImpl implements INotificationRepository {

    @Override
    public void save(Notification notification) {
        Transaction tx = null;
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.merge(notification);
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) try { tx.rollback(); } catch (Exception ignored) {}
            throw ex;
        }
    }

    @Override
    public Notification findById(Long notificationId) {
        if (notificationId == null) return null;
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            return session.get(Notification.class, notificationId);
        }
    }

    @Override
    public List<Notification> findAll() {
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            return session.createQuery(
                            "FROM Notification n ORDER BY n.createdAt DESC",
                            Notification.class)
                    .getResultList();
        } catch (Exception ex) {
            return Collections.emptyList();
        }
    }

    @Override
    public void delete(Long notificationId) {
        if (notificationId == null) return;
        Transaction tx = null;
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            Notification existing = session.get(Notification.class, notificationId);
            if (existing != null) session.remove(existing);
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) try { tx.rollback(); } catch (Exception ignored) {}
            throw ex;
        }
    }
}
