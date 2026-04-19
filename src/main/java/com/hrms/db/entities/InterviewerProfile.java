package com.hrms.db.entities;

import jakarta.persistence.*;

/**
 * Interviewer Profiles - information about employees conducting interviews.
 */
@Entity
@Table(name = "interviewer_profiles")
public class InterviewerProfile {

    @Id
    @Column(name = "interviewer_id", length = 36)
    private String interviewerId;

    @Column(name = "name", length = 100)
    private String name;

    @Column(name = "department", length = 100)
    private String department;

    @Column(name = "expertise", length = 200)
    private String expertise;

    @Column(name = "contact", length = 100)
    private String contact;

    @Column(name = "status", length = 20)
    private String status = "ACTIVE"; // ACTIVE, DELETED

    // --- Getters & Setters ---

    public String getInterviewerId() { return interviewerId; }
    public void setInterviewerId(String interviewerId) { this.interviewerId = interviewerId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getExpertise() { return expertise; }
    public void setExpertise(String expertise) { this.expertise = expertise; }

    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
