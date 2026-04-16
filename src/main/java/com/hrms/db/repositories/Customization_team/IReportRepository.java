package com.hrms.db.repositories.Customization_team;
import java.util.Date;
import java.util.List;

/**
 * INTERFACE: IReportRepository
 * Subsystem: Customization
 * Component: Report Builder
 *
 * Provides DB access for saving and retrieving custom report definitions,
 * data sources, and export format settings.
 *
 * DB Team: Please implement this interface and provide the concrete class.
 * Entity needed:
 *   Report { reportId: int, reportName: String, inputType: String, format: String,
 *            dataSource: String, schedule: String, generatedDate: Date }
 */
public interface IReportRepository {

    /**
     * WRITE: Save a new report definition. Returns the generated reportId.
     */
    int saveReport(String name, String inputType, String format);

    /**
     * READ: Fetch a report definition by its ID.
     */
    Report getReportById(int reportId);

    /**
     * READ: Return all report definitions.
     */
    List<Report> getAllReports();

    /**
     * WRITE: Update report type (e.g., Table / Chart / Dashboard).
     */
    void customizeReportType(int reportId, String type);

    /**
     * WRITE: Set the export format for a report (e.g., PDF / Excel / CSV).
     */
    void exportReportFormat(int reportId, String format);

    /**
     * WRITE: Trigger report generation — DB populates the result.
     */
    void generateReport(int reportId);

    /**
     * WRITE: Delete a report definition by its ID.
     */
    void deleteReport(int reportId);
}

/**
 * DTO: Report
 * Maps to the Report entity in the database.
 */
class Report {
    public int reportId;
    public String reportName;
    public String inputType;
    public String format;
    public String dataSource;
    public String schedule;
    public Date generatedDate;  // READ-ONLY: system-generated, not set by user
}
