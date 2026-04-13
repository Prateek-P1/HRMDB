package com.hrms.db.entities;

import jakarta.persistence.*;
import java.time.LocalDate;

/**
 * Tax regimes — income slabs, statutory deductions per country.
 * Required by: Multi-Country Support, Payroll.
 */
@Entity
@Table(name = "tax_regimes")
public class TaxRegime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "regime_id")
    private Long regimeId;

    @Column(name = "tax_regime_name", nullable = false, length = 100)
    private String taxRegimeName;

    @Column(name = "country_code", length = 2)
    private String countryCode;

    @Column(name = "fiscal_year", length = 10)
    private String fiscalYear;

    @Column(name = "income_slab_min")
    private Double incomeSlabMin;

    @Column(name = "income_slab_max")
    private Double incomeSlabMax;

    @Column(name = "tax_rate_pct")
    private Double taxRatePct;

    @Column(name = "statutory_deduction_name", length = 50)
    private String statutoryDeductionName; // PF, ESIC, NI, CPF

    @Column(name = "deduction_calc_basis", length = 10)
    private String deductionCalcBasis; // BASIC, GROSS, FIXED

    @Column(name = "deduction_employee_rate")
    private Double deductionEmployeeRate;

    @Column(name = "deduction_employer_rate")
    private Double deductionEmployerRate;

    @Column(name = "deduction_is_active")
    private Boolean deductionIsActive = true;

    @Column(name = "filing_deadline_date")
    private LocalDate filingDeadlineDate;

    // --- Getters & Setters ---

    public Long getRegimeId() { return regimeId; }
    public void setRegimeId(Long regimeId) { this.regimeId = regimeId; }

    public String getTaxRegimeName() { return taxRegimeName; }
    public void setTaxRegimeName(String taxRegimeName) { this.taxRegimeName = taxRegimeName; }

    public String getCountryCode() { return countryCode; }
    public void setCountryCode(String countryCode) { this.countryCode = countryCode; }

    public String getFiscalYear() { return fiscalYear; }
    public void setFiscalYear(String fiscalYear) { this.fiscalYear = fiscalYear; }

    public Double getIncomeSlabMin() { return incomeSlabMin; }
    public void setIncomeSlabMin(Double incomeSlabMin) { this.incomeSlabMin = incomeSlabMin; }

    public Double getIncomeSlabMax() { return incomeSlabMax; }
    public void setIncomeSlabMax(Double incomeSlabMax) { this.incomeSlabMax = incomeSlabMax; }

    public Double getTaxRatePct() { return taxRatePct; }
    public void setTaxRatePct(Double taxRatePct) { this.taxRatePct = taxRatePct; }

    public String getStatutoryDeductionName() { return statutoryDeductionName; }
    public void setStatutoryDeductionName(String v) { this.statutoryDeductionName = v; }

    public String getDeductionCalcBasis() { return deductionCalcBasis; }
    public void setDeductionCalcBasis(String deductionCalcBasis) { this.deductionCalcBasis = deductionCalcBasis; }

    public Double getDeductionEmployeeRate() { return deductionEmployeeRate; }
    public void setDeductionEmployeeRate(Double v) { this.deductionEmployeeRate = v; }

    public Double getDeductionEmployerRate() { return deductionEmployerRate; }
    public void setDeductionEmployerRate(Double v) { this.deductionEmployerRate = v; }

    public Boolean getDeductionIsActive() { return deductionIsActive; }
    public void setDeductionIsActive(Boolean deductionIsActive) { this.deductionIsActive = deductionIsActive; }

    public LocalDate getFilingDeadlineDate() { return filingDeadlineDate; }
    public void setFilingDeadlineDate(LocalDate filingDeadlineDate) { this.filingDeadlineDate = filingDeadlineDate; }
}
