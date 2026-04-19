package com.hrms.db.repositories.Expense_Management;

import com.pesu.expensesubsystem.entity.AuditLog;
import com.pesu.expensesubsystem.entity.DepartmentBudget;
import com.pesu.expensesubsystem.entity.ExpenseClaim;
import com.pesu.expensesubsystem.entity.Receipt;
import com.pesu.expensesubsystem.enums.ActionType;
import com.pesu.expensesubsystem.enums.CategoryType;
import com.pesu.expensesubsystem.enums.ClaimStatus;
import org.hibernate.Session;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

final class ExpenseRepoMapper {

    private ExpenseRepoMapper() {
    }

    static Double toDouble(BigDecimal v) {
        return v == null ? null : v.doubleValue();
    }

    static Double toDoubleOrZero(BigDecimal v) {
        return v == null ? 0.0 : v.doubleValue();
    }

    static BigDecimal toBigDecimal(Double v) {
        return v == null ? null : BigDecimal.valueOf(v);
    }

    static com.pesu.expensesubsystem.entity.Employee toDtoEmployee(com.hrms.db.entities.Employee e) {
        if (e == null) return null;
        com.pesu.expensesubsystem.entity.Employee dto = new com.pesu.expensesubsystem.entity.Employee();
        dto.setEmployeeId(e.getEmpId());
        dto.setName(e.getName());
        dto.setDepartment(e.getDepartment());
        dto.setManagerId(e.getManager() != null ? e.getManager().getEmpId() : null);
        return dto;
    }

    static com.hrms.db.entities.Employee toEntityEmployee(com.pesu.expensesubsystem.entity.Employee dto, Session session) {
        String id = dto.getEmployeeId();
        if (id == null) throw new IllegalArgumentException("employeeId is null");

        com.hrms.db.entities.Employee entity = session.get(com.hrms.db.entities.Employee.class, id);
        if (entity == null) {
            entity = new com.hrms.db.entities.Employee();
            entity.setEmpId(id);
        }

        String name = dto.getName();
        if (name == null || name.isBlank()) name = (entity.getName() != null ? entity.getName() : id);
        entity.setName(name);
        entity.setDepartment(dto.getDepartment());

        if (dto.getManagerId() != null) {
            com.hrms.db.entities.Employee mgr = session.get(com.hrms.db.entities.Employee.class, dto.getManagerId());
            entity.setManager(mgr);
        } else {
            entity.setManager(null);
        }

        return entity;
    }

    static ExpenseClaim toDtoClaim(com.hrms.db.entities.ExpenseClaim c) {
        if (c == null) return null;
        ExpenseClaim dto = new ExpenseClaim();
        dto.setClaimId(parseLongOrNull(c.getClaimId()));
        dto.setEmployeeId(c.getEmployee() != null ? c.getEmployee().getEmpId() : null);
        dto.setAmount(toBigDecimal(c.getAmount()));
        dto.setExpenseDate(c.getExpenseDate());
        dto.setStatus(parseEnumOrNull(ClaimStatus.class, c.getStatus()));
        dto.setCategory(parseEnumOrNull(CategoryType.class, c.getCategoryId()));
        dto.setApprovedBy(c.getApprovedBy() != null ? c.getApprovedBy().getEmpId() : null);
        return dto;
    }

    static com.hrms.db.entities.ExpenseClaim toEntityClaim(ExpenseClaim dto, Session session) {
        Long claimId = dto.getClaimId();
        if (claimId == null) {
            claimId = System.currentTimeMillis();
            dto.setClaimId(claimId);
        }

        String id = String.valueOf(claimId);
        com.hrms.db.entities.ExpenseClaim entity = session.get(com.hrms.db.entities.ExpenseClaim.class, id);
        if (entity == null) {
            entity = new com.hrms.db.entities.ExpenseClaim();
            entity.setClaimId(id);
        }

        if (dto.getEmployeeId() == null) throw new IllegalArgumentException("employeeId is null");
        com.hrms.db.entities.Employee emp = session.get(com.hrms.db.entities.Employee.class, dto.getEmployeeId());
        if (emp == null) throw new IllegalArgumentException("Employee not found: " + dto.getEmployeeId());
        entity.setEmployee(emp);

        entity.setAmount(toDoubleOrZero(dto.getAmount()));
        entity.setExpenseDate(dto.getExpenseDate() != null ? dto.getExpenseDate() : LocalDate.now());

        ClaimStatus status = dto.getStatus();
        entity.setStatus(status != null ? status.name() : "PENDING");

        CategoryType category = dto.getCategory();
        entity.setCategoryId(category != null ? category.name() : null);

        if (dto.getApprovedBy() != null) {
            com.hrms.db.entities.Employee approver = session.get(com.hrms.db.entities.Employee.class, dto.getApprovedBy());
            entity.setApprovedBy(approver);
        } else {
            entity.setApprovedBy(null);
        }

        return entity;
    }

    static DepartmentBudget toDtoBudget(com.hrms.db.entities.DepartmentBudget b) {
        if (b == null) return null;
        DepartmentBudget dto = new DepartmentBudget();
        dto.setDepartmentName(b.getDepartmentName());
        dto.setBudgetLimit(toBigDecimal(b.getBudgetLimit()));
        dto.setCurrentSpent(toBigDecimal(b.getCurrentSpent()));
        return dto;
    }

    static com.hrms.db.entities.DepartmentBudget findBudgetEntity(Session session, String departmentName) {
        return session.createQuery(
                        "FROM DepartmentBudget b WHERE b.departmentName = :d",
                        com.hrms.db.entities.DepartmentBudget.class)
                .setParameter("d", departmentName)
                .uniqueResult();
    }

    static Receipt toDtoReceipt(com.hrms.db.entities.Receipt r) {
        if (r == null) return null;
        Receipt dto = new Receipt();
        dto.setReceiptId(r.getReceiptId());
        dto.setClaimId(parseLongOrNull(r.getClaimId()));
        dto.setFilePath(r.getFilePath());
        dto.setFileName(r.getFileName());
        dto.setUploadDate(r.getUploadDate());
        return dto;
    }

    static com.hrms.db.entities.ExpenseAuditLog toEntityAuditLog(AuditLog dto, Session session) {
        com.hrms.db.entities.ExpenseAuditLog entity = new com.hrms.db.entities.ExpenseAuditLog();
        entity.setClaimId(dto.getClaimId() != null ? String.valueOf(dto.getClaimId()) : null);

        if (dto.getEmployeeId() != null) {
            com.hrms.db.entities.Employee emp = session.get(com.hrms.db.entities.Employee.class, dto.getEmployeeId());
            entity.setEmployee(emp);
        }

        entity.setActionType(dto.getAction() != null ? dto.getAction().name() : null);
        entity.setDetails(dto.getDetails());
        entity.setTs(dto.getTimestamp());
        entity.setExceptionName(dto.getExceptionName());
        return entity;
    }

    static AuditLog toDtoAuditLog(com.hrms.db.entities.ExpenseAuditLog a) {
        if (a == null) return null;
        AuditLog dto = new AuditLog();
        dto.setLogId(a.getLogId());
        dto.setClaimId(parseLongOrNull(a.getClaimId()));
        dto.setEmployeeId(a.getEmployee() != null ? a.getEmployee().getEmpId() : null);
        dto.setAction(parseEnumOrNull(ActionType.class, a.getActionType()));
        dto.setDetails(a.getDetails());
        dto.setTimestamp(a.getTs());
        dto.setExceptionName(a.getExceptionName());
        return dto;
    }

    static List<AuditLog> mapAuditLogs(List<com.hrms.db.entities.ExpenseAuditLog> entities) {
        if (entities == null || entities.isEmpty()) return Collections.emptyList();
        List<AuditLog> out = new ArrayList<>(entities.size());
        for (com.hrms.db.entities.ExpenseAuditLog a : entities) out.add(toDtoAuditLog(a));
        return out;
    }

    static Long parseLongOrNull(String v) {
        if (v == null) return null;
        try {
            return Long.parseLong(v);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    static <T extends Enum<T>> T parseEnumOrNull(Class<T> type, String value) {
        if (value == null) return null;
        try {
            return Enum.valueOf(type, value);
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }
}
