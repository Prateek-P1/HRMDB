package com.hrms.db.entities;

import jakarta.persistence.*;
import java.time.LocalDate;

/**
 * Successor assignments — maps employees to target roles.
 * Required by: Succession Planning.
 */
@Entity
@Table(name = "successor_assignments")
public class SuccessorAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "assignment_id")
    private Long assignmentId;

    @Column(name = "emp_id", nullable = false, length = 20)
    private String empId;

    @Column(name = "target_role_id")
    private Integer targetRoleId;

    @Column(name = "successor_rank")
    private Integer successorRank;

    @Column(name = "hr_decision", length = 50)
    private String hrDecision; // CONFIRMED, PROVISIONAL, REJECTED

    @Column(name = "assignment_date")
    private LocalDate assignmentDate;

    @Column(name = "no_successor_flag")
    private Boolean noSuccessorFlag = false;

    // --- Getters & Setters ---

    public Long getAssignmentId() { return assignmentId; }
    public void setAssignmentId(Long assignmentId) { this.assignmentId = assignmentId; }

    public String getEmpId() { return empId; }
    public void setEmpId(String empId) { this.empId = empId; }

    public Integer getTargetRoleId() { return targetRoleId; }
    public void setTargetRoleId(Integer targetRoleId) { this.targetRoleId = targetRoleId; }

    public Integer getSuccessorRank() { return successorRank; }
    public void setSuccessorRank(Integer successorRank) { this.successorRank = successorRank; }

    public String getHrDecision() { return hrDecision; }
    public void setHrDecision(String hrDecision) { this.hrDecision = hrDecision; }

    public LocalDate getAssignmentDate() { return assignmentDate; }
    public void setAssignmentDate(LocalDate assignmentDate) { this.assignmentDate = assignmentDate; }

    public Boolean getNoSuccessorFlag() { return noSuccessorFlag; }
    public void setNoSuccessorFlag(Boolean noSuccessorFlag) { this.noSuccessorFlag = noSuccessorFlag; }
}
