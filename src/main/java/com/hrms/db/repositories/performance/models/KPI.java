package com.hrms.db.repositories.performance.models;

import java.util.Date;

public class KPI {
    private int kpiId;
    private String name;
    private String description;
    private double targetValue;
    private String unit;           // e.g. "calls/day", "%", "score"
    private int employeeId;
    private String cycle;

    public KPI() {}

    public KPI(int kpiId, String name, String description, double targetValue, String unit, int employeeId, String cycle) {
        this.kpiId = kpiId;
        this.name = name;
        this.description = description;
        this.targetValue = targetValue;
        this.unit = unit;
        this.employeeId = employeeId;
        this.cycle = cycle;
    }

    public int getKpiId() { return kpiId; }
    public void setKpiId(int kpiId) { this.kpiId = kpiId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public double getTargetValue() { return targetValue; }
    public void setTargetValue(double targetValue) { this.targetValue = targetValue; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    public int getEmployeeId() { return employeeId; }
    public void setEmployeeId(int employeeId) { this.employeeId = employeeId; }
    public String getCycle() { return cycle; }
    public void setCycle(String cycle) { this.cycle = cycle; }

    @Override
    public String toString() {
        return "KPI{id=" + kpiId + ", name='" + name + "', target=" + targetValue + " " + unit + ", emp=" + employeeId + "}";
    }
}
