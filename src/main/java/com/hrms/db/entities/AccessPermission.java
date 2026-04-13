package com.hrms.db.entities;

import jakarta.persistence.*;

/**
 * Access permissions — role-based access control.
 * Required by: Data Security.
 */
@Entity
@Table(name = "access_permissions")
public class AccessPermission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "permission_id")
    private Long permissionId;

    @Column(name = "user_id", nullable = false, length = 20)
    private String userId;

    @Column(name = "user_role", length = 50)
    private String userRole;

    @Column(name = "access_permissions", columnDefinition = "TEXT")
    private String accessPermissions; // JSON or comma-separated permissions

    @Column(name = "compliance_status", length = 30)
    private String complianceStatus;

    // --- Getters & Setters ---

    public Long getPermissionId() { return permissionId; }
    public void setPermissionId(Long permissionId) { this.permissionId = permissionId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getUserRole() { return userRole; }
    public void setUserRole(String userRole) { this.userRole = userRole; }

    public String getAccessPermissions() { return accessPermissions; }
    public void setAccessPermissions(String accessPermissions) { this.accessPermissions = accessPermissions; }

    public String getComplianceStatus() { return complianceStatus; }
    public void setComplianceStatus(String complianceStatus) { this.complianceStatus = complianceStatus; }
}
