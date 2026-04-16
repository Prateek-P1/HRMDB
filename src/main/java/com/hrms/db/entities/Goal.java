package com.hrms.db.entities;

import jakarta.persistence.*;
import java.time.LocalDate;

/**
 * Employee goals — used by Performance Management.
 */
@Entity
@Table(name = "goals")
public class Goal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "goal_id")
    private Long goalId;

    @Column(name = "emp_id", nullable = false, length = 20)
    private String empId;

    @Column(name = "goal_title", length = 200)
    private String goalTitle;

    @Column(name = "goal_description", columnDefinition = "TEXT")
    private String goalDescription;

    @Column(name = "goal_start_date")
    private LocalDate goalStartDate;

    @Column(name = "goal_end_date")
    private LocalDate goalEndDate;

    @Column(name = "goal_status", length = 30)
    private String goalStatus; // NOT_STARTED, IN_PROGRESS, COMPLETED

    // --- Getters & Setters ---

    public Long getGoalId() { return goalId; }
    public void setGoalId(Long goalId) { this.goalId = goalId; }

    public String getEmpId() { return empId; }
    public void setEmpId(String empId) { this.empId = empId; }

    public String getGoalTitle() { return goalTitle; }
    public void setGoalTitle(String goalTitle) { this.goalTitle = goalTitle; }

    public String getGoalDescription() { return goalDescription; }
    public void setGoalDescription(String goalDescription) { this.goalDescription = goalDescription; }

    public LocalDate getGoalStartDate() { return goalStartDate; }
    public void setGoalStartDate(LocalDate goalStartDate) { this.goalStartDate = goalStartDate; }

    public LocalDate getGoalEndDate() { return goalEndDate; }
    public void setGoalEndDate(LocalDate goalEndDate) { this.goalEndDate = goalEndDate; }

    public String getGoalStatus() { return goalStatus; }
    public void setGoalStatus(String goalStatus) { this.goalStatus = goalStatus; }
}
