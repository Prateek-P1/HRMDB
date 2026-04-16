package com.hrms.db.entities;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Exchange rates — currency conversion factors.
 * Required by: Multi-Country Support, Payroll.
 */
@Entity
@Table(name = "exchange_rates")
public class ExchangeRate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rate_id")
    private Long rateId;

    @Column(name = "base_currency_code", nullable = false, length = 3)
    private String baseCurrencyCode;

    @Column(name = "target_currency_code", nullable = false, length = 3)
    private String targetCurrencyCode;

    @Column(name = "exchange_rate")
    private Double exchangeRate;

    @Column(name = "rate_effective_date")
    private LocalDate rateEffectiveDate;

    @Column(name = "rate_locked_at")
    private LocalDateTime rateLockedAt;

    @Column(name = "fx_rate_source", length = 50)
    private String fxRateSource; // ECB, OpenExchangeRates

    @Column(name = "manual_override_flag")
    private Boolean manualOverrideFlag = false;

    @Column(name = "rate_fluctuation_threshold_pct")
    private Double rateFluctuationThresholdPct;

    // --- Getters & Setters ---

    public Long getRateId() { return rateId; }
    public void setRateId(Long rateId) { this.rateId = rateId; }

    public String getBaseCurrencyCode() { return baseCurrencyCode; }
    public void setBaseCurrencyCode(String baseCurrencyCode) { this.baseCurrencyCode = baseCurrencyCode; }

    public String getTargetCurrencyCode() { return targetCurrencyCode; }
    public void setTargetCurrencyCode(String targetCurrencyCode) { this.targetCurrencyCode = targetCurrencyCode; }

    public Double getExchangeRate() { return exchangeRate; }
    public void setExchangeRate(Double exchangeRate) { this.exchangeRate = exchangeRate; }

    public LocalDate getRateEffectiveDate() { return rateEffectiveDate; }
    public void setRateEffectiveDate(LocalDate rateEffectiveDate) { this.rateEffectiveDate = rateEffectiveDate; }

    public LocalDateTime getRateLockedAt() { return rateLockedAt; }
    public void setRateLockedAt(LocalDateTime rateLockedAt) { this.rateLockedAt = rateLockedAt; }

    public String getFxRateSource() { return fxRateSource; }
    public void setFxRateSource(String fxRateSource) { this.fxRateSource = fxRateSource; }

    public Boolean getManualOverrideFlag() { return manualOverrideFlag; }
    public void setManualOverrideFlag(Boolean manualOverrideFlag) { this.manualOverrideFlag = manualOverrideFlag; }

    public Double getRateFluctuationThresholdPct() { return rateFluctuationThresholdPct; }
    public void setRateFluctuationThresholdPct(Double v) { this.rateFluctuationThresholdPct = v; }
}
