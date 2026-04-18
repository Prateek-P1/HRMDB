package com.hrms.db.entities;

import jakarta.persistence.*;
import java.time.LocalDate;

/**
 * Job applications — links candidates to job postings.
 */
@Entity
@Table(name = "applications")
public class Application {

    @Id
    @Column(name = "application_id", length = 36)
    private String applicationId;

    @Column(name = "candidate_id", nullable = false, length = 36)
    private String candidateId;

    @Column(name = "job_id", nullable = false, length = 36)
    private String jobId;

    @Column(name = "date_applied")
    private LocalDate dateApplied;

    @Column(name = "current_stage", length = 50)
    private String currentStage; // APPLIED, SCREENING, INTERVIEW, OFFER, HIRED, REJECTED

    @Column(name = "history", columnDefinition = "TEXT")
    private String history;

    @Column(name = "status_timestamp")
    private java.time.LocalDateTime timestamp;

    @Column(name = "status", length = 20)
    private String status = "ACTIVE"; // ACTIVE, DELETED

    @Column(name = "score")
    private Integer score;

    @Column(name = "ranking")
    private Integer ranking;

    @Column(name = "shortlist_status", length = 20)
    private String shortlistStatus;

    // --- Getters & Setters ---

    public String getApplicationId() { return applicationId; }
    public void setApplicationId(String applicationId) { this.applicationId = applicationId; }

    public String getCandidateId() { return candidateId; }
    public void setCandidateId(String candidateId) { this.candidateId = candidateId; }

    public String getJobId() { return jobId; }
    public void setJobId(String jobId) { this.jobId = jobId; }

    public LocalDate getDateApplied() { return dateApplied; }
    public void setDateApplied(LocalDate dateApplied) { this.dateApplied = dateApplied; }

    public String getCurrentStage() { return currentStage; }
    public void setCurrentStage(String currentStage) { this.currentStage = currentStage; }

    public String getHistory() { return history; }
    public void setHistory(String history) { this.history = history; }

    public java.time.LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(java.time.LocalDateTime timestamp) { this.timestamp = timestamp; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }

    public Integer getRanking() { return ranking; }
    public void setRanking(Integer ranking) { this.ranking = ranking; }

    public String getShortlistStatus() { return shortlistStatus; }
    public void setShortlistStatus(String shortlistStatus) { this.shortlistStatus = shortlistStatus; }
}
