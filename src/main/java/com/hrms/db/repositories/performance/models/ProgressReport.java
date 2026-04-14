package com.hrms.db.repositories.performance.models;

import java.util.List;

public class ProgressReport {
    private int employeeId;
    private int cycleId;
    private float avgGoalProgress;
    private int totalGoals;
    private int completedGoals;
    private List<KPI> kpis;

    public ProgressReport() {}

    public int getEmployeeId() { return employeeId; }
    public void setEmployeeId(int employeeId) { this.employeeId = employeeId; }
    public int getCycleId() { return cycleId; }
    public void setCycleId(int cycleId) { this.cycleId = cycleId; }
    public float getAvgGoalProgress() { return avgGoalProgress; }
    public void setAvgGoalProgress(float avgGoalProgress) { this.avgGoalProgress = avgGoalProgress; }
    public int getTotalGoals() { return totalGoals; }
    public void setTotalGoals(int totalGoals) { this.totalGoals = totalGoals; }
    public int getCompletedGoals() { return completedGoals; }
    public void setCompletedGoals(int completedGoals) { this.completedGoals = completedGoals; }
    public List<KPI> getKpis() { return kpis; }
    public void setKpis(List<KPI> kpis) { this.kpis = kpis; }

    @Override
    public String toString() {
        return "ProgressReport{empId=" + employeeId + ", avgGoalProgress=" + avgGoalProgress + "%, completedGoals=" + completedGoals + "/" + totalGoals + "}";
    }
}
