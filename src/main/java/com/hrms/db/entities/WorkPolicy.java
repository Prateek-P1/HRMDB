package com.hrms.db.entities;

import jakarta.persistence.*;

/**
 * Work policies — standard hours, overtime thresholds, break limits.
 * Required by: Time Tracking subsystem.
 */
@Entity
@Table(name = "work_policies")
public class WorkPolicy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "policy_id")
    private Integer policyId;

    @Column(name = "policy_name", length = 100)
    private String policyName;

    @Column(name = "standard_work_hours")
    private Double standardWorkHours;

    @Column(name = "overtime_threshold")
    private Double overtimeThreshold;

    @Column(name = "max_break_duration")
    private Double maxBreakDuration;

    // --- Getters & Setters ---

    public Integer getPolicyId() { return policyId; }
    public void setPolicyId(Integer policyId) { this.policyId = policyId; }

    public String getPolicyName() { return policyName; }
    public void setPolicyName(String policyName) { this.policyName = policyName; }

    public Double getStandardWorkHours() { return standardWorkHours; }
    public void setStandardWorkHours(Double standardWorkHours) { this.standardWorkHours = standardWorkHours; }

    public Double getOvertimeThreshold() { return overtimeThreshold; }
    public void setOvertimeThreshold(Double overtimeThreshold) { this.overtimeThreshold = overtimeThreshold; }

    public Double getMaxBreakDuration() { return maxBreakDuration; }
    public void setMaxBreakDuration(Double maxBreakDuration) { this.maxBreakDuration = maxBreakDuration; }
}
