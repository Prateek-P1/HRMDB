package com.hrms.db.repositories.timetracking;

import com.hrms.db.entities.WorkPolicy;

import java.util.List;

public interface IPolicyRepository {
    void save(WorkPolicy policy);

    WorkPolicy findById(Integer policyId);

    List<WorkPolicy> findAll();

    void delete(Integer policyId);
}
