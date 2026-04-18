package com.hrms.db.entities;

import jakarta.persistence.*;

/**
 * Interview Results - outcome of an interview schedule.
 */
@Entity
@Table(name = "interview_results")
public class InterviewResult {

    @Id
    @Column(name = "schedule_id", length = 36)
    private String scheduleId;

    @Column(name = "feedback", columnDefinition = "TEXT")
    private String feedback;

    @Column(name = "score")
    private Integer score;

    @Column(name = "pass_fail_outcome", length = 20)
    private String passFailOutcome; // PASS, FAIL
    
    @Column(name = "status", length = 20)
    private String status = "ACTIVE"; // ACTIVE, DELETED

    // --- Getters & Setters ---

    public String getScheduleId() { return scheduleId; }
    public void setScheduleId(String scheduleId) { this.scheduleId = scheduleId; }

    public String getFeedback() { return feedback; }
    public void setFeedback(String feedback) { this.feedback = feedback; }

    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }

    public String getPassFailOutcome() { return passFailOutcome; }
    public void setPassFailOutcome(String passFailOutcome) { this.passFailOutcome = passFailOutcome; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
