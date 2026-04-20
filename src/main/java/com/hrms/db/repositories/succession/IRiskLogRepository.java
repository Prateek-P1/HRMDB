package com.hrms.db.repositories.succession;

import com.hrms.db.entities.RiskLog;

import java.util.List;
import java.util.Optional;

public interface IRiskLogRepository {
    RiskLog save(RiskLog riskLog);
    List<RiskLog> findByRoleIdFk(Integer roleIdFk);
    Optional<RiskLog> findLatestByRole(Integer roleIdFk);
    List<RiskLog> findLatestRiskPerRole();
}
