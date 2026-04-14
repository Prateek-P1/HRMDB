package com.hrms.db.repositories.performance.models;

import java.util.List;

public class DeptReport {
    private int deptId;
    private int cycleId;
    private double avgRating;
    private int totalEmployees;
    private int appraisalsCompleted;
    private List<Employee> topPerformers;

    public DeptReport() {}

    public int getDeptId() { return deptId; }
    public void setDeptId(int deptId) { this.deptId = deptId; }
    public int getCycleId() { return cycleId; }
    public void setCycleId(int cycleId) { this.cycleId = cycleId; }
    public double getAvgRating() { return avgRating; }
    public void setAvgRating(double avgRating) { this.avgRating = avgRating; }
    public int getTotalEmployees() { return totalEmployees; }
    public void setTotalEmployees(int totalEmployees) { this.totalEmployees = totalEmployees; }
    public int getAppraisalsCompleted() { return appraisalsCompleted; }
    public void setAppraisalsCompleted(int appraisalsCompleted) { this.appraisalsCompleted = appraisalsCompleted; }
    public List<Employee> getTopPerformers() { return topPerformers; }
    public void setTopPerformers(List<Employee> topPerformers) { this.topPerformers = topPerformers; }

    @Override
    public String toString() {
        return "DeptReport{deptId=" + deptId + ", avgRating=" + avgRating + ", total=" + totalEmployees + ", completed=" + appraisalsCompleted + "}";
    }
}
