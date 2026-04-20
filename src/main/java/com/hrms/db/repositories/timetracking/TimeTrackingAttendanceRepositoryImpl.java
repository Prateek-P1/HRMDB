package com.hrms.db.repositories.timetracking;

import com.hrms.db.config.DatabaseConnection;
import com.hrms.db.entities.Attendance;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.Collections;
import java.util.List;

public class TimeTrackingAttendanceRepositoryImpl implements IAttendanceRepository {

    @Override
    public void save(Attendance attendance) {
        Transaction tx = null;
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.merge(attendance);
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) try { tx.rollback(); } catch (Exception ignored) {}
            throw ex;
        }
    }

    @Override
    public Attendance findById(String recordId) {
        if (recordId == null) return null;
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            return session.get(Attendance.class, recordId);
        }
    }

    @Override
    public List<Attendance> findAll() {
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            return session.createQuery(
                            "FROM Attendance a ORDER BY a.payPeriod DESC",
                            Attendance.class)
                    .getResultList();
        } catch (Exception ex) {
            return Collections.emptyList();
        }
    }

    @Override
    public void delete(String recordId) {
        if (recordId == null) return;
        Transaction tx = null;
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            Attendance existing = session.get(Attendance.class, recordId);
            if (existing != null) session.remove(existing);
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) try { tx.rollback(); } catch (Exception ignored) {}
            throw ex;
        }
    }
}
