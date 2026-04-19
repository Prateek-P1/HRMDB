package com.hrms.db.repositories.benefits;

import com.hrms.db.config.DatabaseConnection;
import com.hrms.db.entities.Notification;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.Collections;
import java.util.List;

/** Hibernate-backed Notification DAO using the central {@link Notification} entity. */
public class NotificationDAOImpl implements NotificationDAO {

    @Override
    public void save(Notification notification) {
        Transaction tx = null;
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.persist(notification);
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) try { tx.rollback(); } catch (Exception ignored) {}
            throw ex;
        }
    }

    @Override
    public Notification findById(Long notificationId) {
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            return session.get(Notification.class, notificationId);
        }
    }

    @Override
    public List<Notification> findAll() {
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            return session.createQuery(
                    "FROM Notification n ORDER BY n.createdAt DESC",
                    Notification.class
            ).getResultList();
        } catch (Exception ex) {
            return Collections.emptyList();
        }
    }

    @Override
    public List<Notification> findByEmployeeId(String employeeId) {
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            return session.createQuery(
                            "FROM Notification n WHERE n.recipientId = :id ORDER BY n.createdAt DESC",
                            Notification.class)
                    .setParameter("id", employeeId)
                    .getResultList();
        } catch (Exception ex) {
            return Collections.emptyList();
        }
    }

    @Override
    public List<Notification> findByStatus(String status) {
        if (status == null) return Collections.emptyList();
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            return session.createQuery(
                            "FROM Notification n WHERE n.status = :s ORDER BY n.createdAt DESC",
                            Notification.class)
                    .setParameter("s", status)
                    .getResultList();
        } catch (Exception ex) {
            return Collections.emptyList();
        }
    }

    @Override
    public void update(Notification notification) {
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
}
