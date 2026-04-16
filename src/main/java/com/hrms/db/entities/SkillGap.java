package com.hrms.db.entities;

import jakarta.persistence.*;

/**
 * Skill gaps identified during performance reviews.
 * Required by: Performance Management, Succession Planning.
 */
@Entity
@Table(name = "skill_gaps")
public class SkillGap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "skill_gap_id")
    private Long skillGapId;

    @Column(name = "emp_id", nullable = false, length = 20)
    private String empId;

    @Column(name = "skill_name", length = 100)
    private String skillName;

    @Column(name = "skill_current_level")
    private Integer skillCurrentLevel;

    @Column(name = "skill_target_level")
    private Integer skillTargetLevel;

    @Column(name = "training_plan_id")
    private Long trainingPlanId;

    // --- Getters & Setters ---

    public Long getSkillGapId() { return skillGapId; }
    public void setSkillGapId(Long skillGapId) { this.skillGapId = skillGapId; }

    public String getEmpId() { return empId; }
    public void setEmpId(String empId) { this.empId = empId; }

    public String getSkillName() { return skillName; }
    public void setSkillName(String skillName) { this.skillName = skillName; }

    public Integer getSkillCurrentLevel() { return skillCurrentLevel; }
    public void setSkillCurrentLevel(Integer skillCurrentLevel) { this.skillCurrentLevel = skillCurrentLevel; }

    public Integer getSkillTargetLevel() { return skillTargetLevel; }
    public void setSkillTargetLevel(Integer skillTargetLevel) { this.skillTargetLevel = skillTargetLevel; }

    public Long getTrainingPlanId() { return trainingPlanId; }
    public void setTrainingPlanId(Long trainingPlanId) { this.trainingPlanId = trainingPlanId; }
}
