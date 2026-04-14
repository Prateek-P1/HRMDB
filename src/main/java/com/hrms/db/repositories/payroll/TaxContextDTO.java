package com.hrms.db.repositories.payroll;

/**
 * TaxContextDTO — region and tax context used by the IncomeTaxTDS calculator.
 * Maps to fields from the 'employees' table (tax columns).
 * Part of PayrollDataPackage (Group 4).
 */
public class TaxContextDTO {
    public String countryCode;
    public String currencyCode;
    public String taxRegime;
    public String stateName;
    public String filingStatus;
    public String taxCode;
    public String nationalIDNumber;
}
