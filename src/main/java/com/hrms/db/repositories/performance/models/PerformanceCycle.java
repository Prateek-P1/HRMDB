package com.hrms.db.repositories.performance.models;

import java.util.Date;

public class PerformanceCycle {
    private int cycleId;
    private String name;       // e.g. "2025 Annual Review"
    private String type;       // ANNUAL, QUARTERLY, MONTHLY
    private Date startDate;
    private Date endDate;
    private boolean active;

    public PerformanceCycle() {}

    public PerformanceCycle(int cycleId, String name, String type, Date startDate, Date endDate) {
        this.cycleId = cycleId;
        this.name = name;
        this.type = type;
        this.startDate = startDate;
        this.endDate = endDate;
        this.active = true;
    }

    public int getCycleId() { return cycleId; }
    public void setCycleId(int cycleId) { this.cycleId = cycleId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public Date getStartDate() { return startDate; }
    public void setStartDate(Date startDate) { this.startDate = startDate; }
    public Date getEndDate() { return endDate; }
    public void setEndDate(Date endDate) { this.endDate = endDate; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    @Override
    public String toString() {
        return "PerformanceCycle{id=" + cycleId + ", name='" + name + "', type='" + type + "', active=" + active + "}";
    }
}
