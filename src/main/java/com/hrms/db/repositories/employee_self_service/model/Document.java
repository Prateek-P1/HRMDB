package com.hrms.db.repositories.employee_self_service.model;

public class Document {

    private int documentId;
    private int employeeId;
    private DocumentType documentType;
    private String fileUrl;
    private String uploadDate;
    private String verificationStatus;

    public Document() {}

    public Document(int documentId,
                    int employeeId,
                    DocumentType documentType,
                    String fileUrl,
                    String uploadDate,
                    String verificationStatus) {
        this.documentId = documentId;
        this.employeeId = employeeId;
        this.documentType = documentType;
        this.fileUrl = fileUrl;
        this.uploadDate = uploadDate;
        this.verificationStatus = verificationStatus;
    }

    public int getDocumentId() {
        return documentId;
    }

    public void setDocumentId(int documentId) {
        this.documentId = documentId;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public DocumentType getDocumentType() {
        return documentType;
    }

    public void setDocumentType(DocumentType documentType) {
        this.documentType = documentType;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(String uploadDate) {
        this.uploadDate = uploadDate;
    }

    public String getVerificationStatus() {
        return verificationStatus;
    }

    public void setVerificationStatus(String verificationStatus) {
        this.verificationStatus = verificationStatus;
    }
}
