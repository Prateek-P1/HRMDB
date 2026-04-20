package com.hrms.db.repositories.benefits;

import com.hrms.db.entities.BenefitEnrollment;

import java.util.List;

/** DAO contract for {@link BenefitEnrollment} persistence used by Benefits Administration. */
public interface EnrollmentDAO {

    void save(BenefitEnrollment enrollment);

    BenefitEnrollment findById(Long enrollmentId);

    List<BenefitEnrollment> findByEmployeeId(String employeeId);

    List<BenefitEnrollment> findAll();

    void update(BenefitEnrollment enrollment);

    boolean existsByEmployeeAndPlan(String employeeId, Integer planId);
}
