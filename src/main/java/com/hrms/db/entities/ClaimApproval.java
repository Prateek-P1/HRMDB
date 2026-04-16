package com.hrms.db.entities;

import jakarta.persistence.*;

/**
 * Claim approval decisions — links approvers to expense claims.
 */
@Entity
@Table(name = "claim_approvals")
public class ClaimApproval {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "approval_id")
    private Long approvalId;

    @Column(name = "claim_id", nullable = false, length = 36)
    private String claimId;

    @Column(name = "approver_id", length = 20)
    private String approverId;

    @Column(name = "status", length = 20)
    private String status; // APPROVED, REJECTED

    @Column(name = "comments", columnDefinition = "TEXT")
    private String comments;

    // --- Getters & Setters ---

    public Long getApprovalId() { return approvalId; }
    public void setApprovalId(Long approvalId) { this.approvalId = approvalId; }

    public String getClaimId() { return claimId; }
    public void setClaimId(String claimId) { this.claimId = claimId; }

    public String getApproverId() { return approverId; }
    public void setApproverId(String approverId) { this.approverId = approverId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getComments() { return comments; }
    public void setComments(String comments) { this.comments = comments; }
}
