package com.hrms.db.repositories.succession;

import com.hrms.db.entities.ExternalHireRequest;

import java.util.List;
import java.util.Optional;

public interface IExternalHireRequestRepository {
    ExternalHireRequest save(ExternalHireRequest request);
    Optional<ExternalHireRequest> findById(Long extHireRequestId);
    List<ExternalHireRequest> findByStatus(String requestStatus);
    void updateStatus(Long extHireRequestId, String requestStatus);
    void incrementRetryCount(Long extHireRequestId);
}
