package com.hrms.db.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Job postings — used by Recruitment Management.
 */
@Entity
@Table(name = "job_postings")
public class JobPosting {

    @Id
    @Column(name = "job_id", length = 36)
    private String jobId;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "department", length = 100)
    private String department;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "salary")
    private Double salary;

    @Column(name = "status", length = 20)
    private String status = "OPEN"; // OPEN, CLOSED, ON_HOLD

    @Column(name = "platform_name", length = 100)
    private String platformName;

    @Column(name = "channel_type", length = 50)
    private String channelType;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    // --- Getters & Setters ---

    public String getJobId() { return jobId; }
    public void setJobId(String jobId) { this.jobId = jobId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Double getSalary() { return salary; }
    public void setSalary(Double salary) { this.salary = salary; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPlatformName() { return platformName; }
    public void setPlatformName(String platformName) { this.platformName = platformName; }

    public String getChannelType() { return channelType; }
    public void setChannelType(String channelType) { this.channelType = channelType; }

    public LocalDateTime getCreatedAt() { return createdAt; }
}
