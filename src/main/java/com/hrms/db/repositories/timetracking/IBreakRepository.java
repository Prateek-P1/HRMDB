package com.hrms.db.repositories.timetracking;

import com.hrms.db.entities.BreakRecord;

import java.util.List;

public interface IBreakRepository {
    void save(BreakRecord breakRecord);

    BreakRecord findById(Long breakId);

    List<BreakRecord> findAll();

    void delete(Long breakId);
}
