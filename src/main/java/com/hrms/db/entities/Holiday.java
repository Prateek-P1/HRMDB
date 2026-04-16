package com.hrms.db.entities;

import jakarta.persistence.*;
import java.time.LocalDate;

/**
 * Company/office holidays — used by Leave Management, Multi-Country.
 */
@Entity
@Table(name = "holidays")
public class Holiday {

    @Id
    @Column(name = "holiday_id", length = 36)
    private String holidayId;

    @Column(name = "holiday_name", nullable = false, length = 100)
    private String holidayName;

    @Column(name = "holiday_date", nullable = false)
    private LocalDate holidayDate;

    @Column(name = "applicable_location", length = 100)
    private String applicableLocation;

    @Column(name = "holiday_year")
    private Integer holidayYear;

    @Column(name = "country_code", length = 5)
    private String countryCode;

    // --- Getters & Setters ---

    public String getHolidayId() { return holidayId; }
    public void setHolidayId(String holidayId) { this.holidayId = holidayId; }

    public String getHolidayName() { return holidayName; }
    public void setHolidayName(String holidayName) { this.holidayName = holidayName; }

    public LocalDate getHolidayDate() { return holidayDate; }
    public void setHolidayDate(LocalDate holidayDate) { this.holidayDate = holidayDate; }

    public String getApplicableLocation() { return applicableLocation; }
    public void setApplicableLocation(String applicableLocation) { this.applicableLocation = applicableLocation; }

    public Integer getHolidayYear() { return holidayYear; }
    public void setHolidayYear(Integer holidayYear) { this.holidayYear = holidayYear; }

    public String getCountryCode() { return countryCode; }
    public void setCountryCode(String countryCode) { this.countryCode = countryCode; }
}
