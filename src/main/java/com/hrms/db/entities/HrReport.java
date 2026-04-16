package com.hrms.db.entities;

import jakarta.persistence.*;
import java.time.LocalDate;

/**
 * HR reports — generated analytics reports.
 * Required by: HR Analytics & Reporting.
 */
@Entity
@Table(name = "hr_reports")
public class HrReport {

    @Id
    @Column(name = "report_id", length = 36)
    private String reportId;

    @Column(name = "report_name", length = 200)
    private String reportName;

    @Column(name = "report_type", length = 50)
    private String reportType; // ATTRITION, HEADCOUNT, COMPENSATION, PERFORMANCE

    @Column(name = "generated_date")
    private LocalDate generatedDate;

    @Column(name = "export_format", length = 10)
    private String exportFormat; // CSV, PDF, EXCEL

    @Column(name = "export_file_path", length = 500)
    private String exportFilePath;

    @Column(name = "schedule_config", length = 100)
    private String scheduleConfig; // DAILY, WEEKLY, MONTHLY, ON_DEMAND

    // --- Getters & Setters ---

    public String getReportId() { return reportId; }
    public void setReportId(String reportId) { this.reportId = reportId; }

    public String getReportName() { return reportName; }
    public void setReportName(String reportName) { this.reportName = reportName; }

    public String getReportType() { return reportType; }
    public void setReportType(String reportType) { this.reportType = reportType; }

    public LocalDate getGeneratedDate() { return generatedDate; }
    public void setGeneratedDate(LocalDate generatedDate) { this.generatedDate = generatedDate; }

    public String getExportFormat() { return exportFormat; }
    public void setExportFormat(String exportFormat) { this.exportFormat = exportFormat; }

    public String getExportFilePath() { return exportFilePath; }
    public void setExportFilePath(String exportFilePath) { this.exportFilePath = exportFilePath; }

    public String getScheduleConfig() { return scheduleConfig; }
    public void setScheduleConfig(String scheduleConfig) { this.scheduleConfig = scheduleConfig; }
}
