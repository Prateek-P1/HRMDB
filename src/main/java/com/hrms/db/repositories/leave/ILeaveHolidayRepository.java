package com.hrms.db.repositories.leave;

import com.hrms.db.repositories.leave.LeaveDTOs.HolidayDTO;
import java.util.List;

/**
 * ILeaveHolidayRepository — provided by Leave Management team.
 * Holiday calendar queries for leave day calculations.
 */
public interface ILeaveHolidayRepository {

    List<HolidayDTO> getHolidaysByYearAndLocation(int year, String location);

    boolean isHoliday(String date, String location);
}
