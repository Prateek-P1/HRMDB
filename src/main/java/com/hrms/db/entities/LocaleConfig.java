package com.hrms.db.entities;

import jakarta.persistence.*;

/**
 * Locale/i18n configuration — date/number formats, translations.
 * Required by: Multi-Country Support.
 */
@Entity
@Table(name = "locale_configs")
public class LocaleConfig {

    @Id
    @Column(name = "locale_code", length = 20)
    private String localeCode; // BCP 47

    @Column(name = "language_name", length = 50)
    private String languageName;

    @Column(name = "date_format_pattern", length = 30)
    private String dateFormatPattern;

    @Column(name = "number_format_pattern", length = 30)
    private String numberFormatPattern;

    @Column(name = "currency_format_pattern", length = 30)
    private String currencyFormatPattern;

    @Column(name = "first_day_of_week", length = 3)
    private String firstDayOfWeek; // SUN, MON

    // --- Getters & Setters ---

    public String getLocaleCode() { return localeCode; }
    public void setLocaleCode(String localeCode) { this.localeCode = localeCode; }

    public String getLanguageName() { return languageName; }
    public void setLanguageName(String languageName) { this.languageName = languageName; }

    public String getDateFormatPattern() { return dateFormatPattern; }
    public void setDateFormatPattern(String dateFormatPattern) { this.dateFormatPattern = dateFormatPattern; }

    public String getNumberFormatPattern() { return numberFormatPattern; }
    public void setNumberFormatPattern(String numberFormatPattern) { this.numberFormatPattern = numberFormatPattern; }

    public String getCurrencyFormatPattern() { return currencyFormatPattern; }
    public void setCurrencyFormatPattern(String currencyFormatPattern) { this.currencyFormatPattern = currencyFormatPattern; }

    public String getFirstDayOfWeek() { return firstDayOfWeek; }
    public void setFirstDayOfWeek(String firstDayOfWeek) { this.firstDayOfWeek = firstDayOfWeek; }
}
