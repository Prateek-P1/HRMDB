package com.hrms.db.entities;

import jakarta.persistence.*;

/**
 * Screening Results - results of the initial application screening.
 */
@Entity
@Table(name = "screening_results")
public class ScreeningResult {

    @Id
    @Column(name = "application_id", length = 36)
    private String applicationId;

    @Column(name = "score")
    private Integer score;

    @Column(name = "ranking")
    private Integer ranking;

    @Column(name = "shortlist_status", length = 20)
    private String shortlistStatus;

    @Column(name = "status", length = 20)
    private String status = "ACTIVE"; // ACTIVE, DELETED

    // --- Getters & Setters ---

    public String getApplicationId() { return applicationId; }
    public void setApplicationId(String applicationId) { this.applicationId = applicationId; }

    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }

    public Integer getRanking() { return ranking; }
    public void setRanking(Integer ranking) { this.ranking = ranking; }

    public String getShortlistStatus() { return shortlistStatus; }
    public void setShortlistStatus(String shortlistStatus) { this.shortlistStatus = shortlistStatus; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
