package com.hrms.db.repositories.succession;

import com.hrms.db.entities.ReadinessScore;

import java.util.List;
import java.util.Optional;

public interface IReadinessScoreRepository {
    ReadinessScore save(ReadinessScore score);
    Optional<ReadinessScore> findByEmployeeAndRole(String empIdFk, Integer roleIdFk);
    List<ReadinessScore> findByEmployeeIdFk(String empIdFk);
    List<ReadinessScore> findByRoleIdFk(Integer roleIdFk);
    void deleteByEmployeeAndRole(String empIdFk, Integer roleIdFk);
}
