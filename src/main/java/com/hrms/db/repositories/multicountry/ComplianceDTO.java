package com.hrms.db.repositories.multicountry;

public class ComplianceDTO {
    private String countryCode;
    private Integer complianceScore;
    private String violationSeverity;
    private Integer activePolicyCount;

    public ComplianceDTO() {
    }

    public String getCountryCode() { return countryCode; }
    public void setCountryCode(String countryCode) { this.countryCode = countryCode; }

    public Integer getComplianceScore() { return complianceScore; }
    public void setComplianceScore(Integer complianceScore) { this.complianceScore = complianceScore; }

    public String getViolationSeverity() { return violationSeverity; }
    public void setViolationSeverity(String violationSeverity) { this.violationSeverity = violationSeverity; }

    public Integer getActivePolicyCount() { return activePolicyCount; }
    public void setActivePolicyCount(Integer activePolicyCount) { this.activePolicyCount = activePolicyCount; }
}
