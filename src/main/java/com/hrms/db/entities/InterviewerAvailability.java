package com.hrms.db.entities;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Interviewer Availability - slots when an interviewer is available.
 */
@Entity
@Table(name = "interviewer_availability")
public class InterviewerAvailability {

    // A synthetic ID is needed since availability is typically uniquely identified by interviewerId + date + time, or we can use a UUID.
    @Id
    @Column(name = "availability_id", length = 36)
    private String availabilityId;

    @Column(name = "interviewer_id", nullable = false, length = 36)
    private String interviewerId;

    @Column(name = "available_date")
    private LocalDate availableDate;

    @Column(name = "available_time")
    private LocalTime availableTime;

    @Column(name = "slot_duration")
    private Integer slotDuration;

    @Column(name = "status", length = 20)
    private String status = "ACTIVE"; // ACTIVE, DELETED

    // --- Getters & Setters ---

    public String getAvailabilityId() { return availabilityId; }
    public void setAvailabilityId(String availabilityId) { this.availabilityId = availabilityId; }

    public String getInterviewerId() { return interviewerId; }
    public void setInterviewerId(String interviewerId) { this.interviewerId = interviewerId; }

    public LocalDate getAvailableDate() { return availableDate; }
    public void setAvailableDate(LocalDate availableDate) { this.availableDate = availableDate; }

    public LocalTime getAvailableTime() { return availableTime; }
    public void setAvailableTime(LocalTime availableTime) { this.availableTime = availableTime; }

    public Integer getSlotDuration() { return slotDuration; }
    public void setSlotDuration(Integer slotDuration) { this.slotDuration = slotDuration; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
