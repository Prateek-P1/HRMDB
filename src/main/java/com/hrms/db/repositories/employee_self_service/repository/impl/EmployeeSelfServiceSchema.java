package com.hrms.db.repositories.employee_self_service.repository.impl;

import com.hrms.db.config.DatabaseConnection;
import org.hibernate.Session;
import org.hibernate.Transaction;

final class EmployeeSelfServiceSchema {

    private static final String CREATE_AUTH_TABLE_SQL = """
            CREATE TABLE IF NOT EXISTS ess_auth_credentials (
                employee_id INTEGER PRIMARY KEY AUTOINCREMENT,
                emp_code VARCHAR(20) NOT NULL UNIQUE,
                username VARCHAR(150) NOT NULL UNIQUE,
                password_hash VARCHAR(256) NOT NULL,
                account_locked INTEGER NOT NULL DEFAULT 0,
                failed_attempts INTEGER NOT NULL DEFAULT 0,
                updated_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP
            )
            """;

    private static final String CREATE_DOC_MAP_TABLE_SQL = """
            CREATE TABLE IF NOT EXISTS ess_document_map (
                document_id INTEGER PRIMARY KEY AUTOINCREMENT,
                document_code VARCHAR(36) NOT NULL UNIQUE
            )
            """;

    private static volatile boolean initialized = false;

    private EmployeeSelfServiceSchema() {
    }

    static void ensureTables() {
        if (initialized) {
            return;
        }
        synchronized (EmployeeSelfServiceSchema.class) {
            if (initialized) {
                return;
            }
            Transaction tx = null;
            try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
                tx = session.beginTransaction();
                session.createNativeMutationQuery(CREATE_AUTH_TABLE_SQL).executeUpdate();
                session.createNativeMutationQuery(CREATE_DOC_MAP_TABLE_SQL).executeUpdate();
                tx.commit();
                initialized = true;
            } catch (Exception ex) {
                if (tx != null) {
                    try {
                        tx.rollback();
                    } catch (Exception ignored) {
                        // Ignore rollback errors after the main failure.
                    }
                }
            }
        }
    }
}
