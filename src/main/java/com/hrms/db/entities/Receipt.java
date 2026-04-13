package com.hrms.db.entities;

import jakarta.persistence.*;

/**
 * Receipt attachments for expense claims.
 */
@Entity
@Table(name = "receipts")
public class Receipt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "receipt_id")
    private Long receiptId;

    @Column(name = "claim_id", nullable = false, length = 36)
    private String claimId;

    @Column(name = "file_path", length = 500)
    private String filePath;

    // --- Getters & Setters ---

    public Long getReceiptId() { return receiptId; }
    public void setReceiptId(Long receiptId) { this.receiptId = receiptId; }

    public String getClaimId() { return claimId; }
    public void setClaimId(String claimId) { this.claimId = claimId; }

    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }
}
