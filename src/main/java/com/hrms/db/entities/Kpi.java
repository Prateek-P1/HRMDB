package com.hrms.db.entities;

import jakarta.persistence.*;

/**
 * Key Performance Indicators — linked to goals.
 * Required by: Performance Management.
 */
@Entity
@Table(name = "kpis")
public class Kpi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "kpi_id")
    private Long kpiId;

    @Column(name = "goal_id")
    private Long goalId;

    @Column(name = "emp_id", length = 20)
    private String empId;

    @Column(name = "kpi_name", length = 200)
    private String kpiName;

    @Column(name = "kpi_target_value")
    private Double kpiTargetValue;

    @Column(name = "kpi_actual_value")
    private Double kpiActualValue;

    @Column(name = "kpi_unit", length = 30)
    private String kpiUnit;

    // --- Getters & Setters ---

    public Long getKpiId() { return kpiId; }
    public void setKpiId(Long kpiId) { this.kpiId = kpiId; }

    public Long getGoalId() { return goalId; }
    public void setGoalId(Long goalId) { this.goalId = goalId; }

    public String getEmpId() { return empId; }
    public void setEmpId(String empId) { this.empId = empId; }

    public String getKpiName() { return kpiName; }
    public void setKpiName(String kpiName) { this.kpiName = kpiName; }

    public Double getKpiTargetValue() { return kpiTargetValue; }
    public void setKpiTargetValue(Double kpiTargetValue) { this.kpiTargetValue = kpiTargetValue; }

    public Double getKpiActualValue() { return kpiActualValue; }
    public void setKpiActualValue(Double kpiActualValue) { this.kpiActualValue = kpiActualValue; }

    public String getKpiUnit() { return kpiUnit; }
    public void setKpiUnit(String kpiUnit) { this.kpiUnit = kpiUnit; }
}
