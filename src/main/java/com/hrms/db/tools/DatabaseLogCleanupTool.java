package com.hrms.db.tools;

import com.hrms.db.config.DatabaseConnection;
import com.hrms.db.entities.SecurityAuditLog;
import com.hrms.db.facade.HRMSDatabaseFacade;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

/**
 * Removes obvious test and placeholder log rows from security_audit_logs.
 */
public final class DatabaseLogCleanupTool {

    private DatabaseLogCleanupTool() {
    }

    public static void main(String[] args) {
        HRMSDatabaseFacade.getInstance().initialize();

        Transaction tx = null;
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            List<SecurityAuditLog> logs = session.createQuery(
                            "FROM SecurityAuditLog s " +
                                    "WHERE s.actionType = 'SMOKE_TEST' " +
                                    "OR (s.actionType = 'DB_LOG' AND s.operation = :operation AND s.details LIKE :details)",
                            SecurityAuditLog.class)
                    .setParameter("operation", "PayrollRepositoryImpl.logProcessingError")
                    .setParameter("details", "%empID=arg1: arg2%")
                    .getResultList();

            tx = session.beginTransaction();
            for (SecurityAuditLog log : logs) {
                session.remove(log);
            }
            tx.commit();

            System.out.println("[DatabaseLogCleanupTool] Removed " + logs.size() + " log row(s).");
        } catch (Exception ex) {
            if (tx != null) {
                try {
                    tx.rollback();
                } catch (Exception ignored) {
                }
            }
            System.err.println("[DatabaseLogCleanupTool] Cleanup failed: " + ex.getMessage());
            ex.printStackTrace(System.err);
            System.exit(1);
        } finally {
            HRMSDatabaseFacade.getInstance().shutdown();
        }
    }
}
