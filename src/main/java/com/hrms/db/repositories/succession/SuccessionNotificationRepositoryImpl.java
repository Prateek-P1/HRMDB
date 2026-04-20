package com.hrms.db.repositories.succession;

import com.hrms.db.config.DatabaseConnection;
import com.hrms.db.entities.Notification;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.Collections;
import java.util.List;

public class SuccessionNotificationRepositoryImpl implements INotificationRepository {

    @Override
    public Notification save(Notification notification) {
        if (notification == null) return null;
        Transaction tx = null;
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            Notification merged = session.merge(notification);
            tx.commit();
            return merged;
        } catch (Exception ex) {
            if (tx != null) try { tx.rollback(); } catch (Exception ignored) {}
            throw ex;
        }
    }

    @Override
    public List<Notification> findUnreadByRecipient(String recipientIdFk) {
        if (recipientIdFk == null) return Collections.emptyList();
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            return session.createQuery(
                            "FROM Notification n WHERE n.recipientId = :rid AND (n.isRead = false OR n.isRead IS NULL) ORDER BY n.createdAt DESC",
                            Notification.class)
                    .setParameter("rid", recipientIdFk)
                    .getResultList();
        } catch (Exception ex) {
            return Collections.emptyList();
        }
    }

    @Override
    public int getUnreadCount(String recipientIdFk) {
        if (recipientIdFk == null) return 0;
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            Long count = session.createQuery(
                            "SELECT COUNT(n) FROM Notification n WHERE n.recipientId = :rid AND (n.isRead = false OR n.isRead IS NULL)",
                            Long.class)
                    .setParameter("rid", recipientIdFk)
                    .uniqueResult();
            if (count == null) return 0;
            return count > Integer.MAX_VALUE ? Integer.MAX_VALUE : count.intValue();
        }
    }

    @Override
    public void markAsRead(Long notificationId) {
        if (notificationId == null) return;
        Transaction tx = null;
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            Notification n = session.get(Notification.class, notificationId);
            if (n != null) {
                n.setIsRead(true);
                n.setStatus("READ");
                session.merge(n);
            }
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) try { tx.rollback(); } catch (Exception ignored) {}
            throw ex;
        }
    }

    @Override
    public void markAllAsRead(String recipientIdFk) {
        if (recipientIdFk == null) return;
        Transaction tx = null;
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.createMutationQuery(
                            "UPDATE Notification n SET n.isRead = true, n.status = 'READ' WHERE n.recipientId = :rid")
                    .setParameter("rid", recipientIdFk)
                    .executeUpdate();
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) try { tx.rollback(); } catch (Exception ignored) {}
            throw ex;
        }
    }
}
