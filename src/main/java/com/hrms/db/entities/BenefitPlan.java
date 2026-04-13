package com.hrms.db.entities;

import jakarta.persistence.*;

/**
 * Benefit plans — health insurance, retirement, etc.
 * Required by: Benefits Administration.
 */
@Entity
@Table(name = "benefit_plans")
public class BenefitPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "plan_id")
    private Integer planId;

    @Column(name = "plan_name", nullable = false, length = 200)
    private String planName;

    @Column(name = "plan_type", length = 50)
    private String planType; // HEALTH, DENTAL, VISION, RETIREMENT, LIFE

    @Column(name = "coverage_details", columnDefinition = "TEXT")
    private String coverageDetails;

    @Column(name = "cost")
    private Double cost;

    @Column(name = "provider_name", length = 200)
    private String providerName;

    @Column(name = "coverage_limit")
    private Double coverageLimit;

    @Column(name = "plan_duration", length = 30)
    private String planDuration;

    @Column(name = "plan_eligibility_criteria", columnDefinition = "TEXT")
    private String planEligibilityCriteria;

    // --- Getters & Setters ---

    public Integer getPlanId() { return planId; }
    public void setPlanId(Integer planId) { this.planId = planId; }

    public String getPlanName() { return planName; }
    public void setPlanName(String planName) { this.planName = planName; }

    public String getPlanType() { return planType; }
    public void setPlanType(String planType) { this.planType = planType; }

    public String getCoverageDetails() { return coverageDetails; }
    public void setCoverageDetails(String coverageDetails) { this.coverageDetails = coverageDetails; }

    public Double getCost() { return cost; }
    public void setCost(Double cost) { this.cost = cost; }

    public String getProviderName() { return providerName; }
    public void setProviderName(String providerName) { this.providerName = providerName; }

    public Double getCoverageLimit() { return coverageLimit; }
    public void setCoverageLimit(Double coverageLimit) { this.coverageLimit = coverageLimit; }

    public String getPlanDuration() { return planDuration; }
    public void setPlanDuration(String planDuration) { this.planDuration = planDuration; }

    public String getPlanEligibilityCriteria() { return planEligibilityCriteria; }
    public void setPlanEligibilityCriteria(String v) { this.planEligibilityCriteria = v; }
}
