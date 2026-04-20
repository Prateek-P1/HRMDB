package com.hrms.db.repositories.benefits;

import com.hrms.db.entities.BenefitPolicy;

import java.util.List;

/** DAO contract for {@link BenefitPolicy} persistence used by Benefits Administration. */
public interface BenefitPolicyDAO {

    void save(BenefitPolicy policy);

    BenefitPolicy findById(Integer policyId);

    List<BenefitPolicy> findAll();

    List<BenefitPolicy> findAllActive();

    void update(BenefitPolicy policy);

    void delete(Integer policyId);

    boolean existsById(Integer policyId);
}
