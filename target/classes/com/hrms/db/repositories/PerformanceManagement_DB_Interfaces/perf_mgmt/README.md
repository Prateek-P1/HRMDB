# Performance Management Subsystem — DB Interface Package
## OOAD Project

---

## Folder Structure

```
perf_mgmt/
├── src/
│   ├── models/                  ← Data model classes (do not modify)
│   │   ├── Employee.java
│   │   ├── Goal.java
│   │   ├── KPI.java
│   │   ├── KPIRecord.java
│   │   ├── Appraisal.java
│   │   ├── Feedback.java
│   │   ├── FeedbackRequest.java
│   │   ├── Skill.java
│   │   ├── SkillProfile.java
│   │   ├── SkillGap.java
│   │   ├── SkillGapSummary.java
│   │   ├── PerformanceCycle.java
│   │   ├── Notification.java
│   │   ├── Reminder.java
│   │   ├── DeptReport.java
│   │   ├── ProgressReport.java
│   │   └── AuditLog.java
│   │
│   ├── interfaces/              ← Interface contracts (do NOT modify)
│   │   ├── IEmployeeRepository.java
│   │   ├── IGoalRepository.java
│   │   ├── IKPIRepository.java
│   │   ├── IAppraisalRepository.java
│   │   ├── IFeedbackRepository.java
│   │   ├── ISkillGapRepository.java
│   │   ├── IPerformanceCycleRepository.java
│   │   ├── INotificationRepository.java
│   │   ├── IReportRepository.java
│   │   └── IAuditLogRepository.java
│   │
│   ├── impl/
│   │   └── StubImplementations.java   ← DB TEAM: Fill in the TODOs here
│   │
│   └── main/
│       └── PerformanceManagementDemo.java   ← Run this to test everything
│
├── compile.sh      ← One-click compile script
└── README.md
```

---

## How to Compile and Run

### Option 1 — Use the shell script (easiest)
```bash
chmod +x compile.sh
./compile.sh
```

### Option 2 — Manual
```bash
# Step 1: Compile everything
javac -d out \
  src/models/*.java \
  src/interfaces/*.java \
  src/impl/StubImplementations.java \
  src/main/PerformanceManagementDemo.java

# Step 2: Run the demo
java -cp out main.PerformanceManagementDemo
```

You should see output like:
```
--- 1. IEmployeeRepository ---
  ✓ getEmployeeById(101) -> Employee{id=101 ...}
  ✓ getEmployeesByDept(1) -> 2 employees
  ...
All interface tests completed successfully!
```

---

## Instructions for DB Team

1. **Do NOT modify** anything in `src/interfaces/` or `src/models/`.
2. Open `src/impl/StubImplementations.java`.
3. Each class (e.g. `EmployeeRepository`, `GoalRepository`, etc.) has methods with `// TODO` comments.
4. Replace each TODO block with your actual JDBC / JPA / Hibernate DB logic.
5. Run `PerformanceManagementDemo` after implementing to verify all assertions pass.
6. Hand back the updated `StubImplementations.java` to the Performance Management team.

---

## Interface Summary

| # | Interface | Component |
|---|-----------|-----------|
| 1 | IEmployeeRepository | Employee Profile |
| 2 | IGoalRepository | Goal Setting & Tracking |
| 3 | IKPIRepository | KPI Management |
| 4 | IAppraisalRepository | Appraisal & Review |
| 5 | IFeedbackRepository | 360° Feedback |
| 6 | ISkillGapRepository | Skill Gap Analysis |
| 7 | IPerformanceCycleRepository | Cycle Configuration |
| 8 | INotificationRepository | Notifications & Reminders |
| 9 | IReportRepository | Analytics & Reporting |
| 10 | IAuditLogRepository | Audit & Compliance |
