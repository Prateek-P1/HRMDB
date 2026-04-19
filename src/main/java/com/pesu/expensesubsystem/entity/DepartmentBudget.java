package com.pesu.expensesubsystem.entity;

import java.math.BigDecimal;

/**
 * Department budget DTO used by the Expense Management repository interfaces.
 */
public class DepartmentBudget {

    private String departmentName;
    private BigDecimal budgetLimit;
    private BigDecimal currentSpent;

    public DepartmentBudget() {
    }

    public DepartmentBudget(String departmentName, BigDecimal budgetLimit, BigDecimal currentSpent) {
        this.departmentName = departmentName;
        this.budgetLimit = budgetLimit;
        this.currentSpent = currentSpent;
    }

    public String getDepartmentName() { return departmentName; }
    public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }

    public BigDecimal getBudgetLimit() { return budgetLimit; }
    public void setBudgetLimit(BigDecimal budgetLimit) { this.budgetLimit = budgetLimit; }

    public BigDecimal getCurrentSpent() { return currentSpent; }
    public void setCurrentSpent(BigDecimal currentSpent) { this.currentSpent = currentSpent; }
}
