package com.hrms.db.entities;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Interview schedules — links candidates, interviewers, and time slots.
 */
@Entity
@Table(name = "interview_schedules")
public class InterviewSchedule {

    @Id
    @Column(name = "schedule_id", length = 36)
    private String scheduleId;

    @Column(name = "candidate_id", nullable = false, length = 36)
    private String candidateId;

    @Column(name = "interviewer_id", length = 20)
    private String interviewerId;

    @Column(name = "interview_date")
    private LocalDate interviewDate;

    @Column(name = "interview_time")
    private LocalTime interviewTime;

    @Column(name = "interview_type", length = 50)
    private String interviewType; // PHONE, VIDEO, IN_PERSON, TECHNICAL

    @Column(name = "feedback", columnDefinition = "TEXT")
    private String feedback;

    @Column(name = "score")
    private Integer score;

    @Column(name = "outcome", length = 20)
    private String outcome; // PASS, FAIL, ON_HOLD

    @Column(name = "status", length = 20)
    private String status = "ACTIVE"; // ACTIVE, DELETED

    // --- Getters & Setters ---

    public String getScheduleId() { return scheduleId; }
    public void setScheduleId(String scheduleId) { this.scheduleId = scheduleId; }

    public String getCandidateId() { return candidateId; }
    public void setCandidateId(String candidateId) { this.candidateId = candidateId; }

    public String getInterviewerId() { return interviewerId; }
    public void setInterviewerId(String interviewerId) { this.interviewerId = interviewerId; }

    public LocalDate getInterviewDate() { return interviewDate; }
    public void setInterviewDate(LocalDate interviewDate) { this.interviewDate = interviewDate; }

    public LocalTime getInterviewTime() { return interviewTime; }
    public void setInterviewTime(LocalTime interviewTime) { this.interviewTime = interviewTime; }

    public String getInterviewType() { return interviewType; }
    public void setInterviewType(String interviewType) { this.interviewType = interviewType; }

    public String getFeedback() { return feedback; }
    public void setFeedback(String feedback) { this.feedback = feedback; }

    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }

    public String getOutcome() { return outcome; }
    public void setOutcome(String outcome) { this.outcome = outcome; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
