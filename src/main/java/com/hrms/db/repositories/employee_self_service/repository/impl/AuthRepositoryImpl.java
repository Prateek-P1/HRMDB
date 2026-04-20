package com.hrms.db.repositories.employee_self_service.repository.impl;

import com.hrms.db.config.DatabaseConnection;
import com.hrms.db.entities.Employee;
import com.hrms.db.repositories.employee_self_service.repository.interfaces.IAuthRepository;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

public class AuthRepositoryImpl implements IAuthRepository {

    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final String DEFAULT_PASSWORD = "CHANGE_ME";

    public AuthRepositoryImpl() {
        EmployeeSelfServiceSchema.ensureTables();
    }

    @Override
    public boolean validateUser(String username, String password) {
        if (isBlank(username) || isBlank(password)) {
            return false;
        }

        EmployeeSelfServiceSchema.ensureTables();

        Transaction tx = null;
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            Employee employee = findEmployeeByUsername(session, username);
            if (employee == null) {
                tx.commit();
                return false;
            }

            CredentialRow credential = findCredentialByEmpCode(session, employee.getEmpId());
            if (credential == null || credential.accountLocked) {
                tx.commit();
                return false;
            }

            boolean matched = credential.passwordHash.equals(hashPassword(password));
            if (matched) {
                session.createNativeMutationQuery(
                                "UPDATE ess_auth_credentials " +
                                        "SET failed_attempts = 0, account_locked = 0, updated_at = CURRENT_TIMESTAMP " +
                                        "WHERE employee_id = :id")
                        .setParameter("id", credential.employeeId)
                        .executeUpdate();
                tx.commit();
                return true;
            }

            int failedAttempts = credential.failedAttempts + 1;
            int accountLocked = failedAttempts >= MAX_FAILED_ATTEMPTS ? 1 : 0;
            session.createNativeMutationQuery(
                            "UPDATE ess_auth_credentials " +
                                    "SET failed_attempts = :failedAttempts, account_locked = :accountLocked, " +
                                    "updated_at = CURRENT_TIMESTAMP " +
                                    "WHERE employee_id = :id")
                    .setParameter("failedAttempts", failedAttempts)
                    .setParameter("accountLocked", accountLocked)
                    .setParameter("id", credential.employeeId)
                    .executeUpdate();

            tx.commit();
            return false;
        } catch (Exception ex) {
            rollback(tx);
            return false;
        }
    }

    @Override
    public int getEmployeeId(String username) {
        if (isBlank(username)) {
            return -1;
        }

        EmployeeSelfServiceSchema.ensureTables();

        Transaction tx = null;
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            Employee employee = findEmployeeByUsername(session, username);
            if (employee == null) {
                tx.commit();
                return -1;
            }

            int employeeId = ensureCredentialAndGetEmployeeId(session, employee);
            tx.commit();
            return employeeId;
        } catch (Exception ex) {
            rollback(tx);
            return -1;
        }
    }

    @Override
    public boolean updatePassword(int employeeId, String newPassword) {
        if (employeeId <= 0 || isBlank(newPassword)) {
            return false;
        }

        EmployeeSelfServiceSchema.ensureTables();

        Transaction tx = null;
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            int updated = session.createNativeMutationQuery(
                            "UPDATE ess_auth_credentials " +
                                    "SET password_hash = :passwordHash, failed_attempts = 0, account_locked = 0, " +
                                    "updated_at = CURRENT_TIMESTAMP " +
                                    "WHERE employee_id = :id")
                    .setParameter("passwordHash", hashPassword(newPassword))
                    .setParameter("id", employeeId)
                    .executeUpdate();

            tx.commit();
            return updated > 0;
        } catch (Exception ex) {
            rollback(tx);
            return false;
        }
    }

    @Override
    public boolean isAccountLocked(String username) {
        if (isBlank(username)) {
            return false;
        }

        EmployeeSelfServiceSchema.ensureTables();

        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            Employee employee = findEmployeeByUsername(session, username);
            if (employee == null) {
                return false;
            }

            CredentialRow credential = findCredentialByEmpCode(session, employee.getEmpId());
            return credential != null && credential.accountLocked;
        } catch (Exception ex) {
            return false;
        }
    }

    private int ensureCredentialAndGetEmployeeId(Session session, Employee employee) {
        CredentialRow existing = findCredentialByEmpCode(session, employee.getEmpId());
        if (existing != null) {
            return existing.employeeId;
        }

        String canonicalUsername = canonicalUsername(employee);
        session.createNativeMutationQuery(
                        "INSERT OR IGNORE INTO ess_auth_credentials " +
                                "(emp_code, username, password_hash, account_locked, failed_attempts) " +
                                "VALUES (:empCode, :username, :passwordHash, 0, 0)")
                .setParameter("empCode", employee.getEmpId())
                .setParameter("username", canonicalUsername)
                .setParameter("passwordHash", hashPassword(DEFAULT_PASSWORD))
                .executeUpdate();

        CredentialRow created = findCredentialByEmpCode(session, employee.getEmpId());
        return created != null ? created.employeeId : -1;
    }

    private CredentialRow findCredentialByEmpCode(Session session, String empCode) {
        Object rowObj = session.createNativeQuery(
                        "SELECT employee_id, emp_code, password_hash, account_locked, failed_attempts " +
                                "FROM ess_auth_credentials WHERE emp_code = :empCode")
                .setParameter("empCode", empCode)
                .setMaxResults(1)
                .uniqueResult();

        if (rowObj == null) {
            return null;
        }

        Object[] row = (Object[]) rowObj;
        CredentialRow credential = new CredentialRow();
        credential.employeeId = asInt(row[0]);
        credential.empCode = row[1] != null ? row[1].toString() : null;
        credential.passwordHash = row[2] != null ? row[2].toString() : null;
        credential.accountLocked = asBoolean(row[3]);
        credential.failedAttempts = asInt(row[4]);
        return credential;
    }

    private Employee findEmployeeByUsername(Session session, String username) {
        String normalized = username.trim().toLowerCase(Locale.ROOT);
        return session.createQuery(
                        "FROM Employee e WHERE lower(e.empId) = :username OR lower(e.email) = :username",
                        Employee.class)
                .setParameter("username", normalized)
                .setMaxResults(1)
                .uniqueResult();
    }

    private String canonicalUsername(Employee employee) {
        if (employee.getEmail() != null && !employee.getEmail().isBlank()) {
            return employee.getEmail().trim().toLowerCase(Locale.ROOT);
        }
        return employee.getEmpId().trim().toLowerCase(Locale.ROOT);
    }

    private String hashPassword(String rawPassword) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(rawPassword.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-256 algorithm unavailable", ex);
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private int asInt(Object value) {
        if (value == null) {
            return 0;
        }
        if (value instanceof Number number) {
            return number.intValue();
        }
        try {
            return Integer.parseInt(value.toString());
        } catch (NumberFormatException ex) {
            return 0;
        }
    }

    private boolean asBoolean(Object value) {
        if (value == null) {
            return false;
        }
        if (value instanceof Boolean bool) {
            return bool;
        }
        if (value instanceof Number number) {
            return number.intValue() != 0;
        }
        return Boolean.parseBoolean(value.toString());
    }

    private void rollback(Transaction tx) {
        if (tx != null) {
            try {
                tx.rollback();
            } catch (Exception ignored) {
                // Ignore rollback errors after the main failure.
            }
        }
    }

    private static class CredentialRow {
        private int employeeId;
        private String empCode;
        private String passwordHash;
        private boolean accountLocked;
        private int failedAttempts;
    }
}
