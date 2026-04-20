package com.hrms.db.repositories.timetracking;

import com.hrms.db.entities.Attendance;

import java.util.List;

public interface IAttendanceRepository {
    void save(Attendance attendance);

    Attendance findById(String recordId);

    List<Attendance> findAll();

    void delete(String recordId);
}
