package com.hrms.db.repositories.timetracking;

import com.hrms.db.entities.HrReport;

import java.util.List;

public interface IReportRepository {
    void save(HrReport report);

    HrReport findById(String reportId);

    List<HrReport> findAll();

    void delete(String reportId);
}
