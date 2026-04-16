package com.hrms.db.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Break records within a time entry — tracks break start/end.
 * Required by: Time Tracking subsystem.
 */
@Entity
@Table(name = "break_records")
public class BreakRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "break_id")
    private Long breakId;

    @Column(name = "entry_id", nullable = false)
    private Long entryId;

    @Column(name = "break_start_time")
    private LocalDateTime breakStartTime;

    @Column(name = "break_end_time")
    private LocalDateTime breakEndTime;

    // --- Getters & Setters ---

    public Long getBreakId() { return breakId; }
    public void setBreakId(Long breakId) { this.breakId = breakId; }

    public Long getEntryId() { return entryId; }
    public void setEntryId(Long entryId) { this.entryId = entryId; }

    public LocalDateTime getBreakStartTime() { return breakStartTime; }
    public void setBreakStartTime(LocalDateTime breakStartTime) { this.breakStartTime = breakStartTime; }

    public LocalDateTime getBreakEndTime() { return breakEndTime; }
    public void setBreakEndTime(LocalDateTime breakEndTime) { this.breakEndTime = breakEndTime; }
}
