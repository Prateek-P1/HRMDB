package com.hrms.db.repositories.multicountry;

import java.time.LocalDate;

public class TaxRegimeDTO {
    private String countryCode;
    private String taxRegimeName;
    private String fiscalYear;
    private Double taxRatePct;
    private String statutoryDeductionName;
    private Double employeeRate;
    private Double employerRate;
    private LocalDate filingDeadlineDate;

    public TaxRegimeDTO() {
    }

    public String getCountryCode() { return countryCode; }
    public void setCountryCode(String countryCode) { this.countryCode = countryCode; }

    public String getTaxRegimeName() { return taxRegimeName; }
    public void setTaxRegimeName(String taxRegimeName) { this.taxRegimeName = taxRegimeName; }

    public String getFiscalYear() { return fiscalYear; }
    public void setFiscalYear(String fiscalYear) { this.fiscalYear = fiscalYear; }

    public Double getTaxRatePct() { return taxRatePct; }
    public void setTaxRatePct(Double taxRatePct) { this.taxRatePct = taxRatePct; }

    public String getStatutoryDeductionName() { return statutoryDeductionName; }
    public void setStatutoryDeductionName(String statutoryDeductionName) { this.statutoryDeductionName = statutoryDeductionName; }

    public Double getEmployeeRate() { return employeeRate; }
    public void setEmployeeRate(Double employeeRate) { this.employeeRate = employeeRate; }

    public Double getEmployerRate() { return employerRate; }
    public void setEmployerRate(Double employerRate) { this.employerRate = employerRate; }

    public LocalDate getFilingDeadlineDate() { return filingDeadlineDate; }
    public void setFilingDeadlineDate(LocalDate filingDeadlineDate) { this.filingDeadlineDate = filingDeadlineDate; }
}
