package com.hrms.db.entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * RiskLog — records risk evaluations for critical roles.
 *
 * This entity is introduced to support the Succession Planning repository interfaces.
 */
@Entity
@Table(name = "risk_logs")
public class RiskLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "risk_log_id")
    private Long riskLogId;

    @Column(name = "role_id", nullable = false)
    private Integer roleIdFk;

    @Column(name = "risk_level", length = 20)
    private String riskLevel; // HIGH, MEDIUM, LOW, etc.

    @Column(name = "details", columnDefinition = "TEXT")
    private String details;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

    public Long getRiskLogId() { return riskLogId; }
    public void setRiskLogId(Long riskLogId) { this.riskLogId = riskLogId; }

    public Integer getRoleIdFk() { return roleIdFk; }
    public void setRoleIdFk(Integer roleIdFk) { this.roleIdFk = roleIdFk; }

    public String getRiskLevel() { return riskLevel; }
    public void setRiskLevel(String riskLevel) { this.riskLevel = riskLevel; }

    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
