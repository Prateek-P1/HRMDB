package com.hrms.db.gui;

import com.hrms.db.facade.HRMSDatabaseFacade;
import com.hrms.db.gui.panels.DiagnosticsPanel;
import com.hrms.db.gui.panels.ErrorLogPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Main local administration dashboard for the database team.
 */
public class DBAdminDashboard extends JFrame {

    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm:ss a");

    private final JLabel subsystemStatusLabel = new JLabel("Subsystem: Starting");
    private final JLabel activityLabel = new JLabel("Preparing dashboard...");
    private final DiagnosticsPanel diagnosticsPanel;
    private final ErrorLogPanel errorLogPanel;

    public DBAdminDashboard() {
        setTitle("HRMS Database Administration");
        setSize(1180, 760);
        setMinimumSize(new Dimension(980, 640));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        try {
            HRMSDatabaseFacade.getInstance().initialize();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Fatal error initializing database: " + ex.getMessage(),
                    "Startup Error",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        diagnosticsPanel = new DiagnosticsPanel(this::setActivity);
        errorLogPanel = new ErrorLogPanel(this::setActivity);

        add(buildHeader(), BorderLayout.NORTH);
        add(buildTabs(), BorderLayout.CENTER);
        add(buildStatusBar(), BorderLayout.SOUTH);

        setSubsystemStatus("ONLINE");
        setActivity("Dashboard ready");

        Runtime.getRuntime().addShutdownHook(new Thread(() -> HRMSDatabaseFacade.getInstance().shutdown()));

        SwingUtilities.invokeLater(() -> {
            diagnosticsPanel.refreshData();
            errorLogPanel.refreshData();
        });
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> new DBAdminDashboard().setVisible(true));
    }

    private JComponent buildHeader() {
        JPanel header = new JPanel(new BorderLayout(18, 0));
        header.setBorder(new EmptyBorder(16, 18, 10, 18));

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("HRMS Database Admin");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("Local diagnostics, health checks, and system error visibility for the database team");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitle.setForeground(new Color(88, 98, 110));
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        titlePanel.add(title);
        titlePanel.add(Box.createVerticalStrut(4));
        titlePanel.add(subtitle);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));

        JButton refreshAllButton = new JButton("Refresh All");
        refreshAllButton.addActionListener(e -> {
            setActivity("Refreshing all dashboard panels...");
            diagnosticsPanel.refreshData();
            errorLogPanel.refreshData();
        });

        JButton rebootButton = new JButton("Reboot DB");
        rebootButton.addActionListener(e -> diagnosticsPanel.rebootSubsystem());

        actionPanel.add(refreshAllButton);
        actionPanel.add(rebootButton);

        header.add(titlePanel, BorderLayout.CENTER);
        header.add(actionPanel, BorderLayout.EAST);
        return header;
    }

    private JComponent buildTabs() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tabs.addTab("Diagnostics & Health", diagnosticsPanel);
        tabs.addTab("System Error Logs", errorLogPanel);
        tabs.addChangeListener(e -> setActivity("Viewing " + tabs.getTitleAt(tabs.getSelectedIndex())));
        return tabs;
    }

    private JComponent buildStatusBar() {
        JPanel statusPanel = new JPanel(new BorderLayout(12, 0));
        statusPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEtchedBorder(),
                new EmptyBorder(8, 12, 8, 12)
        ));

        subsystemStatusLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        activityLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        left.add(subsystemStatusLabel);
        left.add(new JLabel("|"));
        left.add(activityLabel);

        JLabel runtimeLabel = new JLabel("Java " + System.getProperty("java.version"));
        runtimeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        statusPanel.add(left, BorderLayout.WEST);
        statusPanel.add(runtimeLabel, BorderLayout.EAST);
        return statusPanel;
    }

    private void setSubsystemStatus(String status) {
        subsystemStatusLabel.setText("Subsystem: " + status);
    }

    private void setActivity(String message) {
        activityLabel.setText(message + " | " + LocalDateTime.now().format(TIME_FORMAT));
    }
}
