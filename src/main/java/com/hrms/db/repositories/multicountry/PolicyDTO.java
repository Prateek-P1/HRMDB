package com.hrms.db.repositories.multicountry;

public class PolicyDTO {
    private String policyId;
    private String policyName;
    private String policyCategory;
    private String status;
    private Integer complianceScore;
    private String violationSeverity;

    public PolicyDTO() {
    }

    public String getPolicyId() { return policyId; }
    public void setPolicyId(String policyId) { this.policyId = policyId; }

    public String getPolicyName() { return policyName; }
    public void setPolicyName(String policyName) { this.policyName = policyName; }

    public String getPolicyCategory() { return policyCategory; }
    public void setPolicyCategory(String policyCategory) { this.policyCategory = policyCategory; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Integer getComplianceScore() { return complianceScore; }
    public void setComplianceScore(Integer complianceScore) { this.complianceScore = complianceScore; }

    public String getViolationSeverity() { return violationSeverity; }
    public void setViolationSeverity(String violationSeverity) { this.violationSeverity = violationSeverity; }
}
