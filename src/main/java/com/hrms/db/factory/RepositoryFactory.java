package com.hrms.db.factory;

import com.hrms.db.repositories.attrition.AttritionRepositoryImpl;
import com.hrms.db.repositories.attrition.IAttritionRepository;
import com.hrms.db.repositories.Customization_team.*;
import com.hrms.db.repositories.onboarding.IOnboardingRepository;
import com.hrms.db.repositories.onboarding.OnboardingRepositoryImpl;
import com.hrms.db.repositories.payroll.IPayrollRepository;
import com.hrms.db.repositories.payroll.PayrollRepositoryImpl;
import com.hrms.db.repositories.performance.PerformanceRepositoryImpl;
import com.hrms.db.repositories.performance.interfaces.*;

/**
 * RepositoryFactory — Implementation of the Abstract Factory Pattern.
 *
 * This singleton factory is the single point of entry for all other subsystems
 * to obtain references to our DB repositories. It guarantees that subsystem teams
 * only ever code against INTERFACES and never directly instantiate our implementation classes.
 *
 * It uses Lazy Initialization to create repositories only when first requested.
 */
public class RepositoryFactory {

    private static RepositoryFactory instance;

    // Subsystem Repository Instances
    private IPayrollRepository payrollRepository;
    private IAttritionRepository attritionRepository;
    private IOnboardingRepository onboardingRepository;
    private CustomizationRepositoryImpl customizationRepository;
    private PerformanceRepositoryImpl performanceRepository;

    private RepositoryFactory() {}

    /**
     * Get the singleton instance of the factory.
     */
    public static synchronized RepositoryFactory getInstance() {
        if (instance == null) {
            instance = new RepositoryFactory();
        }
        return instance;
    }

    // ── Payroll ────────────────────────────────────────────────────────

    public synchronized IPayrollRepository getPayrollRepository() {
        if (payrollRepository == null) {
            payrollRepository = new PayrollRepositoryImpl();
        }
        return payrollRepository;
    }

    // ── Attrition ──────────────────────────────────────────────────────

    public synchronized IAttritionRepository getAttritionRepository() {
        if (attritionRepository == null) {
            attritionRepository = new AttritionRepositoryImpl();
        }
        return attritionRepository;
    }

    // ── Onboarding ─────────────────────────────────────────────────────

    public synchronized IOnboardingRepository getOnboardingRepository() {
        if (onboardingRepository == null) {
            onboardingRepository = new OnboardingRepositoryImpl();
        }
        return onboardingRepository;
    }

    // ── Customization (Unified Impl) ───────────────────────────────────

    private synchronized CustomizationRepositoryImpl getCustomizationImpl() {
        if (customizationRepository == null) {
            customizationRepository = new CustomizationRepositoryImpl();
        }
        return customizationRepository;
    }

    public IEITRepository getEitRepository() { return getCustomizationImpl(); }
    public IFlexfieldRepository getFlexfieldRepository() { return getCustomizationImpl(); }
    public IFormRepository getFormRepository() { return getCustomizationImpl(); }
    public ILookupRepository getLookupRepository() { return getCustomizationImpl(); }
    public IModuleRepository getModuleRepository() { return getCustomizationImpl(); }
    public com.hrms.db.repositories.Customization_team.IReportRepository getCustomizationReportRepository() { return getCustomizationImpl(); }
    public ITaskFlowRepository getTaskFlowRepository() { return getCustomizationImpl(); }
    public IWorkflowRepository getWorkflowRepository() { return getCustomizationImpl(); }

    // ── Performance Management (Unified Impl) ──────────────────────────

    private synchronized PerformanceRepositoryImpl getPerformanceImpl() {
        if (performanceRepository == null) {
            performanceRepository = new PerformanceRepositoryImpl();
        }
        return performanceRepository;
    }

    public IAppraisalRepository getAppraisalRepository() { return getPerformanceImpl(); }
    public IAuditLogRepository getAuditLogRepository() { return getPerformanceImpl(); }
    public IEmployeeRepository getEmployeeRepository() { return getPerformanceImpl(); }
    public IFeedbackRepository getFeedbackRepository() { return getPerformanceImpl(); }
    public IGoalRepository getGoalRepository() { return getPerformanceImpl(); }
    public IKPIRepository getKpiRepository() { return getPerformanceImpl(); }
    public INotificationRepository getNotificationRepository() { return getPerformanceImpl(); }
    public com.hrms.db.repositories.performance.interfaces.IReportRepository getPerformanceReportRepository() { return getPerformanceImpl(); }
    public ISkillGapRepository getSkillGapRepository() { return getPerformanceImpl(); }
    public IPerformanceCycleRepository getPerformanceCycleRepository() { return getPerformanceImpl(); }
}
