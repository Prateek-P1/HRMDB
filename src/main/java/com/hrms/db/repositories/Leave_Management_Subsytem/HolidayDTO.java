package com.hrms.db.repositories.Leave_Management_Subsytem;

/**
 * DTO: HolidayDTO
 *
 * Team-provided data contract for Holiday calendar.
 */
public class HolidayDTO {
    public String holidayId;
    public String holidayName;
    public String date;     // ISO: yyyy-MM-dd
    public String location;
    public int year;
}
