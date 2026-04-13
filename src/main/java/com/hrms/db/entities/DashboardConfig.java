package com.hrms.db.entities;

import jakarta.persistence.*;

/**
 * Dashboard configurations — widget layout and filter settings.
 * Required by: HR Analytics & Reporting.
 */
@Entity
@Table(name = "dashboard_configs")
public class DashboardConfig {

    @Id
    @Column(name = "dashboard_id", length = 36)
    private String dashboardId;

    @Column(name = "dashboard_name", length = 200)
    private String dashboardName;

    @Column(name = "user_id", length = 20)
    private String userId;

    @Column(name = "filter_criteria", columnDefinition = "TEXT")
    private String filterCriteria; // JSON

    @Column(name = "widget_list", columnDefinition = "TEXT")
    private String widgetList; // JSON

    @Column(name = "date_range", length = 50)
    private String dateRange;

    // --- Getters & Setters ---

    public String getDashboardId() { return dashboardId; }
    public void setDashboardId(String dashboardId) { this.dashboardId = dashboardId; }

    public String getDashboardName() { return dashboardName; }
    public void setDashboardName(String dashboardName) { this.dashboardName = dashboardName; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getFilterCriteria() { return filterCriteria; }
    public void setFilterCriteria(String filterCriteria) { this.filterCriteria = filterCriteria; }

    public String getWidgetList() { return widgetList; }
    public void setWidgetList(String widgetList) { this.widgetList = widgetList; }

    public String getDateRange() { return dateRange; }
    public void setDateRange(String dateRange) { this.dateRange = dateRange; }
}
