package com.hrms.db.entities;

import jakarta.persistence.*;
import java.time.LocalDate;

/**
 * Payroll deductions for benefit plans.
 * Required by: Benefits Administration, Payroll.
 */
@Entity
@Table(name = "benefit_deductions")
public class BenefitDeduction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "deduction_id")
    private Long deductionId;

    @Column(name = "emp_id", nullable = false, length = 20)
    private String empId;

    @Column(name = "plan_id")
    private Integer planId;

    @Column(name = "deduction_amount")
    private Double deductionAmount;

    @Column(name = "payroll_cycle", length = 20)
    private String payrollCycle;

    @Column(name = "deduction_date")
    private LocalDate deductionDate;

    // --- Getters & Setters ---

    public Long getDeductionId() { return deductionId; }
    public void setDeductionId(Long deductionId) { this.deductionId = deductionId; }

    public String getEmpId() { return empId; }
    public void setEmpId(String empId) { this.empId = empId; }

    public Integer getPlanId() { return planId; }
    public void setPlanId(Integer planId) { this.planId = planId; }

    public Double getDeductionAmount() { return deductionAmount; }
    public void setDeductionAmount(Double deductionAmount) { this.deductionAmount = deductionAmount; }

    public String getPayrollCycle() { return payrollCycle; }
    public void setPayrollCycle(String payrollCycle) { this.payrollCycle = payrollCycle; }

    public LocalDate getDeductionDate() { return deductionDate; }
    public void setDeductionDate(LocalDate deductionDate) { this.deductionDate = deductionDate; }
}
