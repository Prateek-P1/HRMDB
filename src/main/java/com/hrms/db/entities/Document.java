package com.hrms.db.entities;

import jakarta.persistence.*;
import java.time.LocalDate;

/**
 * Employee documents — ID proofs, contracts, certificates, etc.
 * Required by: Document Management, Onboarding.
 */
@Entity
@Table(name = "documents")
public class Document {

    @Id
    @Column(name = "document_id", length = 36)
    private String documentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emp_id", nullable = false)
    private Employee employee;

    @Column(name = "document_type", length = 50)
    private String documentType; // ID_PROOF, CONTRACT, CERTIFICATE, PAYSLIP, OTHER

    @Column(name = "file_path", length = 500)
    private String filePath;

    @Column(name = "upload_date")
    private LocalDate uploadDate;

    @Column(name = "verification_status", length = 20)
    private String verificationStatus; // PENDING, VERIFIED, REJECTED

    // --- Getters & Setters ---

    public String getDocumentId() { return documentId; }
    public void setDocumentId(String documentId) { this.documentId = documentId; }

    public Employee getEmployee() { return employee; }
    public void setEmployee(Employee employee) { this.employee = employee; }

    public String getDocumentType() { return documentType; }
    public void setDocumentType(String documentType) { this.documentType = documentType; }

    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }

    public LocalDate getUploadDate() { return uploadDate; }
    public void setUploadDate(LocalDate uploadDate) { this.uploadDate = uploadDate; }

    public String getVerificationStatus() { return verificationStatus; }
    public void setVerificationStatus(String verificationStatus) { this.verificationStatus = verificationStatus; }
}
