package com.hrms.db.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Computed payroll outputs written back by Payroll subsystem.
 */
@Entity
@Table(name = "payroll_results", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"emp_id", "batch_id", "pay_period"})
})
public class PayrollResult {

    @Id
    @Column(name = "record_id", length = 36)
    private String recordId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emp_id", nullable = false)
    private Employee employee;

    @Column(name = "batch_id", nullable = false, length = 50)
    private String batchId;

    @Column(name = "pay_period", nullable = false, length = 10)
    private String payPeriod;

    @Column(name = "final_gross_pay")
    private Double finalGrossPay;

    @Column(name = "final_net_pay")
    private Double finalNetPay;

    @Column(name = "penalty_amount")
    private Double penaltyAmount = 0.0;

    @Column(name = "pf_amount")
    private Double pfAmount = 0.0;

    @Column(name = "pt_amount")
    private Double ptAmount = 0.0;

    @Column(name = "monthly_tds_amount")
    private Double monthlyTdsAmount = 0.0;

    @Column(name = "overtime_pay")
    private Double overtimePay = 0.0;

    @Column(name = "payout_amount")
    private Double payoutAmount;

    @Column(name = "reimbursement_payout")
    private Double reimbursementPayout = 0.0;

    @Column(name = "gratuity_amount")
    private Double gratuityAmount = 0.0;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    @PrePersist
    protected void onCreate() {
        this.processedAt = LocalDateTime.now();
    }

    // --- Getters & Setters ---

    public String getRecordId() { return recordId; }
    public void setRecordId(String recordId) { this.recordId = recordId; }

    public Employee getEmployee() { return employee; }
    public void setEmployee(Employee employee) { this.employee = employee; }

    public String getBatchId() { return batchId; }
    public void setBatchId(String batchId) { this.batchId = batchId; }

    public String getPayPeriod() { return payPeriod; }
    public void setPayPeriod(String payPeriod) { this.payPeriod = payPeriod; }

    public Double getFinalGrossPay() { return finalGrossPay; }
    public void setFinalGrossPay(Double finalGrossPay) { this.finalGrossPay = finalGrossPay; }

    public Double getFinalNetPay() { return finalNetPay; }
    public void setFinalNetPay(Double finalNetPay) { this.finalNetPay = finalNetPay; }

    public Double getPenaltyAmount() { return penaltyAmount; }
    public void setPenaltyAmount(Double penaltyAmount) { this.penaltyAmount = penaltyAmount; }

    public Double getPfAmount() { return pfAmount; }
    public void setPfAmount(Double pfAmount) { this.pfAmount = pfAmount; }

    public Double getPtAmount() { return ptAmount; }
    public void setPtAmount(Double ptAmount) { this.ptAmount = ptAmount; }

    public Double getMonthlyTdsAmount() { return monthlyTdsAmount; }
    public void setMonthlyTdsAmount(Double monthlyTdsAmount) { this.monthlyTdsAmount = monthlyTdsAmount; }

    public Double getOvertimePay() { return overtimePay; }
    public void setOvertimePay(Double overtimePay) { this.overtimePay = overtimePay; }

    public Double getPayoutAmount() { return payoutAmount; }
    public void setPayoutAmount(Double payoutAmount) { this.payoutAmount = payoutAmount; }

    public Double getReimbursementPayout() { return reimbursementPayout; }
    public void setReimbursementPayout(Double reimbursementPayout) { this.reimbursementPayout = reimbursementPayout; }

    public Double getGratuityAmount() { return gratuityAmount; }
    public void setGratuityAmount(Double gratuityAmount) { this.gratuityAmount = gratuityAmount; }

    public LocalDateTime getProcessedAt() { return processedAt; }
}
