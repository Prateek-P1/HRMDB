package com.hrms.db.repositories.Expense_Management;

import com.hrms.db.config.DatabaseConnection;
import org.hibernate.Session;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;

public class ExpenseLeaveRepositoryImpl implements LeaveRepository {

    @Override
    public boolean isEmployeeOnLeave(String employeeId, LocalDate date) {
        if (employeeId == null || date == null) return false;
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            Long count = session.createQuery(
                            "SELECT COUNT(l) FROM LeaveRecord l WHERE l.employee.empId = :id AND l.status = 'APPROVED' AND l.startDate <= :d AND l.endDate >= :d",
                            Long.class)
                    .setParameter("id", employeeId)
                    .setParameter("d", date)
                    .uniqueResult();
            return count != null && count > 0;
        } catch (Exception ex) {
            return false;
        }
    }

    @Override
    public List<LocalDate> getLeaveDates(String employeeId, LocalDate start, LocalDate end) {
        if (employeeId == null || start == null || end == null) return Collections.emptyList();
        if (end.isBefore(start)) return Collections.emptyList();

        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            List<com.hrms.db.entities.LeaveRecord> records = session.createQuery(
                            "FROM LeaveRecord l WHERE l.employee.empId = :id AND l.status = 'APPROVED' AND l.endDate >= :s AND l.startDate <= :e",
                            com.hrms.db.entities.LeaveRecord.class)
                    .setParameter("id", employeeId)
                    .setParameter("s", start)
                    .setParameter("e", end)
                    .getResultList();

            LinkedHashSet<LocalDate> dates = new LinkedHashSet<>();
            for (com.hrms.db.entities.LeaveRecord r : records) {
                LocalDate from = r.getStartDate().isAfter(start) ? r.getStartDate() : start;
                LocalDate to = r.getEndDate().isBefore(end) ? r.getEndDate() : end;
                for (LocalDate d = from; !d.isAfter(to); d = d.plusDays(1)) {
                    dates.add(d);
                }
            }
            return new ArrayList<>(dates);
        } catch (Exception ex) {
            return Collections.emptyList();
        }
    }

    @Override
    public boolean hasTravelAuthorization(String employeeId, LocalDate date) {
        // No travel authorization model exists in this repository yet.
        return false;
    }
}
