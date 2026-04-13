package com.hrms.db.entities;

import jakarta.persistence.*;

/**
 * Readiness scores — how ready an employee is for a target role.
 * Required by: Succession Planning.
 */
@Entity
@Table(name = "readiness_scores")
public class ReadinessScore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "score_id")
    private Long scoreId;

    @Column(name = "emp_id", nullable = false, length = 20)
    private String empId;

    @Column(name = "role_id")
    private Integer roleId;

    @Column(name = "appraisal_score")
    private Double appraisalScore;

    @Column(name = "competency_requirement", length = 200)
    private String competencyRequirement;

    @Column(name = "readiness_score")
    private Double readinessScore;

    @Column(name = "skill_gap_flag")
    private Boolean skillGapFlag = false;

    @Column(name = "skill_gap_detail", columnDefinition = "TEXT")
    private String skillGapDetail;

    // --- Getters & Setters ---

    public Long getScoreId() { return scoreId; }
    public void setScoreId(Long scoreId) { this.scoreId = scoreId; }

    public String getEmpId() { return empId; }
    public void setEmpId(String empId) { this.empId = empId; }

    public Integer getRoleId() { return roleId; }
    public void setRoleId(Integer roleId) { this.roleId = roleId; }

    public Double getAppraisalScore() { return appraisalScore; }
    public void setAppraisalScore(Double appraisalScore) { this.appraisalScore = appraisalScore; }

    public String getCompetencyRequirement() { return competencyRequirement; }
    public void setCompetencyRequirement(String v) { this.competencyRequirement = v; }

    public Double getReadinessScore() { return readinessScore; }
    public void setReadinessScore(Double readinessScore) { this.readinessScore = readinessScore; }

    public Boolean getSkillGapFlag() { return skillGapFlag; }
    public void setSkillGapFlag(Boolean skillGapFlag) { this.skillGapFlag = skillGapFlag; }

    public String getSkillGapDetail() { return skillGapDetail; }
    public void setSkillGapDetail(String skillGapDetail) { this.skillGapDetail = skillGapDetail; }
}
