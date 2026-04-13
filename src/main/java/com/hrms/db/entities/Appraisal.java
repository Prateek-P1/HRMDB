package com.hrms.db.entities;

import jakarta.persistence.*;
import java.time.LocalDate;

/**
 * Employee appraisals — periodic performance reviews.
 * Required by: Performance Management.
 */
@Entity
@Table(name = "appraisals")
public class Appraisal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "appraisal_id")
    private Long appraisalId;

    @Column(name = "emp_id", nullable = false, length = 20)
    private String empId;

    @Column(name = "appraisal_period", length = 30)
    private String appraisalPeriod; // e.g. "2024-H1"

    @Column(name = "appraisal_score")
    private Double appraisalScore;

    @Column(name = "appraisal_date")
    private LocalDate appraisalDate;

    @Column(name = "appraisal_status", length = 30)
    private String appraisalStatus; // DRAFT, SUBMITTED, FINALIZED

    @Column(name = "reviewer_id", length = 20)
    private String reviewerId;

    // --- Getters & Setters ---

    public Long getAppraisalId() { return appraisalId; }
    public void setAppraisalId(Long appraisalId) { this.appraisalId = appraisalId; }

    public String getEmpId() { return empId; }
    public void setEmpId(String empId) { this.empId = empId; }

    public String getAppraisalPeriod() { return appraisalPeriod; }
    public void setAppraisalPeriod(String appraisalPeriod) { this.appraisalPeriod = appraisalPeriod; }

    public Double getAppraisalScore() { return appraisalScore; }
    public void setAppraisalScore(Double appraisalScore) { this.appraisalScore = appraisalScore; }

    public LocalDate getAppraisalDate() { return appraisalDate; }
    public void setAppraisalDate(LocalDate appraisalDate) { this.appraisalDate = appraisalDate; }

    public String getAppraisalStatus() { return appraisalStatus; }
    public void setAppraisalStatus(String appraisalStatus) { this.appraisalStatus = appraisalStatus; }

    public String getReviewerId() { return reviewerId; }
    public void setReviewerId(String reviewerId) { this.reviewerId = reviewerId; }
}
