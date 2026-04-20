package com.hrms.db.repositories.benefits;

import com.hrms.db.entities.BenefitPlan;

import java.util.List;

/** DAO contract for {@link BenefitPlan} persistence used by Benefits Administration. */
public interface BenefitPlanDAO {

    void save(BenefitPlan plan);

    BenefitPlan findById(Integer planId);

    List<BenefitPlan> findAll();

    List<BenefitPlan> findByType(String planType);

    void update(BenefitPlan plan);

    void delete(Integer planId);

    boolean existsById(Integer planId);
}