package com.pesu.expensesubsystem.entity;

/**
 * Expense subsystem view of an employee.
 *
 * This is a DTO used by the Expense Management repository interfaces.
 */
public class Employee {

    private String employeeId;
    private String name;
    private String department;
    private String managerId;

    public Employee() {
    }

    public Employee(String employeeId, String name, String department, String managerId) {
        this.employeeId = employeeId;
        this.name = name;
        this.department = department;
        this.managerId = managerId;
    }

    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getManagerId() { return managerId; }
    public void setManagerId(String managerId) { this.managerId = managerId; }
}
