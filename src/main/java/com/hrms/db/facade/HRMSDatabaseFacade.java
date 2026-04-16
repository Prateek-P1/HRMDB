package com.hrms.db.facade;

import com.hrms.db.config.DatabaseConnection;
import com.hrms.db.factory.RepositoryFactory;

/**
 * HRMSDatabaseFacade — Implementation of the Facade Pattern.
 *
 * This is the root entry point for the entire Database Subsystem.
 * Other teams use this to initialize the database connection pool (via Hibernate)
 * and to request the RepositoryFactory. It provides a simple, simplified interface
 * to our complex subsystem.
 */
public class HRMSDatabaseFacade {

    private static HRMSDatabaseFacade instance;

    private HRMSDatabaseFacade() {}

    /**
     * Get the singleton instance of the Database Facade.
     */
    public static synchronized HRMSDatabaseFacade getInstance() {
        if (instance == null) {
            instance = new HRMSDatabaseFacade();
        }
        return instance;
    }

    /**
     * Bootstraps the database subsystem.
     * Starts the Hibernate SessionFactory by reading hibernate.cfg.xml.
     */
    public void initialize() {
        // Trigger initialization of the SessionFactory
        DatabaseConnection.getSessionFactory();
        System.out.println("[HRMSDatabaseFacade] Database subsystem initialized successfully.");
    }

    /**
     * Shuts down the database subsystem cleanly.
     * Closes the connection pool and cleans up resources.
     */
    public void shutdown() {
        DatabaseConnection.shutdown();
        System.out.println("[HRMSDatabaseFacade] Database subsystem shut down gracefully.");
    }

    /**
     * Returns the RepositoryFactory, which teams use to request their specific interfaces.
     */
    public RepositoryFactory getRepositories() {
        return RepositoryFactory.getInstance();
    }
}
