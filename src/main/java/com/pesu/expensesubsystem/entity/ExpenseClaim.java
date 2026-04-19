package com.pesu.expensesubsystem.entity;

import com.pesu.expensesubsystem.enums.CategoryType;
import com.pesu.expensesubsystem.enums.ClaimStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Expense claim DTO used by the Expense Management repository interfaces.
 */
public class ExpenseClaim {

    private Long claimId;
    private String employeeId;
    private BigDecimal amount;
    private LocalDate expenseDate;
    private ClaimStatus status;
    private CategoryType category;
    private String description;
    private LocalDate submissionDate;
    private String approvedBy;

    public ExpenseClaim() {
    }

    public Long getClaimId() { return claimId; }
    public void setClaimId(Long claimId) { this.claimId = claimId; }

    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public LocalDate getExpenseDate() { return expenseDate; }
    public void setExpenseDate(LocalDate expenseDate) { this.expenseDate = expenseDate; }

    public ClaimStatus getStatus() { return status; }
    public void setStatus(ClaimStatus status) { this.status = status; }

    public CategoryType getCategory() { return category; }
    public void setCategory(CategoryType category) { this.category = category; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDate getSubmissionDate() { return submissionDate; }
    public void setSubmissionDate(LocalDate submissionDate) { this.submissionDate = submissionDate; }

    public String getApprovedBy() { return approvedBy; }
    public void setApprovedBy(String approvedBy) { this.approvedBy = approvedBy; }
}
