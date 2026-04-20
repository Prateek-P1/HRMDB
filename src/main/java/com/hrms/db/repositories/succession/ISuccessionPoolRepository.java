package com.hrms.db.repositories.succession;

import com.hrms.db.entities.SuccessionPoolEntry;

import java.util.List;
import java.util.Optional;

public interface ISuccessionPoolRepository {
    SuccessionPoolEntry save(SuccessionPoolEntry entry);
    Optional<SuccessionPoolEntry> findByEmployeeId(String employeeId);
    List<SuccessionPoolEntry> findAllEligible();
    List<SuccessionPoolEntry> findAll();
}
