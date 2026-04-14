package com.hrms.db.repositories.performance.models;

import java.util.Date;

public class Appraisal {
    private int appraisalId;
    private int employeeId;
    private int reviewerId;
    private int approverId;
    private String cycle;
    private float rating;          // e.g. 1.0 to 5.0
    private String comments;
    private String status;         // DRAFT, SUBMITTED, APPROVED
    private Date submittedDate;
    private Date approvedDate;

    public Appraisal() {}

    public Appraisal(int appraisalId, int employeeId, int reviewerId, String cycle) {
        this.appraisalId = appraisalId;
        this.employeeId = employeeId;
        this.reviewerId = reviewerId;
        this.cycle = cycle;
        this.status = "DRAFT";
        this.rating = 0.0f;
    }

    public int getAppraisalId() { return appraisalId; }
    public void setAppraisalId(int appraisalId) { this.appraisalId = appraisalId; }
    public int getEmployeeId() { return employeeId; }
    public void setEmployeeId(int employeeId) { this.employeeId = employeeId; }
    public int getReviewerId() { return reviewerId; }
    public void setReviewerId(int reviewerId) { this.reviewerId = reviewerId; }
    public int getApproverId() { return approverId; }
    public void setApproverId(int approverId) { this.approverId = approverId; }
    public String getCycle() { return cycle; }
    public void setCycle(String cycle) { this.cycle = cycle; }
    public float getRating() { return rating; }
    public void setRating(float rating) { this.rating = rating; }
    public String getComments() { return comments; }
    public void setComments(String comments) { this.comments = comments; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Date getSubmittedDate() { return submittedDate; }
    public void setSubmittedDate(Date submittedDate) { this.submittedDate = submittedDate; }
    public Date getApprovedDate() { return approvedDate; }
    public void setApprovedDate(Date approvedDate) { this.approvedDate = approvedDate; }

    @Override
    public String toString() {
        return "Appraisal{id=" + appraisalId + ", empId=" + employeeId + ", rating=" + rating + ", status='" + status + "'}";
    }
}
