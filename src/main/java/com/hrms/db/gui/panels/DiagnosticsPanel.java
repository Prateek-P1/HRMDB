package com.hrms.db.gui.panels;

import com.hrms.db.config.DatabaseConnection;
import com.hrms.db.facade.HRMSDatabaseFacade;
import org.hibernate.Session;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import org.hibernate.jdbc.Work;

/**
 * DiagnosticsPanel — Checks DB connection health and lists basic schema stats.
 */
public class DiagnosticsPanel extends JPanel {

    private final JTextArea outputArea;

    public DiagnosticsPanel() {
        setLayout(new BorderLayout());

        outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        add(new JScrollPane(outputArea), BorderLayout.CENTER);

        JPanel ctrlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnRun = new JButton("Run Diagnostics");
        btnRun.addActionListener(e -> runDiagnostics());
        ctrlPanel.add(btnRun);
        
        JButton btnReboot = new JButton("Reboot Subsystem");
        btnReboot.addActionListener(e -> {
            HRMSDatabaseFacade.getInstance().shutdown();
            HRMSDatabaseFacade.getInstance().initialize();
            runDiagnostics();
        });
        ctrlPanel.add(btnReboot);

        add(ctrlPanel, BorderLayout.NORTH);
    }

    private void runDiagnostics() {
        outputArea.setText("Running Diagnostics...\n");
        append("Hibernate SessionFactory: " + (DatabaseConnection.getSessionFactory() != null ? "ACTIVE" : "OFFLINE"));

        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            session.doWork(new Work() {
                @Override
                public void execute(Connection connection) throws java.sql.SQLException {
                    DatabaseMetaData meta = connection.getMetaData();
                    append("Database Product: " + meta.getDatabaseProductName());
                    append("Database Version: " + meta.getDatabaseProductVersion());
                    append("Driver Name: " + meta.getDriverName());
                    append("Connection: SUCCESS");
                }
            });

            // Quick Entity Counts
            long empCount = session.createQuery("SELECT COUNT(e) FROM Employee e", Long.class).uniqueResult();
            long payrollCount = session.createQuery("SELECT COUNT(p) FROM PayrollResult p", Long.class).uniqueResult();
            long errorCount = session.createQuery("SELECT COUNT(s) FROM SecurityAuditLog s WHERE s.actionType = 'DB_ERROR'", Long.class).uniqueResult();

            append("\n--- Table Statistics ---");
            append("Total Employees: " + empCount);
            append("Total Payroll Records: " + payrollCount);
            append("Total Database Errors Logged: " + errorCount);

        } catch (Exception ex) {
            append("ERROR: " + ex.getMessage());
        }
    }

    private void append(String text) {
        outputArea.append(text + "\n");
    }
}
