package com.hrms.db.gui;

import com.hrms.db.facade.HRMSDatabaseFacade;
import com.hrms.db.gui.panels.DiagnosticsPanel;
import com.hrms.db.gui.panels.ErrorLogPanel;

import javax.swing.*;
import java.awt.*;

/**
 * DBAdminDashboard — The main entry point for the Database Team.
 * Note: The other subsystem teams do not run this. They only call the Facade.
 */
public class DBAdminDashboard extends JFrame {

    public DBAdminDashboard() {
        setTitle("HRMS Database Administration");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center on screen

        // Initialize the DB via the Facade
        try {
            HRMSDatabaseFacade.getInstance().initialize();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Fatal error initializing database: " + ex.getMessage(),
                    "Startup Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        // Main Tabbed Pane
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 14));

        // Note: Can add a new panel later, like "Schema Browser" or something.
        tabs.addTab("Diagnostics & Health", new ImageIcon(), new DiagnosticsPanel(), "Check DB Status");
        tabs.addTab("System Error Logs", new ImageIcon(), new ErrorLogPanel(), "View DB Exceptions");

        add(tabs, BorderLayout.CENTER);

        // Status bar
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusPanel.setBorder(BorderFactory.createEtchedBorder());
        JLabel statusLabel = new JLabel("Database Subsystem: ONLINE | " + System.getProperty("java.version"));
        statusPanel.add(statusLabel);
        add(statusPanel, BorderLayout.SOUTH);
        
        // Add shutdown hook to close the session factory
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            HRMSDatabaseFacade.getInstance().shutdown();
        }));
    }

    public static void main(String[] args) {
        // Use modern look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            new DBAdminDashboard().setVisible(true);
        });
    }
}
