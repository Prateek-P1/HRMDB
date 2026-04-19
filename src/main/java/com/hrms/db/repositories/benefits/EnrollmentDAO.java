package com.hrms.db.repositories.benefits;

import com.hrms.db.entities.BenefitEnrollment;

import java.util.List;

/** Benefit enrollment operations backed by {@link BenefitEnrollment}. */
public interface EnrollmentDAO {
    void save(BenefitEnrollment enrollment);
    BenefitEnrollment findById(Long enrollmentId);
    List<BenefitEnrollment> findByEmployeeId(String employeeId);
    List<BenefitEnrollment> findAll();
    void update(BenefitEnrollment enrollment);
    boolean existsByEmployeeAndPlan(String employeeId, Integer planId);
}
