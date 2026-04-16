package com.hrms.db.factory;

import com.hrms.db.repositories.attrition.AttritionRepositoryImpl;
import com.hrms.db.repositories.attrition.IAttritionRepository;
import com.hrms.db.repositories.Customization_team.*;
import com.hrms.db.repositories.leave.*;
import com.hrms.db.repositories.onboarding.IOnboardingRepository;
import com.hrms.db.repositories.onboarding.OnboardingRepositoryImpl;
import com.hrms.db.repositories.payroll.IPayrollRepository;
import com.hrms.db.repositories.payroll.PayrollRepositoryImpl;
import com.hrms.db.repositories.performance.PerformanceRepositoryImpl;
import com.hrms.db.repositories.performance.interfaces.*;

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
    private CustomizationRepositoryImpl customizationRepository;
    private PerformanceRepositoryImpl performanceRepository;
    private LeaveRepositoryImpl leaveRepository;
    private com.hrms.db.repositories.Leave_Management_Subsytem.LeaveManagementSubsystemRepositoryImpl leaveManagementSubsystemRepository;

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
}
