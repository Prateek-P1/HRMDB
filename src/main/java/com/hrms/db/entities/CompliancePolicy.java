package com.hrms.db.entities;

import jakarta.persistence.*;
import java.time.LocalDate;

/**
 * Compliance policies per country.
 * Required by: Multi-Country Support.
 */
@Entity
@Table(name = "compliance_policies")
public class CompliancePolicy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "policy_id")
    private Integer policyId;

    @Column(name = "policy_name", length = 200)
    private String policyName;

    @Column(name = "policy_category", length = 50)
    private String policyCategory;

    @Column(name = "country_code", length = 2)
    private String countryCode;

    @Column(name = "compliance_score")
    private Integer complianceScore; // 0-100

    @Column(name = "violation_severity", length = 10)
    private String violationSeverity; // CRITICAL, HIGH, MEDIUM, LOW

    @Column(name = "violation_description", columnDefinition = "TEXT")
    private String violationDescription;

    @Column(name = "remediation_due_date")
    private LocalDate remediationDueDate;

    @Column(name = "is_active")
    private Boolean isActive = true;

    // --- Getters & Setters ---

    public Integer getPolicyId() { return policyId; }
    public void setPolicyId(Integer policyId) { this.policyId = policyId; }

    public String getPolicyName() { return policyName; }
    public void setPolicyName(String policyName) { this.policyName = policyName; }

    public String getPolicyCategory() { return policyCategory; }
    public void setPolicyCategory(String policyCategory) { this.policyCategory = policyCategory; }

    public String getCountryCode() { return countryCode; }
    public void setCountryCode(String countryCode) { this.countryCode = countryCode; }

    public Integer getComplianceScore() { return complianceScore; }
    public void setComplianceScore(Integer complianceScore) { this.complianceScore = complianceScore; }

    public String getViolationSeverity() { return violationSeverity; }
    public void setViolationSeverity(String violationSeverity) { this.violationSeverity = violationSeverity; }

    public String getViolationDescription() { return violationDescription; }
    public void setViolationDescription(String v) { this.violationDescription = v; }

    public LocalDate getRemediationDueDate() { return remediationDueDate; }
    public void setRemediationDueDate(LocalDate remediationDueDate) { this.remediationDueDate = remediationDueDate; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
}
