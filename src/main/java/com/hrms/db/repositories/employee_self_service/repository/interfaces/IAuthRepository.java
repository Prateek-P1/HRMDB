package com.hrms.db.repositories.employee_self_service.repository.interfaces;

public interface IAuthRepository {

    boolean validateUser(String username, String password);

    int getEmployeeId(String username);

    boolean updatePassword(int employeeId, String newPassword);

    boolean isAccountLocked(String username);
}
