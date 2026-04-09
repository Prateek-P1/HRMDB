// ================================================================
// HRMS Database Team — Core Implementation
// Design Patterns: Factory (Creational), Facade (Structural),
//                  Chain of Responsibility (Behavioral), Iterator
// Principles: SOLID, GRASP
// ================================================================

import java.sql.*;
import java.util.*;


// ================================================================
// 1. DATABASE CONNECTION (Singleton pattern for connection pool)
//    GRASP: Information Expert — this class owns connection logic
//    SOLID: Single Responsibility — only manages connections
// ================================================================
class DatabaseConnection {
    private static DatabaseConnection instance;
    private Connection connection;

    // Use environment variables — never hardcode credentials!
    private static final String URL = System.getenv("DB_URL");       // e.g. jdbc:postgresql://db.xxx.supabase.co:5432/postgres
    private static final String USER = System.getenv("DB_USER");     // e.g. postgres
    private static final String PASSWORD = System.getenv("DB_PASS");

    private DatabaseConnection() throws SQLException {
        this.connection = DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static synchronized DatabaseConnection getInstance() throws SQLException {
        if (instance == null || instance.connection.isClosed()) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }
}


// ================================================================
// 2. CHAIN OF RESPONSIBILITY — Error Handling Pipeline
//    SOLID: Open/Closed — add new handlers without modifying existing ones
//    GRASP: Protected Variations — shields callers from error handling details
// ================================================================

abstract class ErrorHandler {
    protected ErrorHandler next;

    public ErrorHandler setNext(ErrorHandler next) {
        this.next = next;
        return next;
    }

    public abstract void handle(String batchID, String empID, String errorMsg, Connection conn);
}

// Handler 1: Log to database
class DatabaseErrorLogger extends ErrorHandler {
    @Override
    public void handle(String batchID, String empID, String errorMsg, Connection conn) {
        try {
            String sql = "INSERT INTO payroll_audit_log(batch_id, emp_id, action_type, error_msg) VALUES (?,?,?,?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, batchID);
            ps.setString(2, empID);
            ps.setString(3, "ERROR");
            ps.setString(4, errorMsg);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[DB Logger] Failed to log error: " + e.getMessage());
        }
        if (next != null) next.handle(batchID, empID, errorMsg, conn);
    }
}

// Handler 2: Print to console (for development)
class ConsoleErrorLogger extends ErrorHandler {
    @Override
    public void handle(String batchID, String empID, String errorMsg, Connection conn) {
        System.err.printf("[ERROR] Batch=%s, Emp=%s | %s%n", batchID, empID, errorMsg);
        if (next != null) next.handle(batchID, empID, errorMsg, conn);
    }
}

// Handler 3: Critical error escalation (placeholder — hook to email/Slack etc.)
class CriticalErrorEscalator extends ErrorHandler {
    private static final String[] CRITICAL_KEYWORDS = {"NullPointerException", "Connection", "timeout"};

    @Override
    public void handle(String batchID, String empID, String errorMsg, Connection conn) {
        for (String keyword : CRITICAL_KEYWORDS) {
            if (errorMsg.contains(keyword)) {
                System.err.println("[CRITICAL] Escalating: " + errorMsg);
                // TODO: integrate email/webhook alert here
                return;
            }
        }
        if (next != null) next.handle(batchID, empID, errorMsg, conn);
    }
}


// ================================================================
// 3. ITERATOR — Active Employee Iterator
//    SOLID: Interface Segregation — callers only use what they need
//    GRASP: Low Coupling — payroll engine doesn't know about SQL
// ================================================================

interface EmployeeIterator extends Iterator<String> {
    // Inherits hasNext() and next() from Iterator<String>
}

class ActiveEmployeeIterator implements EmployeeIterator {
    private final List<String> ids;
    private int index = 0;

    public ActiveEmployeeIterator(Connection conn) throws SQLException {
        ids = new ArrayList<>();
        String sql = "SELECT emp_id FROM employees WHERE employment_status = 'ACTIVE'";
        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            ids.add(rs.getString("emp_id"));
        }
    }

    @Override public boolean hasNext() { return index < ids.size(); }
    @Override public String next() { return ids.get(index++); }
}


// ================================================================
// 4. PAYROLL REPOSITORY IMPLEMENTATION
//    This is what the Payroll team's interface requires.
//    SOLID: Dependency Inversion — implements IPayrollRepository
//    GRASP: Controller — handles all payroll DB operations
// ================================================================

class PayrollRepositoryImpl implements IPayrollRepository {

    private final Connection conn;
    private final ErrorHandler errorChain;

    public PayrollRepositoryImpl(Connection conn) {
        this.conn = conn;

        // Build the error handling chain: Console → Database → Escalator
        ErrorHandler console = new ConsoleErrorLogger();
        ErrorHandler dbLogger = new DatabaseErrorLogger();
        ErrorHandler escalator = new CriticalErrorEscalator();
        console.setNext(dbLogger).setNext(escalator);
        this.errorChain = console;
    }

    // -----------------------------------------------------------
    // READ: Build the full PayrollDataPackage for one employee
    // -----------------------------------------------------------
    @Override
    public PayrollDataPackage fetchEmployeeData(String empID, String payPeriod) {
        try {
            PayrollDataPackage pkg = new PayrollDataPackage();
            pkg.payPeriod = payPeriod;

            // Fetch employee core data
            String empSQL = "SELECT * FROM employees WHERE emp_id = ?";
            PreparedStatement ps = conn.prepareStatement(empSQL);
            ps.setString(1, empID);
            ResultSet rs = ps.executeQuery();

            if (!rs.next()) return null;

            EmployeeDTO emp = new EmployeeDTO();
            emp.empID           = rs.getString("emp_id");
            emp.name            = rs.getString("name");
            emp.department      = rs.getString("department");
            emp.gradeLevel      = rs.getString("grade_level");
            emp.basicPay        = rs.getDouble("basic_pay");
            emp.yearsOfService  = rs.getInt("years_of_service");
            pkg.employee = emp;

            TaxContextDTO tax = new TaxContextDTO();
            tax.countryCode     = rs.getString("country_code");
            tax.currencyCode    = rs.getString("currency_code");
            tax.taxRegime       = rs.getString("tax_regime");
            tax.stateName       = rs.getString("state_name");
            tax.filingStatus    = rs.getString("filing_status");
            tax.taxCode         = rs.getString("tax_code");
            tax.nationalIDNumber = rs.getString("national_id_number");
            pkg.tax = tax;

            // Fetch attendance for the pay period
            String attSQL = "SELECT * FROM attendance WHERE emp_id = ? AND pay_period = ?";
            ps = conn.prepareStatement(attSQL);
            ps.setString(1, empID);
            ps.setString(2, payPeriod);
            rs = ps.executeQuery();

            AttendanceDTO att = new AttendanceDTO();
            if (rs.next()) {
                att.workingDaysInMonth  = rs.getInt("working_days_in_month");
                att.leaveWithPay        = rs.getInt("leave_with_pay");
                att.leaveWithoutPay     = rs.getInt("leave_without_pay");
                att.hoursWorked         = rs.getDouble("hours_worked");
                att.overtimeHours       = rs.getDouble("overtime_hours");
            }
            pkg.attendance = att;

            // Fetch financials for the pay period
            String finSQL = "SELECT * FROM financials WHERE emp_id = ? AND pay_period = ?";
            ps = conn.prepareStatement(finSQL);
            ps.setString(1, empID);
            ps.setString(2, payPeriod);
            rs = ps.executeQuery();

            FinancialsDTO fin = new FinancialsDTO();
            if (rs.next()) {
                fin.pendingClaims           = rs.getDouble("pending_claims");
                fin.approvedReimbursement   = rs.getDouble("approved_reimbursement");
                fin.insurancePremium        = rs.getDouble("insurance_premium");
                fin.declaredInvestments     = rs.getDouble("declared_investments");
            }
            pkg.financials = fin;

            return pkg;

        } catch (SQLException e) {
            logProcessingError("FETCH", empID, e.getMessage());
            return null;
        }
    }

    // -----------------------------------------------------------
    // READ: Get all active employee IDs — uses Iterator internally
    // -----------------------------------------------------------
    @Override
    public List<String> getAllActiveEmployeeIDs() {
        try {
            List<String> ids = new ArrayList<>();
            EmployeeIterator iterator = new ActiveEmployeeIterator(conn);
            while (iterator.hasNext()) {
                ids.add(iterator.next());
            }
            return ids;
        } catch (SQLException e) {
            logProcessingError("BATCH", "ALL", e.getMessage());
            return Collections.emptyList();
        }
    }

    // -----------------------------------------------------------
    // WRITE: Save computed payroll result back to DB
    // -----------------------------------------------------------
    @Override
    public boolean savePayrollResult(String batchID, PayrollResultDTO result) {
        String sql = """
            INSERT INTO payroll_results
                (emp_id, batch_id, pay_period, final_gross_pay, final_net_pay,
                 penalty_amount, pf_amount, monthly_tds_amount, payout_amount)
            VALUES (?,?,?,?,?,?,?,?,?)
            ON CONFLICT (emp_id, batch_id, pay_period) DO UPDATE SET
                final_gross_pay = EXCLUDED.final_gross_pay,
                final_net_pay   = EXCLUDED.final_net_pay,
                payout_amount   = EXCLUDED.payout_amount
            """;
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, result.empID);
            ps.setString(2, batchID);
            ps.setString(3, ""); // pay_period — payroll team should include this in the DTO; use empty for now
            ps.setDouble(4, result.finalGrossPay);
            ps.setDouble(5, result.finalNetPay);
            ps.setDouble(6, result.penaltyAmount);
            ps.setDouble(7, result.pfAmount);
            ps.setDouble(8, result.taxDeducted);
            ps.setDouble(9, result.payoutAmount);
            ps.executeUpdate();

            // Log successful save
            logAction(batchID, result.empID, "SAVE_SUCCESS");
            return true;

        } catch (SQLException e) {
            logProcessingError(batchID, result.empID, e.getMessage());
            return false;
        }
    }

    // -----------------------------------------------------------
    // EXCEPTION HANDLING: Routes through the CoR chain
    // -----------------------------------------------------------
    @Override
    public void logProcessingError(String batchID, String empID, String errorMsg) {
        errorChain.handle(batchID, empID, errorMsg, conn);
    }

    // Helper: log a non-error action
    private void logAction(String batchID, String empID, String actionType) throws SQLException {
        String sql = "INSERT INTO payroll_audit_log(batch_id, emp_id, action_type) VALUES (?,?,?)";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, batchID);
        ps.setString(2, empID);
        ps.setString(3, actionType);
        ps.executeUpdate();
    }
}


// ================================================================
// 5. FACTORY — Creates the right repository per subsystem
//    SOLID: Open/Closed — add new subsystems without changing factory logic
//    GRASP: Creator — factory is responsible for object creation
// ================================================================

interface Repository {}  // Marker interface all repos implement

class RepositoryFactory {

    public enum SubSystem {
        PAYROLL, LEAVE, EXPENSE, RECRUITMENT, TIME_TRACKING
    }

    // SOLID: Dependency Inversion — returns abstraction, not concrete class
    public static IPayrollRepository createPayrollRepository() throws SQLException {
        Connection conn = DatabaseConnection.getInstance().getConnection();
        return new PayrollRepositoryImpl(conn);
    }

    // Add more as other teams send you their interfaces:
    // public static ILeaveRepository createLeaveRepository() throws SQLException { ... }
    // public static IExpenseRepository createExpenseRepository() throws SQLException { ... }
}


// ================================================================
// 6. FACADE — Single entry point for all subsystems
//    SOLID: Single Responsibility — hides all DB complexity behind one class
//    GRASP: Controller — top-level coordinator, simplifies subsystem access
// ================================================================

class HRMSDatabaseFacade {

    private static HRMSDatabaseFacade instance;
    private final IPayrollRepository payrollRepo;

    private HRMSDatabaseFacade() throws SQLException {
        this.payrollRepo = RepositoryFactory.createPayrollRepository();
        // Add more repos here as teams send interfaces:
        // this.leaveRepo = RepositoryFactory.createLeaveRepository();
    }

    // Singleton — only one facade instance exists
    public static synchronized HRMSDatabaseFacade getInstance() throws SQLException {
        if (instance == null) {
            instance = new HRMSDatabaseFacade();
        }
        return instance;
    }

    // Expose payroll operations
    public IPayrollRepository payroll() { return payrollRepo; }

    // Future: expose other subsystems
    // public ILeaveRepository leave() { return leaveRepo; }
}


// ================================================================
// 7. USAGE EXAMPLE — How the Payroll team calls your code
// ================================================================

class UsageExample {
    public static void main(String[] args) throws SQLException {

        // Payroll team gets the facade (or repo directly)
        HRMSDatabaseFacade db = HRMSDatabaseFacade.getInstance();

        // Batch run: get all active employees
        List<String> employeeIDs = db.payroll().getAllActiveEmployeeIDs();

        for (String empID : employeeIDs) {
            // Fetch all data for this employee
            PayrollDataPackage data = db.payroll().fetchEmployeeData(empID, "2024-06");

            if (data == null) {
                db.payroll().logProcessingError("BATCH-001", empID, "No data found");
                continue;
            }

            // ... Payroll team does their calculation here ...
            PayrollResultDTO result = new PayrollResultDTO();
            result.empID = empID;
            result.finalGrossPay = data.employee.basicPay * 1.2; // example
            result.finalNetPay   = result.finalGrossPay * 0.85;
            result.payoutAmount  = result.finalNetPay;

            // Write result back to DB
            boolean saved = db.payroll().savePayrollResult("BATCH-001", result);
            System.out.println("Saved result for " + empID + ": " + saved);
        }
    }
}
