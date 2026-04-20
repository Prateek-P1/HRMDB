package com.hrms.db.repositories.multicountry;

import java.time.LocalDateTime;

public class ReportDTO {
    private String reportName;
    private LocalDateTime generatedAt;
    private String content;

    public ReportDTO() {
    }

    public ReportDTO(String reportName, LocalDateTime generatedAt, String content) {
        this.reportName = reportName;
        this.generatedAt = generatedAt;
        this.content = content;
    }

    public String getReportName() { return reportName; }
    public void setReportName(String reportName) { this.reportName = reportName; }

    public LocalDateTime getGeneratedAt() { return generatedAt; }
    public void setGeneratedAt(LocalDateTime generatedAt) { this.generatedAt = generatedAt; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}
