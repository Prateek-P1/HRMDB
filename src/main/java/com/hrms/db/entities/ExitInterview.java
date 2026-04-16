package com.hrms.db.entities;

import jakarta.persistence.*;
import java.time.LocalDate;

/**
 * Exit interviews — feedback from departing employees.
 * Required by: Onboarding/Offboarding, Attrition Analytics.
 */
@Entity
@Table(name = "exit_interviews")
public class ExitInterview {

    @Id
    @Column(name = "interview_id", length = 36)
    private String interviewId;

    @Column(name = "emp_id", nullable = false, length = 20)
    private String empId;

    @Column(name = "primary_reason", length = 200)
    private String primaryReason;

    @Column(name = "feedback_text", columnDefinition = "TEXT")
    private String feedbackText;

    @Column(name = "satisfaction_rating")
    private Integer satisfactionRating; // 1-5

    @Column(name = "issues_reported", columnDefinition = "TEXT")
    private String issuesReported;

    @Column(name = "interviewer_notes", columnDefinition = "TEXT")
    private String interviewerNotes;

    @Column(name = "exit_date")
    private LocalDate exitDate;

    // --- Getters & Setters ---

    public String getInterviewId() { return interviewId; }
    public void setInterviewId(String interviewId) { this.interviewId = interviewId; }

    public String getEmpId() { return empId; }
    public void setEmpId(String empId) { this.empId = empId; }

    public String getPrimaryReason() { return primaryReason; }
    public void setPrimaryReason(String primaryReason) { this.primaryReason = primaryReason; }

    public String getFeedbackText() { return feedbackText; }
    public void setFeedbackText(String feedbackText) { this.feedbackText = feedbackText; }

    public Integer getSatisfactionRating() { return satisfactionRating; }
    public void setSatisfactionRating(Integer satisfactionRating) { this.satisfactionRating = satisfactionRating; }

    public String getIssuesReported() { return issuesReported; }
    public void setIssuesReported(String issuesReported) { this.issuesReported = issuesReported; }

    public String getInterviewerNotes() { return interviewerNotes; }
    public void setInterviewerNotes(String interviewerNotes) { this.interviewerNotes = interviewerNotes; }

    public LocalDate getExitDate() { return exitDate; }
    public void setExitDate(LocalDate exitDate) { this.exitDate = exitDate; }
}
