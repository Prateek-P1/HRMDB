package com.hrms.db.repositories.Expense_Management;

import java.time.LocalDate;
import java.util.List;

public interface LeaveRepository {
	boolean isEmployeeOnLeave(String employeeId, LocalDate date);
	List<LocalDate> getLeaveDates(String employeeId, LocalDate start, LocalDate end);
	boolean hasTravelAuthorization(String employeeId, LocalDate date);
}