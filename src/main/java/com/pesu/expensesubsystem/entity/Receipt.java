package com.pesu.expensesubsystem.entity;

import java.time.LocalDateTime;

/**
 * Receipt/attachment DTO for Expense Claims.
 */
public class Receipt {

    private Long receiptId;
    private Long claimId;
    private String filePath;
    private String fileName;
    private LocalDateTime uploadDate;

    public Receipt() {
    }

    public Long getReceiptId() { return receiptId; }
    public void setReceiptId(Long receiptId) { this.receiptId = receiptId; }

    public Long getClaimId() { return claimId; }
    public void setClaimId(Long claimId) { this.claimId = claimId; }

    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public LocalDateTime getUploadDate() { return uploadDate; }
    public void setUploadDate(LocalDateTime uploadDate) { this.uploadDate = uploadDate; }
}
