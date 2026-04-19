package com.hrms.db.repositories.multicountry;

import java.time.LocalDate;

public class ExchangeRateDTO {
    private String fromCurrency;
    private String toCurrency;
    private Double rate;
    private LocalDate effectiveDate;
    private String source;

    public ExchangeRateDTO() {
    }

    public String getFromCurrency() { return fromCurrency; }
    public void setFromCurrency(String fromCurrency) { this.fromCurrency = fromCurrency; }

    public String getToCurrency() { return toCurrency; }
    public void setToCurrency(String toCurrency) { this.toCurrency = toCurrency; }

    public Double getRate() { return rate; }
    public void setRate(Double rate) { this.rate = rate; }

    public LocalDate getEffectiveDate() { return effectiveDate; }
    public void setEffectiveDate(LocalDate effectiveDate) { this.effectiveDate = effectiveDate; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
}
