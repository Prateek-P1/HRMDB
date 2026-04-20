package com.hrms.db.repositories.multicountry;

public class WorkingHoursDTO {
    private String countryCode;
    private Integer standardHoursPerWeek;
    private String workDayStart; // e.g., 09:00
    private String workDayEnd;   // e.g., 18:00

    public WorkingHoursDTO() {
    }

    public String getCountryCode() { return countryCode; }
    public void setCountryCode(String countryCode) { this.countryCode = countryCode; }

    public Integer getStandardHoursPerWeek() { return standardHoursPerWeek; }
    public void setStandardHoursPerWeek(Integer standardHoursPerWeek) { this.standardHoursPerWeek = standardHoursPerWeek; }

    public String getWorkDayStart() { return workDayStart; }
    public void setWorkDayStart(String workDayStart) { this.workDayStart = workDayStart; }

    public String getWorkDayEnd() { return workDayEnd; }
    public void setWorkDayEnd(String workDayEnd) { this.workDayEnd = workDayEnd; }
}
