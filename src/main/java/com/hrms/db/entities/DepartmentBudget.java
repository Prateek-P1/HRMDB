package com.hrms.db.entities;

import jakarta.persistence.*;

/**
 * Department budgets — used to enforce spending limits by Expense Management.
 */
@Entity
@Table(name = "department_budgets")
public class DepartmentBudget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "department_id")
    private Integer departmentId;

    @Column(name = "department_name", length = 100)
    private String departmentName;

    @Column(name = "budget_limit")
    private Double budgetLimit;

    @Column(name = "current_spent")
    private Double currentSpent = 0.0;

    // --- Getters & Setters ---

    public Integer getDepartmentId() { return departmentId; }
    public void setDepartmentId(Integer departmentId) { this.departmentId = departmentId; }

    public String getDepartmentName() { return departmentName; }
    public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }

    public Double getBudgetLimit() { return budgetLimit; }
    public void setBudgetLimit(Double budgetLimit) { this.budgetLimit = budgetLimit; }

    public Double getCurrentSpent() { return currentSpent; }
    public void setCurrentSpent(Double currentSpent) { this.currentSpent = currentSpent; }
}
