hrms-database/
│
├── README.md
│
├── sql/
│   ├── schema.sql                  ← Full DB schema (run this on Supabase once)
│   └── seed_data.sql               ← Sample data for testing
│
└── src/
    └── main/
        └── java/
            └── com/hrms/db/
                │
                ├── config/
                │   ├── DatabaseConnection.java       ← Singleton JDBC connection
                │
                ├── facade/
                |   ├── HRMSDatabaseFacade.java         ← Facade pattern
                │
                ├── factory/
                │   └── RepositoryFactory.java         ← Factory: creates repo instances
                │
                ├── handlers/                          ← Chain of Responsibility
                │   ├── ErrorHandler.java              ← Abstract handler
                │   ├── ConsoleErrorLogger.java
                │   ├── DatabaseErrorLogger.java
                │   └── CriticalErrorEscalator.java
                |
                ├── logging/
                │   ├── LogHandler.java                
                │   ├── ConsoleLogHandler.java
                │   └── DatabaseLogHandler.java
                |
                │
                ├── iterators/
                │   ├── EmployeeIterator.java           ← Iterator interface
                │   └── ActiveEmployeeIterator.java
                │
                └── repositories/
                    │
                    ├── payroll/
                    │   ├── IPayrollRepository.java     ← Interface (given by Payroll team)
                    │   └── PayrollRepositoryImpl.java  ← YOUR implementation
                    │
                    ├── leave/
                    │   ├── ILeaveRepository.java       ← Interface (given by Leave team)
                    │   └── LeaveRepositoryImpl.java
                    │
                    ├── expense/
                    │   ├── IExpenseRepository.java
                    │   └── ExpenseRepositoryImpl.java
                    │
                    └── ...one folder per subsystem...


## How to add a new subsystem interface

When a team sends you their interface file (e.g. `ILeaveRepository.java`):

1. Create a new folder: `src/.../repositories/leave/`
2. Drop their interface file into it
3. Create `LeaveRepositoryImpl.java` implementing that interface
4. Add a factory method in `RepositoryFactory.java`:
   ```java
   public static ILeaveRepository createLeaveRepository() throws SQLException {
       Connection conn = DatabaseConnection.getInstance().getConnection();
       return new LeaveRepositoryImpl(conn);
   }
   ```
5. Expose it in `HRMSDatabaseFacade.java`:
   ```java
   public ILeaveRepository leave() { return leaveRepo; }
   ```

The pattern is identical every time. The only thing that changes is:
- The SQL queries inside the impl (based on which tables that subsystem needs)
- The DTO classes they define

## pom.xml dependency needed

```xml
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <version>42.7.3</version>
</dependency>
```

## Environment variables (set these — never hardcode!)

```
DB_URL=jdbc:postgresql://db.xxxx.supabase.co:5432/postgres
DB_USER=postgres
DB_PASS=your_supabase_password
```