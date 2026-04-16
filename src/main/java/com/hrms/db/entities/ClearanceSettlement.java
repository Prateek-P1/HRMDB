package com.hrms.db.entities;

import jakarta.persistence.*;

/**
 * Clearance and settlement for exiting employees.
 * Required by: Employee Onboarding/Offboarding.
 */
@Entity
@Table(name = "clearance_settlements")
public class ClearanceSettlement {

    @Id
    @Column(name = "clearance_id", length = 36)
    private String clearanceId;

    @Column(name = "emp_id", length = 20)
    private String empId;

    @Column(name = "settlement_amount")
    private Double settlementAmount;

    @Column(name = "clearance_status", length = 30)
    private String clearanceStatus; // PENDING, IN_PROGRESS, COMPLETED

    @Column(name = "asset_return_status", length = 30)
    private String assetReturnStatus;

    @Column(name = "settlement_type", length = 50)
    private String settlementType; // RESIGNATION, TERMINATION, RETIREMENT

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    // Transient employee reference (not persisted directly — empId is the FK column)
    @Transient
    private Employee employeeObj;

    // --- Core Getters & Setters ---

    public String getClearanceId() { return clearanceId; }
    public void setClearanceId(String clearanceId) { this.clearanceId = clearanceId; }

    public String getEmpId() { return empId; }
    public void setEmpId(String empId) { this.empId = empId; }

    public Double getSettlementAmount() { return settlementAmount; }
    public void setSettlementAmount(Double settlementAmount) { this.settlementAmount = settlementAmount; }

    public String getClearanceStatus() { return clearanceStatus; }
    public void setClearanceStatus(String clearanceStatus) { this.clearanceStatus = clearanceStatus; }

    public String getAssetReturnStatus() { return assetReturnStatus; }
    public void setAssetReturnStatus(String assetReturnStatus) { this.assetReturnStatus = assetReturnStatus; }

    public String getSettlementType() { return settlementType; }
    public void setSettlementType(String settlementType) { this.settlementType = settlementType; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    // --- Alias getters used by OnboardingRepositoryImpl ---

    /** Alias for clearanceId — used by exit/settlement flow */
    public String getSettlementId() { return clearanceId; }
    public void setSettlementId(String id) { this.clearanceId = id; }

    /** Transient employee object — sets empId when assigned */
    public Employee getEmployee() { return employeeObj; }
    public void setEmployee(Employee employee) {
        this.employeeObj = employee;
        if (employee != null) this.empId = employee.getEmpId();
    }

    /** Alias for clearanceStatus */
    public String getStatus() { return clearanceStatus; }
    public void setStatus(String status) { this.clearanceStatus = status; }

    /** Alias for settlementAmount */
    public Double getFinalSettlementAmount() { return settlementAmount; }
    public void setFinalSettlementAmount(Double amount) { this.settlementAmount = amount; }
}
