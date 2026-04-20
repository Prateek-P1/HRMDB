package com.hrms.db.repositories.Expense_Management;

import com.pesu.expensesubsystem.entity.DepartmentBudget;
import java.math.BigDecimal;
import java.util.Optional;

public interface BudgetRepository {
    Optional<DepartmentBudget> findByDepartment(String departmentName);
    DepartmentBudget save(DepartmentBudget budget);
    void updateBudget(String departmentName, BigDecimal newSpent);
    boolean existsByDepartment(String departmentName);
}
