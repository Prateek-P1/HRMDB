package com.hrms.db.repositories.performance.models;

public class Employee {
    private int employeeId;
    private String name;
    private String email;
    private int deptId;
    private int managerId;
    private String roleId;
    private String designation;

    public Employee() {}

    public Employee(int employeeId, String name, String email, int deptId, int managerId, String roleId, String designation) {
        this.employeeId = employeeId;
        this.name = name;
        this.email = email;
        this.deptId = deptId;
        this.managerId = managerId;
        this.roleId = roleId;
        this.designation = designation;
    }

    public int getEmployeeId() { return employeeId; }
    public void setEmployeeId(int employeeId) { this.employeeId = employeeId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public int getDeptId() { return deptId; }
    public void setDeptId(int deptId) { this.deptId = deptId; }
    public int getManagerId() { return managerId; }
    public void setManagerId(int managerId) { this.managerId = managerId; }
    public String getRoleId() { return roleId; }
    public void setRoleId(String roleId) { this.roleId = roleId; }
    public String getDesignation() { return designation; }
    public void setDesignation(String designation) { this.designation = designation; }

    @Override
    public String toString() {
        return "Employee{id=" + employeeId + ", name='" + name + "', dept=" + deptId + ", role='" + roleId + "'}";
    }
}
