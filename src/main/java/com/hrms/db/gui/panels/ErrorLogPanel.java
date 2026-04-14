package com.hrms.db.gui.panels;

import com.hrms.db.config.DatabaseConnection;
import com.hrms.db.entities.SecurityAuditLog;
import org.hibernate.Session;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * ErrorLogPanel — A GUI panel that queries the security_audit_logs 
 * table to display DB_ERROR actions logged by the DatabaseErrorLogger.
 */
public class ErrorLogPanel extends JPanel {

    private final JTable table;
    private final DefaultTableModel tableModel;

    public ErrorLogPanel() {
        setLayout(new BorderLayout());

        // Define table columns
        String[] columns = {"Timestamp", "Outcome", "Operation", "Details"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        
        // Adjust column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(150);
        table.getColumnModel().getColumn(1).setPreferredWidth(80);
        table.getColumnModel().getColumn(2).setPreferredWidth(180);
        table.getColumnModel().getColumn(3).setPreferredWidth(400);

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // Control Panel
        JPanel ctrlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnRefresh = new JButton("Refresh Error Logs");
        btnRefresh.addActionListener(e -> loadData());
        ctrlPanel.add(btnRefresh);
        add(ctrlPanel, BorderLayout.NORTH);

        // Load initial data
        loadData();
    }

    private void loadData() {
        tableModel.setRowCount(0); // clear existing
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            List<SecurityAuditLog> logs = session.createQuery(
                    "FROM SecurityAuditLog s WHERE s.actionType IN ('DB_ERROR', 'DB_LOG') ORDER BY s.timestamp DESC", 
                    SecurityAuditLog.class).setMaxResults(100).getResultList();

            for (SecurityAuditLog log : logs) {
                tableModel.addRow(new Object[]{
                        log.getTimestamp(),
                        log.getOutcome(),
                        log.getOperation(),
                        log.getDetails()
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to load error logs: " + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
