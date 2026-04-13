package com.hrms.db.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Core employee table — every subsystem reads this.
 * Maps to the central 'employees' table.
 */
@Entity
@Table(name = "employees")
public class Employee {

    @Id
    @Column(name = "emp_id", length = 20)
    private String empId;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "email", length = 150)
    private String email;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "address", length = 500)
    private String address;

    @Column(name = "department", length = 100)
    private String department;

    @Column(name = "designation", length = 100)
    private String designation;

    @Column(name = "grade_level", length = 20)
    private String gradeLevel;

    @Column(name = "basic_pay")
    private Double basicPay;

    @Column(name = "years_of_service")
    private Integer yearsOfService = 0;

    @Column(name = "role", length = 100)
    private String role;

    @Column(name = "gender", length = 10)
    private String gender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id")
    private Employee manager;

    @Column(name = "employment_status", length = 20)
    private String employmentStatus = "ACTIVE";

    @Column(name = "date_of_joining")
    private java.time.LocalDate dateOfJoining;

    @Column(name = "salary_band", length = 20)
    private String salaryBand;

    @Column(name = "employment_type", length = 30)
    private String employmentType;

    @Column(name = "office_location", length = 100)
    private String officeLocation;

    // --- Tax / Multi-country fields (used by Payroll) ---

    @Column(name = "country_code", length = 5)
    private String countryCode;

    @Column(name = "currency_code", length = 5)
    private String currencyCode;

    @Column(name = "tax_regime", length = 50)
    private String taxRegime;

    @Column(name = "state_name", length = 100)
    private String stateName;

    @Column(name = "filing_status", length = 30)
    private String filingStatus;

    @Column(name = "tax_code", length = 30)
    private String taxCode;

    @Column(name = "national_id_number", length = 50)
    private String nationalIdNumber;

    // --- Attrition fields ---

    @Column(name = "attendance_rate")
    private Double attendanceRate;

    @Column(name = "performance_score")
    private Double performanceScore;

    @Column(name = "months_since_promotion")
    private Integer monthsSincePromotion;

    @Column(name = "tenure_years")
    private Double tenureYears;

    // --- Timestamps ---

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // --- Relationships ---

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Attendance> attendanceRecords;

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<LeaveRecord> leaveRecords;

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ExpenseClaim> expenseClaims;

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PayrollResult> payrollResults;

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TimeEntry> timeEntries;

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Document> documents;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // --- Getters & Setters ---

    public String getEmpId() { return empId; }
    public void setEmpId(String empId) { this.empId = empId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getDesignation() { return designation; }
    public void setDesignation(String designation) { this.designation = designation; }

    public String getGradeLevel() { return gradeLevel; }
    public void setGradeLevel(String gradeLevel) { this.gradeLevel = gradeLevel; }

    public Double getBasicPay() { return basicPay; }
    public void setBasicPay(Double basicPay) { this.basicPay = basicPay; }

    public Integer getYearsOfService() { return yearsOfService; }
    public void setYearsOfService(Integer yearsOfService) { this.yearsOfService = yearsOfService; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public Employee getManager() { return manager; }
    public void setManager(Employee manager) { this.manager = manager; }

    public String getEmploymentStatus() { return employmentStatus; }
    public void setEmploymentStatus(String employmentStatus) { this.employmentStatus = employmentStatus; }

    public java.time.LocalDate getDateOfJoining() { return dateOfJoining; }
    public void setDateOfJoining(java.time.LocalDate dateOfJoining) { this.dateOfJoining = dateOfJoining; }

    public String getSalaryBand() { return salaryBand; }
    public void setSalaryBand(String salaryBand) { this.salaryBand = salaryBand; }

    public String getEmploymentType() { return employmentType; }
    public void setEmploymentType(String employmentType) { this.employmentType = employmentType; }

    public String getOfficeLocation() { return officeLocation; }
    public void setOfficeLocation(String officeLocation) { this.officeLocation = officeLocation; }

    public String getCountryCode() { return countryCode; }
    public void setCountryCode(String countryCode) { this.countryCode = countryCode; }

    public String getCurrencyCode() { return currencyCode; }
    public void setCurrencyCode(String currencyCode) { this.currencyCode = currencyCode; }

    public String getTaxRegime() { return taxRegime; }
    public void setTaxRegime(String taxRegime) { this.taxRegime = taxRegime; }

    public String getStateName() { return stateName; }
    public void setStateName(String stateName) { this.stateName = stateName; }

    public String getFilingStatus() { return filingStatus; }
    public void setFilingStatus(String filingStatus) { this.filingStatus = filingStatus; }

    public String getTaxCode() { return taxCode; }
    public void setTaxCode(String taxCode) { this.taxCode = taxCode; }

    public String getNationalIdNumber() { return nationalIdNumber; }
    public void setNationalIdNumber(String nationalIdNumber) { this.nationalIdNumber = nationalIdNumber; }

    public Double getAttendanceRate() { return attendanceRate; }
    public void setAttendanceRate(Double attendanceRate) { this.attendanceRate = attendanceRate; }

    public Double getPerformanceScore() { return performanceScore; }
    public void setPerformanceScore(Double performanceScore) { this.performanceScore = performanceScore; }

    public Integer getMonthsSincePromotion() { return monthsSincePromotion; }
    public void setMonthsSincePromotion(Integer monthsSincePromotion) { this.monthsSincePromotion = monthsSincePromotion; }

    public Double getTenureYears() { return tenureYears; }
    public void setTenureYears(Double tenureYears) { this.tenureYears = tenureYears; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    public List<Attendance> getAttendanceRecords() { return attendanceRecords; }
    public void setAttendanceRecords(List<Attendance> attendanceRecords) { this.attendanceRecords = attendanceRecords; }

    public List<LeaveRecord> getLeaveRecords() { return leaveRecords; }
    public void setLeaveRecords(List<LeaveRecord> leaveRecords) { this.leaveRecords = leaveRecords; }

    public List<ExpenseClaim> getExpenseClaims() { return expenseClaims; }
    public void setExpenseClaims(List<ExpenseClaim> expenseClaims) { this.expenseClaims = expenseClaims; }

    public List<PayrollResult> getPayrollResults() { return payrollResults; }
    public void setPayrollResults(List<PayrollResult> payrollResults) { this.payrollResults = payrollResults; }

    public List<TimeEntry> getTimeEntries() { return timeEntries; }
    public void setTimeEntries(List<TimeEntry> timeEntries) { this.timeEntries = timeEntries; }

    public List<Document> getDocuments() { return documents; }
    public void setDocuments(List<Document> documents) { this.documents = documents; }
}
