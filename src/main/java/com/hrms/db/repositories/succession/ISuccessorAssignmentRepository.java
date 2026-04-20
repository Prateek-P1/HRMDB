package com.hrms.db.repositories.succession;

import com.hrms.db.entities.SuccessorAssignment;

import java.util.List;

public interface ISuccessorAssignmentRepository {
    SuccessorAssignment save(SuccessorAssignment assignment);
    List<SuccessorAssignment> findRankedByRole(Integer roleIdFk);
    List<SuccessorAssignment> findByEmployeeIdFk(String empIdFk);
    boolean existsConflictingAssignment(String empIdFk, Integer roleIdFk);
    void deleteById(Long assignmentId);
    List<SuccessorAssignment> findApprovedByRole(Integer roleIdFk);
}
