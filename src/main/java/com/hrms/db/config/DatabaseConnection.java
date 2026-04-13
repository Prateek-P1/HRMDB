package com.hrms.db.config;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * Singleton Hibernate SessionFactory provider.
 * 
 * GRASP: Information Expert — owns all connection/session logic.
 * SOLID: Single Responsibility — only manages the SessionFactory lifecycle.
 *
 * Usage:
 *   SessionFactory sf = DatabaseConnection.getSessionFactory();
 *   Session session = sf.openSession();
 */
public class DatabaseConnection {

    private static volatile SessionFactory sessionFactory;

    private DatabaseConnection() {
        // Prevent instantiation
    }

    /**
     * Returns the singleton SessionFactory, creating it on first call.
     * Thread-safe via double-checked locking.
     */
    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            synchronized (DatabaseConnection.class) {
                if (sessionFactory == null) {
                    sessionFactory = new Configuration()
                            .configure("hibernate.cfg.xml")
                            .buildSessionFactory();
                }
            }
        }
        return sessionFactory;
    }

    /**
     * Gracefully shuts down the SessionFactory.
     * Call this when the application is stopping.
     */
    public static void shutdown() {
        if (sessionFactory != null && !sessionFactory.isClosed()) {
            sessionFactory.close();
        }
    }
}
