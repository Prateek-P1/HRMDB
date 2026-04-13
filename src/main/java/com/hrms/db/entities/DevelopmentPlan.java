package com.hrms.db.entities;

import jakarta.persistence.*;

/**
 * Development plans for successors — tracks training/development activities.
 */
@Entity
@Table(name = "development_plans")
public class DevelopmentPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "plan_id")
    private Long planId;

    @Column(name = "successor_id", nullable = false, length = 20)
    private String successorId;

    @Column(name = "target_role_id")
    private Integer targetRoleId;

    @Column(name = "skill_gap_id")
    private Long skillGapId;

    @Column(name = "progress_percentage")
    private Double progressPercentage = 0.0;

    // --- Getters & Setters ---

    public Long getPlanId() { return planId; }
    public void setPlanId(Long planId) { this.planId = planId; }

    public String getSuccessorId() { return successorId; }
    public void setSuccessorId(String successorId) { this.successorId = successorId; }

    public Integer getTargetRoleId() { return targetRoleId; }
    public void setTargetRoleId(Integer targetRoleId) { this.targetRoleId = targetRoleId; }

    public Long getSkillGapId() { return skillGapId; }
    public void setSkillGapId(Long skillGapId) { this.skillGapId = skillGapId; }

    public Double getProgressPercentage() { return progressPercentage; }
    public void setProgressPercentage(Double progressPercentage) { this.progressPercentage = progressPercentage; }
}
