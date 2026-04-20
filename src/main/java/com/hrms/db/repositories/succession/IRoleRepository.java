package com.hrms.db.repositories.succession;

import com.hrms.db.entities.CriticalRole;

import java.util.List;
import java.util.Optional;

public interface IRoleRepository {
    CriticalRole save(CriticalRole role);
    Optional<CriticalRole> findById(Integer roleId);
    List<CriticalRole> findAll();
    List<CriticalRole> findByCriticality(String criticality);
    boolean existsByRoleName(String roleName);
    void deleteById(Integer roleId);
}
