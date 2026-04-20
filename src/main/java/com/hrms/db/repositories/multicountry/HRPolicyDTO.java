package com.hrms.db.repositories.multicountry;

import java.time.LocalDateTime;

public class HRPolicyDTO {
    private String countryCode;
    private String policyName;
    private String policyText;
    private LocalDateTime lastUpdated;

    public HRPolicyDTO() {
    }

    public String getCountryCode() { return countryCode; }
    public void setCountryCode(String countryCode) { this.countryCode = countryCode; }

    public String getPolicyName() { return policyName; }
    public void setPolicyName(String policyName) { this.policyName = policyName; }

    public String getPolicyText() { return policyText; }
    public void setPolicyText(String policyText) { this.policyText = policyText; }

    public LocalDateTime getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; }
}
