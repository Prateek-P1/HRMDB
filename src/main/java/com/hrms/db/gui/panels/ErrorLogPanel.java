package com.hrms.db.gui.panels;

import com.hrms.db.config.DatabaseConnection;
import com.hrms.db.entities.SecurityAuditLog;
import org.hibernate.Session;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

/**
 * Error log viewer for database and security audit events.
 */
public class ErrorLogPanel extends JPanel {

    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm:ss a");

    private final JTable table;
    private final DefaultTableModel tableModel;
    private final JTextArea detailsArea;
    private final JLabel statusLabel;
    private final JComboBox<String> filterBox;
    private final JButton refreshButton;
    private final Consumer<String> statusCallback;
    private List<SecurityAuditLog> currentLogs = Collections.emptyList();

    public ErrorLogPanel(Consumer<String> statusCallback) {
        this.statusCallback = statusCallback;

        setLayout(new BorderLayout(0, 12));
        setBorder(new EmptyBorder(8, 8, 8, 8));

        String[] columns = {"Timestamp", "Type", "Outcome", "Operation"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(24);
        table.getSelectionModel().addListSelectionListener(this::updateDetailsPane);
        table.getColumnModel().getColumn(0).setPreferredWidth(170);
        table.getColumnModel().getColumn(1).setPreferredWidth(120);
        table.getColumnModel().getColumn(2).setPreferredWidth(100);
        table.getColumnModel().getColumn(3).setPreferredWidth(420);

        detailsArea = new JTextArea();
        detailsArea.setEditable(false);
        detailsArea.setLineWrap(true);
        detailsArea.setWrapStyleWord(true);
        detailsArea.setMargin(new Insets(10, 10, 10, 10));
        detailsArea.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        detailsArea.setText("Select a log row to inspect the full details.");

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                new JScrollPane(table),
                new JScrollPane(detailsArea));
        splitPane.setResizeWeight(0.72);

        JPanel controlPanel = new JPanel(new BorderLayout(12, 0));
        JPanel leftControls = new JPanel(new FlowLayout(FlowLayout.LEFT));

        refreshButton = new JButton("Refresh Error Logs");
        refreshButton.addActionListener(e -> refreshData());
        leftControls.add(refreshButton);

        filterBox = new JComboBox<>(new String[] {
                "DB errors only",
                "DB errors and DB logs",
                "All security logs"
        });
        filterBox.addActionListener(e -> refreshData());
        leftControls.add(filterBox);

        statusLabel = new JLabel("Loading logs...");
        statusLabel.setForeground(new Color(88, 98, 110));

        controlPanel.add(leftControls, BorderLayout.WEST);
        controlPanel.add(statusLabel, BorderLayout.EAST);

        add(controlPanel, BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);
    }

    public void refreshData() {
        setBusy(true, "Refreshing error logs...");
        statusLabel.setText("Loading logs...");
        tableModel.setRowCount(0);
        detailsArea.setText("Select a log row to inspect the full details.");

        SwingWorker<List<SecurityAuditLog>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<SecurityAuditLog> doInBackground() {
                try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
                    String selectedFilter = (String) filterBox.getSelectedItem();
                    String hql = switch (selectedFilter) {
                        case "DB errors only" ->
                                "FROM SecurityAuditLog s WHERE s.actionType = 'DB_ERROR' ORDER BY s.timestamp DESC";
                        case "All security logs" ->
                                "FROM SecurityAuditLog s ORDER BY s.timestamp DESC";
                        default ->
                                "FROM SecurityAuditLog s WHERE s.actionType IN ('DB_ERROR', 'DB_LOG') ORDER BY s.timestamp DESC";
                    };

                    return session.createQuery(hql, SecurityAuditLog.class)
                            .setMaxResults(150)
                            .getResultList();
                }
            }

            @Override
            protected void done() {
                try {
                    currentLogs = get();
                    for (SecurityAuditLog log : currentLogs) {
                        tableModel.addRow(new Object[] {
                                log.getTimestamp() != null ? TIME_FORMAT.format(log.getTimestamp()) : "Unknown",
                                safe(log.getActionType()),
                                safe(log.getOutcome()),
                                safe(log.getOperation())
                        });
                    }

                    if (!currentLogs.isEmpty()) {
                        table.setRowSelectionInterval(0, 0);
                        updateDetailsFromIndex(0);
                    } else {
                        detailsArea.setText("No logs matched the current filter.");
                    }

                    statusLabel.setText(currentLogs.size() + " log(s) loaded");
                    setBusy(false, "Error log refresh complete");
                } catch (Exception ex) {
                    currentLogs = Collections.emptyList();
                    statusLabel.setText("Refresh failed");
                    detailsArea.setText("Failed to load logs.\n\n" + ex.getMessage());
                    setBusy(false, "Error log refresh failed");
                    JOptionPane.showMessageDialog(ErrorLogPanel.this,
                            "Failed to load error logs: " + ex.getMessage(),
                            "Database Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        };

        worker.execute();
    }

    private void updateDetailsPane(ListSelectionEvent event) {
        if (!event.getValueIsAdjusting() && table.getSelectedRow() >= 0) {
            updateDetailsFromIndex(table.getSelectedRow());
        }
    }

    private void updateDetailsFromIndex(int index) {
        if (index < 0 || index >= currentLogs.size()) {
            return;
        }

        SecurityAuditLog log = currentLogs.get(index);
        String details = "Timestamp: " + (log.getTimestamp() != null ? TIME_FORMAT.format(log.getTimestamp()) : "Unknown") +
                "\nType: " + safe(log.getActionType()) +
                "\nOutcome: " + safe(log.getOutcome()) +
                "\nOperation: " + safe(log.getOperation()) +
                "\nUser: " + safe(log.getUserId()) +
                "\n\nDetails:\n" + safe(log.getDetails());
        detailsArea.setText(details);
        detailsArea.setCaretPosition(0);
    }

    private String safe(String value) {
        return value != null && !value.isBlank() ? value : "N/A";
    }

    private void setBusy(boolean busy, String message) {
        refreshButton.setEnabled(!busy);
        filterBox.setEnabled(!busy);
        if (statusCallback != null) {
            statusCallback.accept(message);
        }
    }
}
