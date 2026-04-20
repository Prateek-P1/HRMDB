package com.hrms.db.repositories.multicountry;

public class AnalyticsDTO {
    private String countryCode;
    private Integer totalEmployees;
    private Integer activeEmployees;
    private Integer countriesRepresented;
    private Double averageBasicPay;

    public AnalyticsDTO() {
    }

    public String getCountryCode() { return countryCode; }
    public void setCountryCode(String countryCode) { this.countryCode = countryCode; }

    public Integer getTotalEmployees() { return totalEmployees; }
    public void setTotalEmployees(Integer totalEmployees) { this.totalEmployees = totalEmployees; }

    public Integer getActiveEmployees() { return activeEmployees; }
    public void setActiveEmployees(Integer activeEmployees) { this.activeEmployees = activeEmployees; }

    public Integer getCountriesRepresented() { return countriesRepresented; }
    public void setCountriesRepresented(Integer countriesRepresented) { this.countriesRepresented = countriesRepresented; }

    public Double getAverageBasicPay() { return averageBasicPay; }
    public void setAverageBasicPay(Double averageBasicPay) { this.averageBasicPay = averageBasicPay; }
}
