package com.hrms.db.repositories.multicountry;

import java.util.List;

/**
 * IMultiCountryRepository — Unified interface for Multi-Country Support subsystem
 *
 * Covers:
 * - Localization of HR policies
 * - Multi-currency payroll
 * - Language & regional settings
 * - Compliance & legal adherence
 * - Workforce analytics
 * - Benefits & compensation
 * - Cross-border mobility
 * - Centralized vs decentralized control
 *
 * DB Team: Implement MultiCountryRepositoryImpl
 */
public interface IMultiCountryRepository {

    // ─────────────────────────────────────────────
    // 1. LOCALIZATION OF HR POLICIES
    // ─────────────────────────────────────────────
    HRPolicyDTO getHRPolicy(String countryCode);
    void createHRPolicy(HRPolicyDTO policy);
    void updateHRPolicy(HRPolicyDTO policy);

    List<EmploymentTypeDTO> getEmploymentTypes(String countryCode);
    void configureWorkingHours(String countryCode, WorkingHoursDTO hours);
    void configureOvertimeRules(String countryCode, OvertimeDTO overtime);


    // ─────────────────────────────────────────────
    // 2. MULTI-CURRENCY & PAYROLL SUPPORT
    // ─────────────────────────────────────────────
    PayrollDTO getPayrollDetails(String employeeID, String countryCode);
    void processPayroll(String countryCode, String payrollPeriod);

    ExchangeRateDTO getExchangeRate(String fromCurrency, String toCurrency);
    void updateExchangeRate(ExchangeRateDTO rate);

    TaxRegimeDTO getTaxRules(String countryCode);
    void applyDeductions(String employeeID);

    ReportDTO generateGlobalPayrollReport();


    // ─────────────────────────────────────────────
    // 3. LANGUAGE & REGIONAL SETTINGS
    // ─────────────────────────────────────────────
    LocaleDTO getLocale(String localeCode);
    void updateLocale(LocaleDTO locale);

    List<LanguagePackDTO> getSupportedLanguages(String countryCode);
    void setUserLanguage(String employeeID, String languageCode);

    CalendarDTO getRegionalCalendar(String countryCode);
    void updateDateFormat(String countryCode, String format);


    // ─────────────────────────────────────────────
    // 4. COMPLIANCE & LEGAL ADHERENCE
    // ─────────────────────────────────────────────
    ComplianceDTO getComplianceStatus(String countryCode);
    void updateCompliance(String countryCode, ComplianceDTO compliance);

    List<PolicyDTO> getLegalPolicies(String countryCode);
    void updatePolicy(String policyID, String status);

    ReportDTO generateComplianceReport(String countryCode);
    void autoUpdateLegalChanges(String countryCode);


    // ─────────────────────────────────────────────
    // 5. GLOBAL WORKFORCE ANALYTICS
    // ─────────────────────────────────────────────
    AnalyticsDTO getGlobalWorkforceMetrics();
    AnalyticsDTO getCountrySpecificMetrics(String countryCode);

    void generateAnalyticsDashboard();
    void compareRegions(String countryCode1, String countryCode2);


    // ─────────────────────────────────────────────
    // 6. BENEFITS & COMPENSATION MANAGEMENT
    // ─────────────────────────────────────────────
    List<BenefitPlanDTO> getBenefitPlans(String countryCode);
    void assignBenefitPlan(String employeeID, String planID);

    CompensationDTO getCompensationDetails(String employeeID);
    void updateCompensation(CompensationDTO compensation);

    void ensurePayEquity();


    // ─────────────────────────────────────────────
    // 7. CROSS-BORDER MOBILITY & TRANSFERS
    // ─────────────────────────────────────────────
    void initiateTransfer(String employeeID, String fromCountry, String toCountry);
    MobilityDTO getMobilityDetails(String employeeID);

    void manageVisa(String employeeID, VisaDTO visa);
    void trackRelocationBenefits(String employeeID);

    void planSuccession(String employeeID);


    // ─────────────────────────────────────────────
    // 8. CENTRALIZED & DECENTRALIZED CONTROL
    // ─────────────────────────────────────────────
    void enableGlobalPolicy(String policyID);
    void overrideLocalPolicy(String countryCode, String policyID);

    boolean validateAccess(String userID, String countryCode);
    void assignRegionalAdmin(String employeeID, String countryCode);

}