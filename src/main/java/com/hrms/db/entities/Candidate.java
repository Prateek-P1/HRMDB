package com.hrms.db.entities;

import jakarta.persistence.*;

/**
 * Candidates — applicant profiles for recruitment.
 */
@Entity
@Table(name = "candidates")
public class Candidate {

    @Id
    @Column(name = "candidate_id", length = 36)
    private String candidateId;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "contact_info", length = 200)
    private String contactInfo;

    @Column(name = "resume_path", length = 500)
    private String resumePath;

    @Column(name = "skills", columnDefinition = "TEXT")
    private String skills;

    @Column(name = "source", length = 50)
    private String source; // REFERRAL, LINKEDIN, PORTAL, etc.

    // --- Getters & Setters ---

    public String getCandidateId() { return candidateId; }
    public void setCandidateId(String candidateId) { this.candidateId = candidateId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getContactInfo() { return contactInfo; }
    public void setContactInfo(String contactInfo) { this.contactInfo = contactInfo; }

    public String getResumePath() { return resumePath; }
    public void setResumePath(String resumePath) { this.resumePath = resumePath; }

    public String getSkills() { return skills; }
    public void setSkills(String skills) { this.skills = skills; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
}
