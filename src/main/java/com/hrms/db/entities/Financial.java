package com.hrms.db.entities;

import jakarta.persistence.*;

/**
 * Financial inputs per pay period — used by Payroll, Expense, Benefits.
 */
@Entity
@Table(name = "financials", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"emp_id", "pay_period"})
})
public class Financial {

    @Id
    @Column(name = "record_id", length = 36)
    private String recordId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emp_id", nullable = false)
    private Employee employee;

    @Column(name = "pay_period", nullable = false, length = 10)
    private String payPeriod;

    @Column(name = "pending_claims")
    private Double pendingClaims = 0.0;

    @Column(name = "approved_reimbursement")
    private Double approvedReimbursement = 0.0;

    @Column(name = "insurance_premium")
    private Double insurancePremium = 0.0;

    @Column(name = "declared_investments")
    private Double declaredInvestments = 0.0;

    // --- Getters & Setters ---

    public String getRecordId() { return recordId; }
    public void setRecordId(String recordId) { this.recordId = recordId; }

    public Employee getEmployee() { return employee; }
    public void setEmployee(Employee employee) { this.employee = employee; }

    public String getPayPeriod() { return payPeriod; }
    public void setPayPeriod(String payPeriod) { this.payPeriod = payPeriod; }

    public Double getPendingClaims() { return pendingClaims; }
    public void setPendingClaims(Double pendingClaims) { this.pendingClaims = pendingClaims; }

    public Double getApprovedReimbursement() { return approvedReimbursement; }
    public void setApprovedReimbursement(Double v) { this.approvedReimbursement = v; }

    public Double getInsurancePremium() { return insurancePremium; }
    public void setInsurancePremium(Double insurancePremium) { this.insurancePremium = insurancePremium; }

    public Double getDeclaredInvestments() { return declaredInvestments; }
    public void setDeclaredInvestments(Double v) { this.declaredInvestments = v; }
}
