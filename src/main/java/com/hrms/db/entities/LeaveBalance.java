package com.hrms.db.entities;

import jakarta.persistence.*;

/**
 * Leave balances per employee — tracks remaining leave by type.
 * Required by: Leave Management, Employee Self-Service Portal.
 */
@Entity
@Table(name = "leave_balances", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"emp_id", "leave_type"})
})
public class LeaveBalance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "balance_id")
    private Long balanceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emp_id", nullable = false)
    private Employee employee;

    @Column(name = "leave_type", nullable = false, length = 30)
    private String leaveType; // ANNUAL, SICK, CASUAL

    @Column(name = "total_entitled")
    private Double totalEntitled = 0.0;

    @Column(name = "used")
    private Double used = 0.0;

    @Column(name = "balance")
    private Double balance = 0.0;

    @Column(name = "carry_forward")
    private Double carryForward = 0.0;

    // --- Getters & Setters ---

    public Long getBalanceId() { return balanceId; }
    public void setBalanceId(Long balanceId) { this.balanceId = balanceId; }

    public Employee getEmployee() { return employee; }
    public void setEmployee(Employee employee) { this.employee = employee; }

    public String getLeaveType() { return leaveType; }
    public void setLeaveType(String leaveType) { this.leaveType = leaveType; }

    public Double getTotalEntitled() { return totalEntitled; }
    public void setTotalEntitled(Double totalEntitled) { this.totalEntitled = totalEntitled; }

    public Double getUsed() { return used; }
    public void setUsed(Double used) { this.used = used; }

    public Double getBalance() { return balance; }
    public void setBalance(Double balance) { this.balance = balance; }

    public Double getCarryForward() { return carryForward; }
    public void setCarryForward(Double carryForward) { this.carryForward = carryForward; }
}
