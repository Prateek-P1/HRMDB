package com.hrms.db.entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * SuccessionPoolEntry — employees tracked in the succession talent pool.
 *
 * This entity is introduced to support the Succession Planning repository interfaces.
 */
@Entity
@Table(
        name = "succession_pool_entries",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_succession_pool_emp", columnNames = {"emp_id"})
        }
)
public class SuccessionPoolEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "entry_id")
    private Long entryId;

    @Column(name = "emp_id", nullable = false, length = 20)
    private String empId;

    @Column(name = "eligible")
    private Boolean eligible = true;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        if (this.createdAt == null) this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Long getEntryId() { return entryId; }
    public void setEntryId(Long entryId) { this.entryId = entryId; }

    public String getEmpId() { return empId; }
    public void setEmpId(String empId) { this.empId = empId; }

    public Boolean getEligible() { return eligible; }
    public void setEligible(Boolean eligible) { this.eligible = eligible; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
