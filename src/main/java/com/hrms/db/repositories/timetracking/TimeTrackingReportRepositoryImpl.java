package com.hrms.db.repositories.timetracking;

import com.hrms.db.config.DatabaseConnection;
import com.hrms.db.entities.HrReport;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.Collections;
import java.util.List;

public class TimeTrackingReportRepositoryImpl implements IReportRepository {

    @Override
    public void save(HrReport report) {
        Transaction tx = null;
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.merge(report);
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) try { tx.rollback(); } catch (Exception ignored) {}
            throw ex;
        }
    }

    @Override
    public HrReport findById(String reportId) {
        if (reportId == null) return null;
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            return session.get(HrReport.class, reportId);
        }
    }

    @Override
    public List<HrReport> findAll() {
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            return session.createQuery(
                            "FROM HrReport r ORDER BY r.generatedDate DESC",
                            HrReport.class)
                    .getResultList();
        } catch (Exception ex) {
            return Collections.emptyList();
        }
    }

    @Override
    public void delete(String reportId) {
        if (reportId == null) return;
        Transaction tx = null;
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            HrReport existing = session.get(HrReport.class, reportId);
            if (existing != null) session.remove(existing);
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) try { tx.rollback(); } catch (Exception ignored) {}
            throw ex;
        }
    }
}
