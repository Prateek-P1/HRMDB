package com.hrms.db.factory;

import com.hrms.db.repositories.attrition.AttritionRepositoryImpl;
import com.hrms.db.repositories.attrition.IAttritionRepository;
import com.hrms.db.repositories.Customization_team.*;
import com.hrms.db.repositories.docu_management.AuditRepository;
import com.hrms.db.repositories.docu_management.AuditRepositoryImpl;
import com.hrms.db.repositories.docu_management.DocumentRepository;
import com.hrms.db.repositories.docu_management.DocumentRepositoryImpl;
import com.hrms.db.repositories.benefits.*;
import com.hrms.db.repositories.leave.*;
import com.hrms.db.repositories.security.*;
import com.hrms.db.repositories.onboarding.IOnboardingRepository;
import com.hrms.db.repositories.onboarding.OnboardingRepositoryImpl;
import com.hrms.db.repositories.payroll.IPayrollRepository;
import com.hrms.db.repositories.payroll.PayrollRepositoryImpl;
import com.hrms.db.repositories.performance.PerformanceRepositoryImpl;
import com.hrms.db.repositories.performance.interfaces.*;
import com.hrms.db.repositories.Expense_Management.ExpenseAuditRepositoryImpl;
import com.hrms.db.repositories.Expense_Management.ExpenseBudgetRepositoryImpl;
import com.hrms.db.repositories.Expense_Management.ExpenseClaimRepositoryImpl;
import com.hrms.db.repositories.Expense_Management.ExpenseEmployeeRepositoryImpl;
import com.hrms.db.repositories.Expense_Management.ExpenseLeaveRepositoryImpl;
import com.hrms.db.repositories.Expense_Management.ExpenseReceiptRepositoryImpl;
import com.hrms.db.repositories.multicountry.IMultiCountryRepository;
import com.hrms.db.repositories.multicountry.MultiCountryRepositoryImpl;
import com.hrms.db.repositories.timetracking.TimeTrackingAttendanceRepositoryImpl;
import com.hrms.db.repositories.timetracking.TimeTrackingBreakRepositoryImpl;
import com.hrms.db.repositories.timetracking.TimeTrackingEmployeeRepositoryImpl;
import com.hrms.db.repositories.timetracking.TimeTrackingNotificationRepositoryImpl;
import com.hrms.db.repositories.timetracking.TimeTrackingOvertimeRepositoryImpl;
import com.hrms.db.repositories.timetracking.TimeTrackingPolicyRepositoryImpl;
import com.hrms.db.repositories.timetracking.TimeTrackingReportRepositoryImpl;
import com.hrms.db.repositories.succession.DevelopmentPlanRepositoryImpl;
import com.hrms.db.repositories.succession.ExternalHireRequestRepositoryImpl;
import com.hrms.db.repositories.succession.PlanTaskRepositoryImpl;
import com.hrms.db.repositories.succession.ReadinessScoreRepositoryImpl;
import com.hrms.db.repositories.succession.RiskLogRepositoryImpl;
import com.hrms.db.repositories.succession.RoleRepositoryImpl;
import com.hrms.db.repositories.succession.SuccessionAuditLogRepositoryImpl;
import com.hrms.db.repositories.succession.SuccessionNotificationRepositoryImpl;
import com.hrms.db.repositories.succession.SuccessionPoolRepositoryImpl;
import com.hrms.db.repositories.succession.SuccessorAssignmentRepositoryImpl;
import com.hrms.db.repositories.hranalytics.EmployeeServiceImpl;

/**
 * RepositoryFactory — Singleton factory providing access to all DB repositories.
 *
 * Other subsystem teams use this via HRMSDatabaseFacade.getRepositories() to obtain
 * their interface references. Implementation classes are never exposed directly.
 *
 * Uses lazy initialization — repositories are created only when first requested.
 */
public class RepositoryFactory {

    private static RepositoryFactory instance;

    // Subsystem Repository Instances
    private IPayrollRepository payrollRepository;
    private IAttritionRepository attritionRepository;
    private IOnboardingRepository onboardingRepository;
    private DocumentRepository documentRepository;
    private AuditRepository documentAuditRepository;
    private CustomizationRepositoryImpl customizationRepository;
    private PerformanceRepositoryImpl performanceRepository;
    private LeaveRepositoryImpl leaveRepository;
    private com.hrms.db.repositories.Leave_Management_Subsytem.LeaveManagementSubsystemRepositoryImpl leaveManagementSubsystemRepository;
    private SecurityRepositoryImpl securityRepository;

    // Benefits Administration
    private BenefitPlanDAOImpl benefitPlanRepository;
    private BenefitPolicyDAOImpl benefitPolicyRepository;
    private EnrollmentDAOImpl benefitEnrollmentRepository;
    private NotificationDAOImpl benefitsNotificationRepository;
    private EmployeeProfileDAOImpl benefitsEmployeeProfileRepository;
    private AuditLogDAOImpl benefitsAuditLogRepository;

    // Expense Management
    private ExpenseEmployeeRepositoryImpl expenseEmployeeRepository;
    private ExpenseClaimRepositoryImpl expenseClaimRepository;
    private ExpenseBudgetRepositoryImpl expenseBudgetRepository;
    private ExpenseReceiptRepositoryImpl expenseReceiptRepository;
    private ExpenseLeaveRepositoryImpl expenseLeaveRepository;
    private ExpenseAuditRepositoryImpl expenseAuditRepository;

    // Time Tracking
    private TimeTrackingAttendanceRepositoryImpl timeTrackingAttendanceRepository;
    private TimeTrackingBreakRepositoryImpl timeTrackingBreakRepository;
    private TimeTrackingEmployeeRepositoryImpl timeTrackingEmployeeRepository;
    private TimeTrackingNotificationRepositoryImpl timeTrackingNotificationRepository;
    private TimeTrackingOvertimeRepositoryImpl timeTrackingOvertimeRepository;
    private TimeTrackingPolicyRepositoryImpl timeTrackingPolicyRepository;
    private TimeTrackingReportRepositoryImpl timeTrackingReportRepository;

    // Succession Planning
    private RoleRepositoryImpl successionRoleRepository;
    private SuccessionPoolRepositoryImpl successionPoolRepository;
    private ReadinessScoreRepositoryImpl successionReadinessScoreRepository;
    private SuccessorAssignmentRepositoryImpl successionSuccessorAssignmentRepository;
    private DevelopmentPlanRepositoryImpl successionDevelopmentPlanRepository;
    private PlanTaskRepositoryImpl successionPlanTaskRepository;
    private SuccessionNotificationRepositoryImpl successionNotificationRepository;
    private RiskLogRepositoryImpl successionRiskLogRepository;
    private ExternalHireRequestRepositoryImpl successionExternalHireRequestRepository;
    private SuccessionAuditLogRepositoryImpl successionAuditLogRepository;

    // HR & Analytics
    private EmployeeServiceImpl hrAnalyticsEmployeeService;

    // Multi-country Support
    private IMultiCountryRepository multiCountryRepository;

    private RepositoryFactory() {}

    public static synchronized RepositoryFactory getInstance() {
        if (instance == null) {
            instance = new RepositoryFactory();
        }
        return instance;
    }

    // ── Payroll ────────────────────────────────────────────────────────

    public synchronized IPayrollRepository getPayrollRepository() {
        if (payrollRepository == null) payrollRepository = new PayrollRepositoryImpl();
        return payrollRepository;
    }

    // ── Attrition ──────────────────────────────────────────────────────

    public synchronized IAttritionRepository getAttritionRepository() {
        if (attritionRepository == null) attritionRepository = new AttritionRepositoryImpl();
        return attritionRepository;
    }

    // ── Onboarding ─────────────────────────────────────────────────────

    public synchronized IOnboardingRepository getOnboardingRepository() {
        if (onboardingRepository == null) onboardingRepository = new OnboardingRepositoryImpl();
        return onboardingRepository;
    }

    // ── Document Management ────────────────────────────────────────────

    private synchronized DocumentRepository getDocumentRepositoryImpl() {
        if (documentRepository == null) {
            documentRepository = new DocumentRepositoryImpl();
        }
        return documentRepository;
    }

    private synchronized AuditRepository getDocumentAuditRepositoryImpl() {
        if (documentAuditRepository == null) {
            documentAuditRepository = new AuditRepositoryImpl();
        }
        return documentAuditRepository;
    }

    public DocumentRepository getDocumentRepository() {
        return getDocumentRepositoryImpl();
    }

    public AuditRepository getDocumentAuditRepository() {
        return getDocumentAuditRepositoryImpl();
    }

    // ── Customization (Unified Impl) ───────────────────────────────────

    private synchronized CustomizationRepositoryImpl getCustomizationImpl() {
        if (customizationRepository == null) customizationRepository = new CustomizationRepositoryImpl();
        return customizationRepository;
    }

    public IEITRepository getEitRepository()               { return getCustomizationImpl(); }
    public IFlexfieldRepository getFlexfieldRepository()   { return getCustomizationImpl(); }
    public IFormRepository getFormRepository()             { return getCustomizationImpl(); }
    public ILookupRepository getLookupRepository()         { return getCustomizationImpl(); }
    public IModuleRepository getModuleRepository()         { return getCustomizationImpl(); }
    public com.hrms.db.repositories.Customization_team.IReportRepository getCustomizationReportRepository() { return getCustomizationImpl(); }
    public ITaskFlowRepository getTaskFlowRepository()     { return getCustomizationImpl(); }
    public IWorkflowRepository getWorkflowRepository()     { return getCustomizationImpl(); }

    // ── Performance Management (Unified Impl) ──────────────────────────

    private synchronized PerformanceRepositoryImpl getPerformanceImpl() {
        if (performanceRepository == null) performanceRepository = new PerformanceRepositoryImpl();
        return performanceRepository;
    }

    public IAppraisalRepository getAppraisalRepository()           { return getPerformanceImpl(); }
    public IAuditLogRepository getAuditLogRepository()             { return getPerformanceImpl(); }
    public IEmployeeRepository getPerformanceEmployeeRepository()  { return getPerformanceImpl(); }
    public IFeedbackRepository getFeedbackRepository()             { return getPerformanceImpl(); }
    public IGoalRepository getGoalRepository()                     { return getPerformanceImpl(); }
    public IKPIRepository getKpiRepository()                       { return getPerformanceImpl(); }
    public INotificationRepository getNotificationRepository()     { return getPerformanceImpl(); }
    public com.hrms.db.repositories.performance.interfaces.IReportRepository getPerformanceReportRepository() { return getPerformanceImpl(); }
    public ISkillGapRepository getSkillGapRepository()             { return getPerformanceImpl(); }
    public IPerformanceCycleRepository getPerformanceCycleRepository() { return getPerformanceImpl(); }

    // ── Leave Management (Unified Impl) ────────────────────────────────

    private synchronized LeaveRepositoryImpl getLeaveImpl() {
        if (leaveRepository == null) leaveRepository = new LeaveRepositoryImpl();
        return leaveRepository;
    }

    private synchronized com.hrms.db.repositories.Leave_Management_Subsytem.LeaveManagementSubsystemRepositoryImpl getLeaveManagementSubsystemImpl() {
        if (leaveManagementSubsystemRepository == null) {
            leaveManagementSubsystemRepository = new com.hrms.db.repositories.Leave_Management_Subsytem.LeaveManagementSubsystemRepositoryImpl(getLeaveImpl());
        }
        return leaveManagementSubsystemRepository;
    }

    public ILeaveRecordRepository getLeaveRecordRepository()         { return getLeaveImpl(); }
    public ILeaveEmployeeRepository getLeaveEmployeeRepository()     { return getLeaveImpl(); }
    public ILeaveHolidayRepository getLeaveHolidayRepository()       { return getLeaveImpl(); }
    public ILeavePolicyRepository getLeavePolicyRepository()         { return getLeaveImpl(); }
    public ILeavePayrollSyncRepository getLeavePayrollSyncRepository() { return getLeaveImpl(); }
    public ILeaveAuditLogRepository getLeaveAuditLogRepository()     { return getLeaveImpl(); }

    // ── Leave Management Subsystem (team folder: Leave_Management_Subsytem) ──
    // These interfaces/DTOs are provided by the Leave team; we implement them by delegating to LeaveRepositoryImpl.

    public com.hrms.db.repositories.Leave_Management_Subsytem.ILeaveRecordRepository getLeaveManagementSubsystemLeaveRecordRepository() {
        return getLeaveManagementSubsystemImpl();
    }

    public com.hrms.db.repositories.Leave_Management_Subsytem.ILeavePolicyRepository getLeaveManagementSubsystemLeavePolicyRepository() {
        return getLeaveManagementSubsystemImpl();
    }

    public com.hrms.db.repositories.Leave_Management_Subsytem.IHolidayRepository getLeaveManagementSubsystemHolidayRepository() {
        return getLeaveManagementSubsystemImpl();
    }

    public com.hrms.db.repositories.Leave_Management_Subsytem.IEmployeeRepository getLeaveManagementSubsystemEmployeeRepository() {
        return getLeaveManagementSubsystemImpl();
    }

    public com.hrms.db.repositories.Leave_Management_Subsytem.IAuditLogRepository getLeaveManagementSubsystemAuditLogRepository() {
        return getLeaveManagementSubsystemImpl();
    }

    public com.hrms.db.repositories.Leave_Management_Subsytem.IPayrollSyncRepository getLeaveManagementSubsystemPayrollSyncRepository() {
        return getLeaveManagementSubsystemImpl();
    }

    // ── Security (Unified Impl) ──────────────────────────────────────

    private synchronized SecurityRepositoryImpl getSecurityImpl() {
        if (securityRepository == null) securityRepository = new SecurityRepositoryImpl();
        return securityRepository;
    }

    public IAuditService getAuditService() { return getSecurityImpl(); }
    public IAuthenticationService getAuthenticationService() { return getSecurityImpl(); }
    public IAuthorizationService getAuthorizationService() { return getSecurityImpl(); }
    public IEncryptionService getEncryptionService() { return getSecurityImpl(); }

    // ── Benefits Administration ───────────────────────────────────────

    private synchronized BenefitPlanDAOImpl getBenefitPlanImpl() {
        if (benefitPlanRepository == null) benefitPlanRepository = new BenefitPlanDAOImpl();
        return benefitPlanRepository;
    }

    private synchronized BenefitPolicyDAOImpl getBenefitPolicyImpl() {
        if (benefitPolicyRepository == null) benefitPolicyRepository = new BenefitPolicyDAOImpl();
        return benefitPolicyRepository;
    }

    private synchronized EnrollmentDAOImpl getBenefitEnrollmentImpl() {
        if (benefitEnrollmentRepository == null) benefitEnrollmentRepository = new EnrollmentDAOImpl();
        return benefitEnrollmentRepository;
    }

    private synchronized NotificationDAOImpl getBenefitsNotificationImpl() {
        if (benefitsNotificationRepository == null) benefitsNotificationRepository = new NotificationDAOImpl();
        return benefitsNotificationRepository;
    }

    private synchronized EmployeeProfileDAOImpl getBenefitsEmployeeProfileImpl() {
        if (benefitsEmployeeProfileRepository == null) benefitsEmployeeProfileRepository = new EmployeeProfileDAOImpl();
        return benefitsEmployeeProfileRepository;
    }

    private synchronized AuditLogDAOImpl getBenefitsAuditLogImpl() {
        if (benefitsAuditLogRepository == null) benefitsAuditLogRepository = new AuditLogDAOImpl();
        return benefitsAuditLogRepository;
    }

    public BenefitPlanDAO getBenefitPlanRepository() { return getBenefitPlanImpl(); }
    public BenefitPolicyDAO getBenefitPolicyRepository() { return getBenefitPolicyImpl(); }
    public EnrollmentDAO getBenefitEnrollmentRepository() { return getBenefitEnrollmentImpl(); }
    public NotificationDAO getBenefitsNotificationRepository() { return getBenefitsNotificationImpl(); }
    public EmployeeProfileDAO getBenefitsEmployeeProfileRepository() { return getBenefitsEmployeeProfileImpl(); }
    public AuditLogDAO getBenefitsAuditLogRepository() { return getBenefitsAuditLogImpl(); }

    // ── Expense Management ───────────────────────────────────────────

    private synchronized ExpenseEmployeeRepositoryImpl getExpenseEmployeeImpl() {
        if (expenseEmployeeRepository == null) expenseEmployeeRepository = new ExpenseEmployeeRepositoryImpl();
        return expenseEmployeeRepository;
    }

    private synchronized ExpenseClaimRepositoryImpl getExpenseClaimImpl() {
        if (expenseClaimRepository == null) expenseClaimRepository = new ExpenseClaimRepositoryImpl();
        return expenseClaimRepository;
    }

    private synchronized ExpenseBudgetRepositoryImpl getExpenseBudgetImpl() {
        if (expenseBudgetRepository == null) expenseBudgetRepository = new ExpenseBudgetRepositoryImpl();
        return expenseBudgetRepository;
    }

    private synchronized ExpenseReceiptRepositoryImpl getExpenseReceiptImpl() {
        if (expenseReceiptRepository == null) expenseReceiptRepository = new ExpenseReceiptRepositoryImpl();
        return expenseReceiptRepository;
    }

    private synchronized ExpenseLeaveRepositoryImpl getExpenseLeaveImpl() {
        if (expenseLeaveRepository == null) expenseLeaveRepository = new ExpenseLeaveRepositoryImpl();
        return expenseLeaveRepository;
    }

    private synchronized ExpenseAuditRepositoryImpl getExpenseAuditImpl() {
        if (expenseAuditRepository == null) expenseAuditRepository = new ExpenseAuditRepositoryImpl();
        return expenseAuditRepository;
    }

    public com.hrms.db.repositories.Expense_Management.EmployeeRepository getExpenseEmployeeRepository() { return getExpenseEmployeeImpl(); }
    public com.hrms.db.repositories.Expense_Management.ClaimRepository getExpenseClaimRepository() { return getExpenseClaimImpl(); }
    public com.hrms.db.repositories.Expense_Management.BudgetRepository getExpenseBudgetRepository() { return getExpenseBudgetImpl(); }
    public com.hrms.db.repositories.Expense_Management.ReceiptRepository getExpenseReceiptRepository() { return getExpenseReceiptImpl(); }
    public com.hrms.db.repositories.Expense_Management.LeaveRepository getExpenseLeaveRepository() { return getExpenseLeaveImpl(); }
    public com.hrms.db.repositories.Expense_Management.AuditRepository getExpenseAuditRepository() { return getExpenseAuditImpl(); }

    // ── Time Tracking ────────────────────────────────────────────

    private synchronized TimeTrackingAttendanceRepositoryImpl getTimeTrackingAttendanceImpl() {
        if (timeTrackingAttendanceRepository == null) timeTrackingAttendanceRepository = new TimeTrackingAttendanceRepositoryImpl();
        return timeTrackingAttendanceRepository;
    }

    private synchronized TimeTrackingBreakRepositoryImpl getTimeTrackingBreakImpl() {
        if (timeTrackingBreakRepository == null) timeTrackingBreakRepository = new TimeTrackingBreakRepositoryImpl();
        return timeTrackingBreakRepository;
    }

    private synchronized TimeTrackingEmployeeRepositoryImpl getTimeTrackingEmployeeImpl() {
        if (timeTrackingEmployeeRepository == null) timeTrackingEmployeeRepository = new TimeTrackingEmployeeRepositoryImpl();
        return timeTrackingEmployeeRepository;
    }

    private synchronized TimeTrackingNotificationRepositoryImpl getTimeTrackingNotificationImpl() {
        if (timeTrackingNotificationRepository == null) timeTrackingNotificationRepository = new TimeTrackingNotificationRepositoryImpl();
        return timeTrackingNotificationRepository;
    }

    private synchronized TimeTrackingOvertimeRepositoryImpl getTimeTrackingOvertimeImpl() {
        if (timeTrackingOvertimeRepository == null) timeTrackingOvertimeRepository = new TimeTrackingOvertimeRepositoryImpl();
        return timeTrackingOvertimeRepository;
    }

    private synchronized TimeTrackingPolicyRepositoryImpl getTimeTrackingPolicyImpl() {
        if (timeTrackingPolicyRepository == null) timeTrackingPolicyRepository = new TimeTrackingPolicyRepositoryImpl();
        return timeTrackingPolicyRepository;
    }

    private synchronized TimeTrackingReportRepositoryImpl getTimeTrackingReportImpl() {
        if (timeTrackingReportRepository == null) timeTrackingReportRepository = new TimeTrackingReportRepositoryImpl();
        return timeTrackingReportRepository;
    }

    public com.hrms.db.repositories.timetracking.IAttendanceRepository getTimeTrackingAttendanceRepository() { return getTimeTrackingAttendanceImpl(); }
    public com.hrms.db.repositories.timetracking.IBreakRepository getTimeTrackingBreakRepository() { return getTimeTrackingBreakImpl(); }
    public com.hrms.db.repositories.timetracking.IEmployeeRepository getTimeTrackingEmployeeRepository() { return getTimeTrackingEmployeeImpl(); }
    public com.hrms.db.repositories.timetracking.INotificationRepository getTimeTrackingNotificationRepository() { return getTimeTrackingNotificationImpl(); }
    public com.hrms.db.repositories.timetracking.IOvertimeRepository getTimeTrackingOvertimeRepository() { return getTimeTrackingOvertimeImpl(); }
    public com.hrms.db.repositories.timetracking.IPolicyRepository getTimeTrackingPolicyRepository() { return getTimeTrackingPolicyImpl(); }
    public com.hrms.db.repositories.timetracking.IReportRepository getTimeTrackingReportRepository() { return getTimeTrackingReportImpl(); }

    // ── Succession Planning ───────────────────────────────────────

    private synchronized RoleRepositoryImpl getSuccessionRoleImpl() {
        if (successionRoleRepository == null) successionRoleRepository = new RoleRepositoryImpl();
        return successionRoleRepository;
    }

    private synchronized SuccessionPoolRepositoryImpl getSuccessionPoolImpl() {
        if (successionPoolRepository == null) successionPoolRepository = new SuccessionPoolRepositoryImpl();
        return successionPoolRepository;
    }

    private synchronized ReadinessScoreRepositoryImpl getSuccessionReadinessScoreImpl() {
        if (successionReadinessScoreRepository == null) successionReadinessScoreRepository = new ReadinessScoreRepositoryImpl();
        return successionReadinessScoreRepository;
    }

    private synchronized SuccessorAssignmentRepositoryImpl getSuccessionSuccessorAssignmentImpl() {
        if (successionSuccessorAssignmentRepository == null) successionSuccessorAssignmentRepository = new SuccessorAssignmentRepositoryImpl();
        return successionSuccessorAssignmentRepository;
    }

    private synchronized DevelopmentPlanRepositoryImpl getSuccessionDevelopmentPlanImpl() {
        if (successionDevelopmentPlanRepository == null) successionDevelopmentPlanRepository = new DevelopmentPlanRepositoryImpl();
        return successionDevelopmentPlanRepository;
    }

    private synchronized PlanTaskRepositoryImpl getSuccessionPlanTaskImpl() {
        if (successionPlanTaskRepository == null) successionPlanTaskRepository = new PlanTaskRepositoryImpl();
        return successionPlanTaskRepository;
    }

    private synchronized SuccessionNotificationRepositoryImpl getSuccessionNotificationImpl() {
        if (successionNotificationRepository == null) successionNotificationRepository = new SuccessionNotificationRepositoryImpl();
        return successionNotificationRepository;
    }

    private synchronized RiskLogRepositoryImpl getSuccessionRiskLogImpl() {
        if (successionRiskLogRepository == null) successionRiskLogRepository = new RiskLogRepositoryImpl();
        return successionRiskLogRepository;
    }

    private synchronized ExternalHireRequestRepositoryImpl getSuccessionExternalHireRequestImpl() {
        if (successionExternalHireRequestRepository == null) successionExternalHireRequestRepository = new ExternalHireRequestRepositoryImpl();
        return successionExternalHireRequestRepository;
    }

    private synchronized SuccessionAuditLogRepositoryImpl getSuccessionAuditLogImpl() {
        if (successionAuditLogRepository == null) successionAuditLogRepository = new SuccessionAuditLogRepositoryImpl();
        return successionAuditLogRepository;
    }

    public com.hrms.db.repositories.succession.IRoleRepository getSuccessionRoleRepository() { return getSuccessionRoleImpl(); }
    public com.hrms.db.repositories.succession.ISuccessionPoolRepository getSuccessionPoolRepository() { return getSuccessionPoolImpl(); }
    public com.hrms.db.repositories.succession.IReadinessScoreRepository getSuccessionReadinessScoreRepository() { return getSuccessionReadinessScoreImpl(); }
    public com.hrms.db.repositories.succession.ISuccessorAssignmentRepository getSuccessionSuccessorAssignmentRepository() { return getSuccessionSuccessorAssignmentImpl(); }
    public com.hrms.db.repositories.succession.IDevelopmentPlanRepository getSuccessionDevelopmentPlanRepository() { return getSuccessionDevelopmentPlanImpl(); }
    public com.hrms.db.repositories.succession.IPlanTaskRepository getSuccessionPlanTaskRepository() { return getSuccessionPlanTaskImpl(); }
    public com.hrms.db.repositories.succession.INotificationRepository getSuccessionNotificationRepository() { return getSuccessionNotificationImpl(); }
    public com.hrms.db.repositories.succession.IRiskLogRepository getSuccessionRiskLogRepository() { return getSuccessionRiskLogImpl(); }
    public com.hrms.db.repositories.succession.IExternalHireRequestRepository getSuccessionExternalHireRequestRepository() { return getSuccessionExternalHireRequestImpl(); }
    public com.hrms.db.repositories.succession.IAuditLogRepository getSuccessionAuditLogRepository() { return getSuccessionAuditLogImpl(); }

    // ── HR & Analytics ───────────────────────────────────────────

    private synchronized EmployeeServiceImpl getHrAnalyticsEmployeeServiceImpl() {
        if (hrAnalyticsEmployeeService == null) hrAnalyticsEmployeeService = new EmployeeServiceImpl();
        return hrAnalyticsEmployeeService;
    }

    public com.hrms.db.repositories.hranalytics.EmployeeService getHrAnalyticsEmployeeService() { return getHrAnalyticsEmployeeServiceImpl(); }

    // ── Multi-country Support ─────────────────────────────────────────

    public synchronized IMultiCountryRepository getMultiCountryRepository() {
        if (multiCountryRepository == null) multiCountryRepository = new MultiCountryRepositoryImpl();
        return multiCountryRepository;
    }
}
