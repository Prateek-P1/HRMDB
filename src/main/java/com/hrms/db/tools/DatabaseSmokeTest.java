package com.hrms.db.tools;

import com.hrms.db.config.DatabaseConnection;
import com.hrms.db.entities.AccessPermission;
import com.hrms.db.entities.Employee;
import com.hrms.db.entities.LeaveRecord;
import com.hrms.db.entities.PayrollResult;
import com.hrms.db.entities.SecurityAuditLog;
import com.hrms.db.facade.HRMSDatabaseFacade;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.nio.file.Paths;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DatabaseSmokeTest
 *
 * Quick local sanity-check:
 * - boots Hibernate (reads hibernate.cfg.xml)
 * - validates JDBC connection
 * - seeds a small amount of deterministic dummy data (SMOKE_* rows) if missing
 * - runs a few simple HQL counts and prints sample rows
 *
 * This is safe to run locally. With SQLite, the DB is a file (default: ./hrms.db).
 */
public final class DatabaseSmokeTest {

    private DatabaseSmokeTest() {}

    private static final String SMOKE_EMP_ID = "SMOKE_EMP_001";
    private static final String SMOKE_LEAVE_ID = "SMOKE_LEAVE_001";
    private static final String SMOKE_PAYROLL_ID = "SMOKE_PAYROLL_001";
    private static final String SMOKE_BATCH_ID = "SMOKE_BATCH_001";
    private static final String SMOKE_PAY_PERIOD = "2026-04";
    private static final String SMOKE_SESSION_ID = "SMOKE_SESSION_001";
    private static final String SMOKE_TOKEN = "SMOKE_TOKEN_001";

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

                try (ResultSet tables = meta.getTables(null, null, "%", new String[] {"TABLE"})) {
                    System.out.println("[DatabaseSmokeTest] Tables (first 30):");
                    int shown = 0;
                    while (tables.next() && shown < 30) {
                        System.out.println("  - " + tables.getString("TABLE_NAME"));
                        shown++;
                    }
                }
            });

            printCounts(session, "Before seeding");
            seedDummyData(session);
            printCounts(session, "After seeding");
            printSampleRows(session);

            System.out.println("[DatabaseSmokeTest] OK");

        } catch (Exception ex) {
            System.err.println("[DatabaseSmokeTest] FAILED: " + ex.getMessage());
            ex.printStackTrace(System.err);
            System.exit(1);
        } finally {
            HRMSDatabaseFacade.getInstance().shutdown();
        }
    }

    private static void printCounts(Session session, String label) {
        Long employees = session.createQuery("SELECT COUNT(e) FROM Employee e", Long.class).uniqueResult();
        Long leaveRecords = session.createQuery("SELECT COUNT(l) FROM LeaveRecord l", Long.class).uniqueResult();
        Long payroll = session.createQuery("SELECT COUNT(p) FROM PayrollResult p", Long.class).uniqueResult();

        System.out.println("[DatabaseSmokeTest] Counts (" + label + "):");
        System.out.println("  employees       = " + (employees != null ? employees : 0));
        System.out.println("  leave_records   = " + (leaveRecords != null ? leaveRecords : 0));
        System.out.println("  payroll_results = " + (payroll != null ? payroll : 0));
    }

    private static void seedDummyData(Session session) {
        Transaction tx = null;
        try {
            tx = session.beginTransaction();

            Employee emp = session.get(Employee.class, SMOKE_EMP_ID);
            if (emp == null) {
                emp = new Employee();
                emp.setEmpId(SMOKE_EMP_ID);
                emp.setName("Smoke Test Employee");
                emp.setEmail("smoke.emp001@example.com");
                emp.setDepartment("QA");
                emp.setDesignation("SMOKE_USER");
                emp.setRole("ADMIN");
                emp.setDateOfJoining(LocalDate.now().minusYears(1));
                session.persist(emp);
            }

            LeaveRecord leave = session.get(LeaveRecord.class, SMOKE_LEAVE_ID);
            if (leave == null) {
                leave = new LeaveRecord();
                leave.setLeaveId(SMOKE_LEAVE_ID);
                leave.setEmployee(emp);
                leave.setStartDate(LocalDate.now().minusDays(2));
                leave.setEndDate(LocalDate.now().minusDays(1));
                leave.setLeaveType("CASUAL");
                leave.setStatus("APPROVED");
                session.persist(leave);
            }

            PayrollResult payroll = session.get(PayrollResult.class, SMOKE_PAYROLL_ID);
            if (payroll == null) {
                payroll = new PayrollResult();
                payroll.setRecordId(SMOKE_PAYROLL_ID);
                payroll.setEmployee(emp);
                payroll.setBatchId(SMOKE_BATCH_ID);
                payroll.setPayPeriod(SMOKE_PAY_PERIOD);
                payroll.setFinalGrossPay(100000.0);
                payroll.setFinalNetPay(85000.0);
                payroll.setPayoutAmount(85000.0);
                session.persist(payroll);
            }

            AccessPermission perm = session.createQuery(
                            "FROM AccessPermission p WHERE p.userId = :uid",
                            AccessPermission.class)
                    .setParameter("uid", SMOKE_EMP_ID)
                    .setMaxResults(1)
                    .uniqueResult();

            if (perm == null) {
                perm = new AccessPermission();
                perm.setUserId(SMOKE_EMP_ID);
                perm.setUserRole("ADMIN");
                perm.setAccessPermissions("*");
                perm.setComplianceStatus("OK");
                session.persist(perm);
            }

            com.hrms.db.entities.UserSession sessionEntity = session.get(com.hrms.db.entities.UserSession.class, SMOKE_SESSION_ID);
            if (sessionEntity == null) {
                sessionEntity = new com.hrms.db.entities.UserSession();
                sessionEntity.setSessionId(SMOKE_SESSION_ID);
                sessionEntity.setUserId(SMOKE_EMP_ID);
                sessionEntity.setUsername(emp.getEmail());
                sessionEntity.setSessionToken(SMOKE_TOKEN);
                sessionEntity.setLoginTimestamp(LocalDateTime.now());
                sessionEntity.setIpAddress("127.0.0.1");
                sessionEntity.setIsActive(true);
                session.persist(sessionEntity);
            }

            SecurityAuditLog audit = new SecurityAuditLog();
            audit.setUserId(SMOKE_EMP_ID);
            audit.setActionType("SMOKE_TEST");
            audit.setAction("SEED");
            audit.setOperation("DatabaseSmokeTest.seedDummyData");
            audit.setOutcome("SUCCESS");
            audit.setDetails("Inserted/verified SMOKE_* rows");
            audit.setTimestamp(LocalDateTime.now());
            session.persist(audit);

            tx.commit();
        } catch (Exception ex) {
            if (tx != null) {
                try { tx.rollback(); } catch (Exception ignored) {}
            }
            throw ex;
        }
    }

    private static void printSampleRows(Session session) {
        System.out.println("[DatabaseSmokeTest] Sample rows (SMOKE_*):");

        List<Object[]> employees = session.createQuery(
                        "SELECT e.empId, e.name, e.email, e.role FROM Employee e WHERE e.empId = :id",
                        Object[].class)
                .setParameter("id", SMOKE_EMP_ID)
                .getResultList();

        for (Object[] r : employees) {
            System.out.println("  employees: empId=" + r[0] + ", name=" + r[1] + ", email=" + r[2] + ", role=" + r[3]);
        }

        List<Object[]> leaves = session.createQuery(
                        "SELECT l.leaveId, l.employee.empId, l.startDate, l.endDate, l.leaveType, l.status " +
                                "FROM LeaveRecord l WHERE l.leaveId = :id",
                        Object[].class)
                .setParameter("id", SMOKE_LEAVE_ID)
                .getResultList();

        for (Object[] r : leaves) {
            System.out.println("  leave_records: leaveId=" + r[0] + ", empId=" + r[1] + ", start=" + r[2] + ", end=" + r[3] + ", type=" + r[4] + ", status=" + r[5]);
        }

        List<Object[]> payroll = session.createQuery(
                        "SELECT p.recordId, p.employee.empId, p.batchId, p.payPeriod, p.finalNetPay " +
                                "FROM PayrollResult p WHERE p.recordId = :id",
                        Object[].class)
                .setParameter("id", SMOKE_PAYROLL_ID)
                .getResultList();

        for (Object[] r : payroll) {
            System.out.println("  payroll_results: recordId=" + r[0] + ", empId=" + r[1] + ", batch=" + r[2] + ", period=" + r[3] + ", netPay=" + r[4]);
        }

        List<Object[]> perms = session.createQuery(
                        "SELECT p.userId, p.userRole, p.accessPermissions, p.complianceStatus " +
                                "FROM AccessPermission p WHERE p.userId = :uid",
                        Object[].class)
                .setParameter("uid", SMOKE_EMP_ID)
                .getResultList();

        for (Object[] r : perms) {
            System.out.println("  access_permissions: userId=" + r[0] + ", role=" + r[1] + ", perms=" + r[2] + ", compliance=" + r[3]);
        }

        List<Object[]> sessions = session.createQuery(
                        "SELECT s.sessionId, s.userId, s.username, s.sessionToken, s.isActive " +
                                "FROM UserSession s WHERE s.sessionId = :sid",
                        Object[].class)
                .setParameter("sid", SMOKE_SESSION_ID)
                .getResultList();

        for (Object[] r : sessions) {
            System.out.println("  user_sessions: sessionId=" + r[0] + ", userId=" + r[1] + ", username=" + r[2] + ", token=" + r[3] + ", active=" + r[4]);
        }
    }
}
