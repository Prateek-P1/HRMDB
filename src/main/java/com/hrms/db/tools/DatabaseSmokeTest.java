package com.hrms.db.tools;

import com.hrms.db.config.DatabaseConnection;
import com.hrms.db.facade.HRMSDatabaseFacade;
import org.hibernate.Session;

import java.nio.file.Paths;
import java.sql.DatabaseMetaData;

/**
 * DatabaseSmokeTest
 *
 * Quick local sanity-check:
 * - boots Hibernate (reads hibernate.cfg.xml)
 * - validates JDBC connection
 * - runs a few simple HQL counts
 *
 * This is safe to run locally. With SQLite, the DB is a file (default: ./hrms.db).
 */
public final class DatabaseSmokeTest {

    private DatabaseSmokeTest() {}

    public static void main(String[] args) {
        System.out.println("[DatabaseSmokeTest] Starting...");
        System.out.println("[DatabaseSmokeTest] Expected SQLite file (relative): hrms.db");
        System.out.println("[DatabaseSmokeTest] Absolute path: " + Paths.get("hrms.db").toAbsolutePath());

        HRMSDatabaseFacade.getInstance().initialize();

        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            session.doWork(connection -> {
                DatabaseMetaData meta = connection.getMetaData();
                System.out.println("[DatabaseSmokeTest] DB Product: " + meta.getDatabaseProductName());
                System.out.println("[DatabaseSmokeTest] DB Version: " + meta.getDatabaseProductVersion());
                System.out.println("[DatabaseSmokeTest] Driver: " + meta.getDriverName());
                System.out.println("[DatabaseSmokeTest] Connection: SUCCESS");
            });

            Long employees = session.createQuery("SELECT COUNT(e) FROM Employee e", Long.class).uniqueResult();
            Long leaveRecords = session.createQuery("SELECT COUNT(l) FROM LeaveRecord l", Long.class).uniqueResult();
            Long payroll = session.createQuery("SELECT COUNT(p) FROM PayrollResult p", Long.class).uniqueResult();

            System.out.println("[DatabaseSmokeTest] Counts:");
            System.out.println("  employees     = " + (employees != null ? employees : 0));
            System.out.println("  leave_records = " + (leaveRecords != null ? leaveRecords : 0));
            System.out.println("  payroll       = " + (payroll != null ? payroll : 0));

            System.out.println("[DatabaseSmokeTest] OK");

        } catch (Exception ex) {
            System.err.println("[DatabaseSmokeTest] FAILED: " + ex.getMessage());
            ex.printStackTrace(System.err);
            System.exit(1);
        } finally {
            HRMSDatabaseFacade.getInstance().shutdown();
        }
    }
}
