package com.hrms.db.repositories.multicountry;

public class BenefitPlanDTO {
    private String planId;
    private String planName;
    private String planType;
    private String coverageDetails;

    public BenefitPlanDTO() {
    }

    public String getPlanId() { return planId; }
    public void setPlanId(String planId) { this.planId = planId; }

    public String getPlanName() { return planName; }
    public void setPlanName(String planName) { this.planName = planName; }

    public String getPlanType() { return planType; }
    public void setPlanType(String planType) { this.planType = planType; }

    public String getCoverageDetails() { return coverageDetails; }
    public void setCoverageDetails(String coverageDetails) { this.coverageDetails = coverageDetails; }
}
