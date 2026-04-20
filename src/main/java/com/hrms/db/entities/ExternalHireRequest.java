package com.hrms.db.entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * ExternalHireRequest — created when no eligible successor exists.
 *
 * This entity is introduced to support the Succession Planning repository interfaces.
 */
@Entity
@Table(name = "external_hire_requests")
public class ExternalHireRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ext_hire_request_id")
    private Long extHireRequestId;

    @Column(name = "role_id")
    private Integer roleIdFk;

    @Column(name = "request_status", length = 30)
    private String requestStatus = "PENDING";

    @Column(name = "retry_count")
    private Integer retryCount = 0;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        if (this.createdAt == null) this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Long getExtHireRequestId() { return extHireRequestId; }
    public void setExtHireRequestId(Long extHireRequestId) { this.extHireRequestId = extHireRequestId; }

    public Integer getRoleIdFk() { return roleIdFk; }
    public void setRoleIdFk(Integer roleIdFk) { this.roleIdFk = roleIdFk; }

    public String getRequestStatus() { return requestStatus; }
    public void setRequestStatus(String requestStatus) { this.requestStatus = requestStatus; }

    public Integer getRetryCount() { return retryCount; }
    public void setRetryCount(Integer retryCount) { this.retryCount = retryCount; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
