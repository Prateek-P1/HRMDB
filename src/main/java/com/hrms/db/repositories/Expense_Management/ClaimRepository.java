package com.hrms.db.repositories.Expense_Management;

import com.pesu.expensesubsystem.entity.ExpenseClaim;
import com.pesu.expensesubsystem.enums.ClaimStatus;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ClaimRepository {
    ExpenseClaim save(ExpenseClaim claim);
    Optional<ExpenseClaim> findById(Long claimId);
    List<ExpenseClaim> findByEmployeeId(String employeeId);
    List<ExpenseClaim> findByStatus(ClaimStatus status);
    List<ExpenseClaim> findAll();
    void update(ExpenseClaim claim);
    void delete(Long claimId);
    List<ExpenseClaim> findPendingClaimsOlderThan(LocalDate date);
}
