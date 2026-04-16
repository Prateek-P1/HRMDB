package com.hrms.db.entities;

import jakarta.persistence.*;

/**
 * Country configuration — ISO codes, timezone, region.
 * Required by: Multi-Country Support.
 */
@Entity
@Table(name = "countries")
public class Country {

    @Id
    @Column(name = "country_code", length = 2)
    private String countryCode;

    @Column(name = "country_code_alpha3", length = 3)
    private String countryCodeAlpha3;

    @Column(name = "country_name", nullable = false, length = 100)
    private String countryName;

    @Column(name = "default_currency_code", length = 3)
    private String defaultCurrencyCode;

    @Column(name = "default_locale", length = 20)
    private String defaultLocale;

    @Column(name = "timezone", length = 50)
    private String timezone;

    @Column(name = "utc_offset", length = 10)
    private String utcOffset;

    @Column(name = "region_code", length = 10)
    private String regionCode; // APAC, EMEA, NAM, LATAM

    @Column(name = "region_name", length = 50)
    private String regionName;

    @Column(name = "is_active")
    private Boolean isActive = true;

    // --- Getters & Setters ---

    public String getCountryCode() { return countryCode; }
    public void setCountryCode(String countryCode) { this.countryCode = countryCode; }

    public String getCountryCodeAlpha3() { return countryCodeAlpha3; }
    public void setCountryCodeAlpha3(String countryCodeAlpha3) { this.countryCodeAlpha3 = countryCodeAlpha3; }

    public String getCountryName() { return countryName; }
    public void setCountryName(String countryName) { this.countryName = countryName; }

    public String getDefaultCurrencyCode() { return defaultCurrencyCode; }
    public void setDefaultCurrencyCode(String defaultCurrencyCode) { this.defaultCurrencyCode = defaultCurrencyCode; }

    public String getDefaultLocale() { return defaultLocale; }
    public void setDefaultLocale(String defaultLocale) { this.defaultLocale = defaultLocale; }

    public String getTimezone() { return timezone; }
    public void setTimezone(String timezone) { this.timezone = timezone; }

    public String getUtcOffset() { return utcOffset; }
    public void setUtcOffset(String utcOffset) { this.utcOffset = utcOffset; }

    public String getRegionCode() { return regionCode; }
    public void setRegionCode(String regionCode) { this.regionCode = regionCode; }

    public String getRegionName() { return regionName; }
    public void setRegionName(String regionName) { this.regionName = regionName; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
}
