package com.hrms.db.entities;

import jakarta.persistence.*;
import java.time.LocalDate;

/**
 * Benefit enrollments — tracks which employees are enrolled in which plans.
 * Required by: Benefits Administration.
 */
@Entity
@Table(name = "benefit_enrollments")
public class BenefitEnrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "enrollment_id")
    private Long enrollmentId;

    @Column(name = "emp_id", nullable = false, length = 20)
    private String empId;

    @Column(name = "plan_id")
    private Integer planId;

    @Column(name = "dependents_information", columnDefinition = "TEXT")
    private String dependentsInformation;

    @Column(name = "enrollment_status", length = 30)
    private String enrollmentStatus; // ACTIVE, PENDING, CANCELLED

    @Column(name = "enrollment_date")
    private LocalDate enrollmentDate;

    // --- Getters & Setters ---

    public Long getEnrollmentId() { return enrollmentId; }
    public void setEnrollmentId(Long enrollmentId) { this.enrollmentId = enrollmentId; }

    public String getEmpId() { return empId; }
    public void setEmpId(String empId) { this.empId = empId; }

    public Integer getPlanId() { return planId; }
    public void setPlanId(Integer planId) { this.planId = planId; }

    public String getDependentsInformation() { return dependentsInformation; }
    public void setDependentsInformation(String v) { this.dependentsInformation = v; }

    public String getEnrollmentStatus() { return enrollmentStatus; }
    public void setEnrollmentStatus(String enrollmentStatus) { this.enrollmentStatus = enrollmentStatus; }

    public LocalDate getEnrollmentDate() { return enrollmentDate; }
    public void setEnrollmentDate(LocalDate enrollmentDate) { this.enrollmentDate = enrollmentDate; }
}
