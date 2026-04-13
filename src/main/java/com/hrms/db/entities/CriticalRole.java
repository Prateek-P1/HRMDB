package com.hrms.db.entities;

import jakarta.persistence.*;

/**
 * Critical roles in the organization — used by Succession Planning.
 */
@Entity
@Table(name = "critical_roles")
public class CriticalRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Integer roleId;

    @Column(name = "role_name", nullable = false, length = 200)
    private String roleName;

    @Column(name = "department", length = 100)
    private String department;

    @Column(name = "criticality", length = 20)
    private String criticality; // CRITICAL, HIGH, MEDIUM, LOW

    @Column(name = "min_readiness_score")
    private Integer minReadinessScore;

    // --- Getters & Setters ---

    public Integer getRoleId() { return roleId; }
    public void setRoleId(Integer roleId) { this.roleId = roleId; }

    public String getRoleName() { return roleName; }
    public void setRoleName(String roleName) { this.roleName = roleName; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getCriticality() { return criticality; }
    public void setCriticality(String criticality) { this.criticality = criticality; }

    public Integer getMinReadinessScore() { return minReadinessScore; }
    public void setMinReadinessScore(Integer minReadinessScore) { this.minReadinessScore = minReadinessScore; }
}
