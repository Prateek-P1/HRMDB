package com.hrms.db.entities;

import jakarta.persistence.*;

/**
 * Currency definitions — ISO 4217.
 * Required by: Multi-Country Support, Payroll.
 */
@Entity
@Table(name = "currencies")
public class Currency {

    @Id
    @Column(name = "currency_code", length = 3)
    private String currencyCode;

    @Column(name = "currency_symbol", length = 5)
    private String currencySymbol;

    @Column(name = "currency_decimal_precision")
    private Integer currencyDecimalPrecision = 2;

    // --- Getters & Setters ---

    public String getCurrencyCode() { return currencyCode; }
    public void setCurrencyCode(String currencyCode) { this.currencyCode = currencyCode; }

    public String getCurrencySymbol() { return currencySymbol; }
    public void setCurrencySymbol(String currencySymbol) { this.currencySymbol = currencySymbol; }

    public Integer getCurrencyDecimalPrecision() { return currencyDecimalPrecision; }
    public void setCurrencyDecimalPrecision(Integer v) { this.currencyDecimalPrecision = v; }
}
