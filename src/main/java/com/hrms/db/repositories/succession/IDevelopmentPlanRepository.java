package com.hrms.db.repositories.succession;

import com.hrms.db.entities.DevelopmentPlan;

import java.util.List;
import java.util.Optional;

public interface IDevelopmentPlanRepository {
    DevelopmentPlan save(DevelopmentPlan plan);
    Optional<DevelopmentPlan> findById(Long planId);
    List<DevelopmentPlan> findByAssignmentIdFk(Long assignmentIdFk);
    void deletePlan(Long planId);
    List<DevelopmentPlan> findAllActive();
}
