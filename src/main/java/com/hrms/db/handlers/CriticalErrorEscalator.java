package com.hrms.db.handlers;

import javax.swing.JOptionPane;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * CriticalErrorEscalator — final link in the error-handler chain.
 *
 * For CRITICAL errors only, this handler:
 *   1. Formats a detailed summary message
 *   2. Shows a modal Swing dialog to alert whoever is watching the admin GUI
 *
 * For non-CRITICAL levels, it does nothing (end of chain).
 *
 * DESIGN NOTE: In a production system this would also send an email/SMS/PagerDuty
 * alert. For the course project, the Swing dialog is sufficient.
 *
 * IMPORTANT: This handler calls passToNext() before showing the dialog so that
 * logging always completes before the UI blocks.
 */
public class CriticalErrorEscalator extends ErrorHandler {

    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public CriticalErrorEscalator(ErrorHandler next) {
        super(next);
    }

    @Override
    public void handle(String operation, Exception ex, ErrorLevel level) {
        // Always pass to next first (end of chain is null — passToNext is a no-op)
        passToNext(operation, ex, level);

        if (level == ErrorLevel.CRITICAL) {
            escalate(operation, ex);
        }
    }

    private void escalate(String operation, Exception ex) {
        String timestamp = LocalDateTime.now().format(FMT);
        String message = ex != null ? ex.getMessage() : "Unknown critical failure";

        String alert = String.format(
                "⛔ CRITICAL DATABASE ERROR%n%n" +
                "Time:      %s%n" +
                "Operation: %s%n" +
                "Error:     %s%n%n" +
                "Action required: Check the Error Log tab in the DB Admin panel.",
                timestamp, operation, message);

        // Show on the EDT if possible; otherwise show directly (for non-GUI contexts)
        if (java.awt.EventQueue.isDispatchThread()) {
            showDialog(alert);
        } else {
            try {
                java.awt.EventQueue.invokeAndWait(() -> showDialog(alert));
            } catch (Exception e) {
                System.err.println("[CriticalErrorEscalator] Could not show dialog: " + e.getMessage());
            }
        }
    }

    private void showDialog(String message) {
        JOptionPane.showMessageDialog(
                null,
                message,
                "CRITICAL DATABASE ERROR",
                JOptionPane.ERROR_MESSAGE
        );
    }
}
