package com.hrms.db.entities;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Time entries — punch in/out records per employee per day.
 * Required by: Time Tracking subsystem.
 */
@Entity
@Table(name = "time_entries")
public class TimeEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "entry_id")
    private Long entryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emp_id", nullable = false)
    private Employee employee;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "punch_in_time")
    private LocalDateTime punchInTime;

    @Column(name = "punch_out_time")
    private LocalDateTime punchOutTime;

    @Column(name = "total_hours")
    private Double totalHours;

    @Column(name = "status", length = 20)
    private String status; // PRESENT, ABSENT, HALF_DAY

    // --- Getters & Setters ---

    public Long getEntryId() { return entryId; }
    public void setEntryId(Long entryId) { this.entryId = entryId; }

    public Employee getEmployee() { return employee; }
    public void setEmployee(Employee employee) { this.employee = employee; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public LocalDateTime getPunchInTime() { return punchInTime; }
    public void setPunchInTime(LocalDateTime punchInTime) { this.punchInTime = punchInTime; }

    public LocalDateTime getPunchOutTime() { return punchOutTime; }
    public void setPunchOutTime(LocalDateTime punchOutTime) { this.punchOutTime = punchOutTime; }

    public Double getTotalHours() { return totalHours; }
    public void setTotalHours(Double totalHours) { this.totalHours = totalHours; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
