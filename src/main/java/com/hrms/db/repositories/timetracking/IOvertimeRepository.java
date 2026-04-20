package com.hrms.db.repositories.timetracking;

import com.hrms.db.entities.OvertimeRecord;

import java.util.List;

public interface IOvertimeRepository {
    void save(OvertimeRecord overtime);

    OvertimeRecord findById(Long overtimeId);

    List<OvertimeRecord> findAll();

    void delete(Long overtimeId);
}
