package com.hrms.db.repositories.performance.models;

import java.util.List;

public class SkillGapSummary {
    private int employeeId;
    private String employeeName;
    private List<SkillGap> gaps;

    public SkillGapSummary() {}

    public SkillGapSummary(int employeeId, String employeeName, List<SkillGap> gaps) {
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.gaps = gaps;
    }

    public int getEmployeeId() { return employeeId; }
    public void setEmployeeId(int employeeId) { this.employeeId = employeeId; }
    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }
    public List<SkillGap> getGaps() { return gaps; }
    public void setGaps(List<SkillGap> gaps) { this.gaps = gaps; }

    @Override
    public String toString() {
        return "SkillGapSummary{emp=" + employeeId + ", name='" + employeeName + "', gapCount=" + (gaps != null ? gaps.size() : 0) + "}";
    }
}
