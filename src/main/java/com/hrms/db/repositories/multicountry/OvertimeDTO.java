package com.hrms.db.repositories.multicountry;

public class OvertimeDTO {
    private String countryCode;
    private Double rateMultiplier;
    private Integer maxHoursPerWeek;

    public OvertimeDTO() {
    }

    public String getCountryCode() { return countryCode; }
    public void setCountryCode(String countryCode) { this.countryCode = countryCode; }

    public Double getRateMultiplier() { return rateMultiplier; }
    public void setRateMultiplier(Double rateMultiplier) { this.rateMultiplier = rateMultiplier; }

    public Integer getMaxHoursPerWeek() { return maxHoursPerWeek; }
    public void setMaxHoursPerWeek(Integer maxHoursPerWeek) { this.maxHoursPerWeek = maxHoursPerWeek; }
}
