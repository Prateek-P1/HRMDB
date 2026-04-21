package com.hrms.db.gui.panels;

import com.hrms.db.config.DatabaseConnection;
import com.hrms.db.facade.HRMSDatabaseFacade;
import org.hibernate.Session;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Consumer;

/**
 * Diagnostics panel for local database health checks.
 */
public class DiagnosticsPanel extends JPanel {

    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm:ss a");

    private final JTextArea outputArea;
    private final JLabel dbPathValue;
    private final JLabel sessionFactoryValue;
    private final JLabel lastRunValue;
    private final JButton runButton;
    private final JButton rebootButton;
    private final Consumer<String> statusCallback;

    public DiagnosticsPanel(Consumer<String> statusCallback) {
        this.statusCallback = statusCallback;

        setLayout(new BorderLayout(0, 12));
        setBorder(new EmptyBorder(8, 8, 8, 8));

        JPanel summaryPanel = new JPanel(new GridLayout(1, 3, 12, 0));
        dbPathValue = createValueLabel(Paths.get("hrms.db").toAbsolutePath().toString());
        sessionFactoryValue = createValueLabel("Unknown");
        lastRunValue = createValueLabel("Never");
        summaryPanel.add(createMetricCard("Database File", dbPathValue));
        summaryPanel.add(createMetricCard("Session Factory", sessionFactoryValue));
        summaryPanel.add(createMetricCard("Last Diagnostics Run", lastRunValue));

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        runButton = new JButton("Run Diagnostics");
        runButton.addActionListener(e -> refreshData());
        rebootButton = new JButton("Reboot Subsystem");
        rebootButton.addActionListener(e -> rebootSubsystem());
        controlPanel.add(runButton);
        controlPanel.add(rebootButton);

        JPanel northPanel = new JPanel(new BorderLayout(0, 12));
        northPanel.add(summaryPanel, BorderLayout.CENTER);
        northPanel.add(controlPanel, BorderLayout.SOUTH);

        outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setFont(new Font("Consolas", Font.PLAIN, 13));
        outputArea.setMargin(new Insets(10, 10, 10, 10));

        add(northPanel, BorderLayout.NORTH);
        add(new JScrollPane(outputArea), BorderLayout.CENTER);
    }

    public void refreshData() {
        runDiagnostics(false);
    }

    public void rebootSubsystem() {
        runDiagnostics(true);
    }

    private void runDiagnostics(boolean rebootFirst) {
        setBusy(true, rebootFirst ? "Rebooting subsystem..." : "Running diagnostics...");
        outputArea.setText("Running diagnostics...\n");

        SwingWorker<DiagnosticsResult, String> worker = new SwingWorker<>() {
            @Override
            protected DiagnosticsResult doInBackground() throws Exception {
                if (rebootFirst) {
                    publish("Shutting down subsystem...");
                    HRMSDatabaseFacade.getInstance().shutdown();
                    publish("Reinitializing subsystem...");
                    HRMSDatabaseFacade.getInstance().initialize();
                }

                publish("SessionFactory present: " + (DatabaseConnection.getSessionFactory() != null));

                try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
                    DiagnosticsResult result = new DiagnosticsResult();
                    session.doWork((Connection connection) -> {
                        DatabaseMetaData meta = connection.getMetaData();
                        result.databaseProduct = meta.getDatabaseProductName();
                        result.databaseVersion = meta.getDatabaseProductVersion();
                        result.driverName = meta.getDriverName();
                    });

                    result.employeeCount = countOrZero(session.createQuery("SELECT COUNT(e) FROM Employee e", Long.class).uniqueResult());
                    result.payrollCount = countOrZero(session.createQuery("SELECT COUNT(p) FROM PayrollResult p", Long.class).uniqueResult());
                    result.errorCount = countOrZero(session.createQuery(
                            "SELECT COUNT(s) FROM SecurityAuditLog s WHERE s.actionType IN ('DB_ERROR', 'DB_LOG')",
                            Long.class).uniqueResult());
                    result.generatedAt = LocalDateTime.now();
                    return result;
                }
            }

            @Override
            protected void process(List<String> chunks) {
                for (String chunk : chunks) {
                    append(chunk);
                }
            }

            @Override
            protected void done() {
                try {
                    DiagnosticsResult result = get();
                    sessionFactoryValue.setText("ACTIVE");
                    lastRunValue.setText(TIME_FORMAT.format(result.generatedAt));

                    append("Database Product: " + result.databaseProduct);
                    append("Database Version: " + result.databaseVersion);
                    append("Driver Name: " + result.driverName);
                    append("Connection: SUCCESS");
                    append("");
                    append("--- Table Statistics ---");
                    append("Total Employees: " + result.employeeCount);
                    append("Total Payroll Records: " + result.payrollCount);
                    append("Total Database Errors Logged: " + result.errorCount);

                    setBusy(false, rebootFirst ? "Subsystem rebooted successfully" : "Diagnostics complete");
                } catch (Exception ex) {
                    sessionFactoryValue.setText("ERROR");
                    append("ERROR: " + ex.getMessage());
                    setBusy(false, "Diagnostics failed");
                }
            }
        };

        worker.execute();
    }

    private JPanel createMetricCard(String label, JLabel valueLabel) {
        JPanel panel = new JPanel(new BorderLayout(0, 6));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 224, 228)),
                new EmptyBorder(12, 12, 12, 12)
        ));

        JLabel labelComponent = new JLabel(label);
        labelComponent.setFont(new Font("Segoe UI", Font.BOLD, 12));
        labelComponent.setForeground(new Color(77, 86, 98));
        panel.add(labelComponent, BorderLayout.NORTH);
        panel.add(valueLabel, BorderLayout.CENTER);
        return panel;
    }

    private JLabel createValueLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        return label;
    }

    private long countOrZero(Long value) {
        return value != null ? value : 0L;
    }

    private void append(String text) {
        outputArea.append(text + "\n");
        outputArea.setCaretPosition(outputArea.getDocument().getLength());
    }

    private void setBusy(boolean busy, String message) {
        runButton.setEnabled(!busy);
        rebootButton.setEnabled(!busy);
        if (statusCallback != null) {
            statusCallback.accept(message);
        }
    }

    private static final class DiagnosticsResult {
        private String databaseProduct;
        private String databaseVersion;
        private String driverName;
        private long employeeCount;
        private long payrollCount;
        private long errorCount;
        private LocalDateTime generatedAt;
    }
}
