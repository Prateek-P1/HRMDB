package com.hrms.db.repositories.Leave_Management_Subsytem;

import java.util.List;

public interface IHolidayRepository {

    List<HolidayDTO> getHolidaysByYearAndLocation(int year, String location);

    boolean isHoliday(String date, String location);
}