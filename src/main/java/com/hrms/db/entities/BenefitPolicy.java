package com.hrms.db.entities;

import jakarta.persistence.*;
import java.time.LocalDate;

/**
 * Benefit policies — eligibility rules and restrictions.
 * Required by: Benefits Administration.
 */
@Entity
@Table(name = "benefit_policies")
public class BenefitPolicy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "policy_id")
    private Integer policyId;

    @Column(name = "policy_name", length = 200)
    private String policyName;

    @Column(name = "eligibility_rules", columnDefinition = "TEXT")
    private String eligibilityRules;

    @Column(name = "salary_band_criteria", length = 100)
    private String salaryBandCriteria;

    @Column(name = "employment_type_restrictions", length = 100)
    private String employmentTypeRestrictions;

    @Column(name = "waiting_period_rules", length = 200)
    private String waitingPeriodRules;

    @Column(name = "maximum_coverage_rules", length = 200)
    private String maximumCoverageRules;

    @Column(name = "policy_effective_date")
    private LocalDate policyEffectiveDate;

    @Column(name = "policy_last_updated")
    private LocalDate policyLastUpdated;

    @Column(name = "is_active")
    private Boolean isActive = true;

    // --- Getters & Setters ---

    public Integer getPolicyId() { return policyId; }
    public void setPolicyId(Integer policyId) { this.policyId = policyId; }

    public String getPolicyName() { return policyName; }
    public void setPolicyName(String policyName) { this.policyName = policyName; }

    public String getEligibilityRules() { return eligibilityRules; }
    public void setEligibilityRules(String eligibilityRules) { this.eligibilityRules = eligibilityRules; }

    public String getSalaryBandCriteria() { return salaryBandCriteria; }
    public void setSalaryBandCriteria(String salaryBandCriteria) { this.salaryBandCriteria = salaryBandCriteria; }

    public String getEmploymentTypeRestrictions() { return employmentTypeRestrictions; }
    public void setEmploymentTypeRestrictions(String v) { this.employmentTypeRestrictions = v; }

    public String getWaitingPeriodRules() { return waitingPeriodRules; }
    public void setWaitingPeriodRules(String waitingPeriodRules) { this.waitingPeriodRules = waitingPeriodRules; }

    public String getMaximumCoverageRules() { return maximumCoverageRules; }
    public void setMaximumCoverageRules(String maximumCoverageRules) { this.maximumCoverageRules = maximumCoverageRules; }

    public LocalDate getPolicyEffectiveDate() { return policyEffectiveDate; }
    public void setPolicyEffectiveDate(LocalDate policyEffectiveDate) { this.policyEffectiveDate = policyEffectiveDate; }

    public LocalDate getPolicyLastUpdated() { return policyLastUpdated; }
    public void setPolicyLastUpdated(LocalDate policyLastUpdated) { this.policyLastUpdated = policyLastUpdated; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
}
