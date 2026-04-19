package com.hrms.db.repositories.multicountry;

public class LanguagePackDTO {
    private String localeCode;
    private String languageName;

    public LanguagePackDTO() {
    }

    public LanguagePackDTO(String localeCode, String languageName) {
        this.localeCode = localeCode;
        this.languageName = languageName;
    }

    public String getLocaleCode() { return localeCode; }
    public void setLocaleCode(String localeCode) { this.localeCode = localeCode; }

    public String getLanguageName() { return languageName; }
    public void setLanguageName(String languageName) { this.languageName = languageName; }
}
