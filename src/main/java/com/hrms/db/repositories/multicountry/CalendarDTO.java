package com.hrms.db.repositories.multicountry;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CalendarDTO {

    public static class Holiday {
        private String name;
        private LocalDate date;
        private Boolean mandatory;

        public Holiday() {
        }

        public Holiday(String name, LocalDate date, Boolean mandatory) {
            this.name = name;
            this.date = date;
            this.mandatory = mandatory;
        }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public LocalDate getDate() { return date; }
        public void setDate(LocalDate date) { this.date = date; }

        public Boolean getMandatory() { return mandatory; }
        public void setMandatory(Boolean mandatory) { this.mandatory = mandatory; }
    }

    private String countryCode;
    private List<Holiday> holidays = new ArrayList<>();

    public CalendarDTO() {
    }

    public String getCountryCode() { return countryCode; }
    public void setCountryCode(String countryCode) { this.countryCode = countryCode; }

    public List<Holiday> getHolidays() { return holidays; }
    public void setHolidays(List<Holiday> holidays) { this.holidays = holidays; }
}
