package com.hrms.db.repositories.multicountry;

public class LocaleDTO {
    private String localeCode;
    private String languageName;
    private String dateFormatPattern;
    private String numberFormatPattern;
    private String currencyFormatPattern;
    private String firstDayOfWeek;

    public LocaleDTO() {
    }

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
