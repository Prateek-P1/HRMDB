package com.hrms.db.repositories.multicountry;

import com.hrms.db.config.DatabaseConnection;
import com.hrms.db.entities.AccessPermission;
import com.hrms.db.entities.BenefitEnrollment;
import com.hrms.db.entities.BenefitPlan;
import com.hrms.db.entities.CompliancePolicy;
import com.hrms.db.entities.Employee;
import com.hrms.db.entities.ExchangeRate;
import com.hrms.db.entities.LocaleConfig;
import com.hrms.db.entities.PayrollResult;
import com.hrms.db.entities.PublicHoliday;
import com.hrms.db.entities.TaxRegime;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Hibernate-backed implementation for the Multi-Country Support unified interface.
 *
 * Where the current DB schema does not provide a backing entity, this class keeps
 * a small in-memory fallback store (per-process).
 */
public class MultiCountryRepositoryImpl implements IMultiCountryRepository {

    private final Map<String, HRPolicyDTO> hrPoliciesByCountry = new ConcurrentHashMap<>();
    private final Map<String, WorkingHoursDTO> workingHoursByCountry = new ConcurrentHashMap<>();
    private final Map<String, OvertimeDTO> overtimeByCountry = new ConcurrentHashMap<>();
    private final Map<String, String> userLanguageByEmployee = new ConcurrentHashMap<>();
    private final Map<String, String> dateFormatByCountry = new ConcurrentHashMap<>();
    private final Map<String, MobilityDTO> mobilityByEmployee = new ConcurrentHashMap<>();
    private final Map<String, VisaDTO> visaByEmployee = new ConcurrentHashMap<>();

    // ─────────────────────────────────────────────
    // 1. LOCALIZATION OF HR POLICIES
    // ─────────────────────────────────────────────

    @Override
    public HRPolicyDTO getHRPolicy(String countryCode) {
        if (countryCode == null) return null;
        return hrPoliciesByCountry.get(countryCode);
    }

    @Override
    public void createHRPolicy(HRPolicyDTO policy) {
        upsertHrPolicy(policy);
    }

    @Override
    public void updateHRPolicy(HRPolicyDTO policy) {
        upsertHrPolicy(policy);
    }

    private void upsertHrPolicy(HRPolicyDTO policy) {
        if (policy == null || policy.getCountryCode() == null) return;
        policy.setLastUpdated(LocalDateTime.now());
        hrPoliciesByCountry.put(policy.getCountryCode(), policy);
    }

    @Override
    public List<EmploymentTypeDTO> getEmploymentTypes(String countryCode) {
        if (countryCode == null) return Collections.emptyList();
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            List<String> types = session.createQuery(
                            "SELECT DISTINCT e.employmentType FROM Employee e WHERE e.countryCode = :cc AND e.employmentType IS NOT NULL",
                            String.class)
                    .setParameter("cc", countryCode)
                    .getResultList();
            List<EmploymentTypeDTO> out = new ArrayList<>(types.size());
            for (String t : types) out.add(new EmploymentTypeDTO(t, t));
            return out;
        } catch (Exception ex) {
            return Collections.emptyList();
        }
    }

    @Override
    public void configureWorkingHours(String countryCode, WorkingHoursDTO hours) {
        if (countryCode == null || hours == null) return;
        hours.setCountryCode(countryCode);
        workingHoursByCountry.put(countryCode, hours);
    }

    @Override
    public void configureOvertimeRules(String countryCode, OvertimeDTO overtime) {
        if (countryCode == null || overtime == null) return;
        overtime.setCountryCode(countryCode);
        overtimeByCountry.put(countryCode, overtime);
    }

    // ─────────────────────────────────────────────
    // 2. MULTI-CURRENCY & PAYROLL SUPPORT
    // ─────────────────────────────────────────────

    @Override
    public PayrollDTO getPayrollDetails(String employeeID, String countryCode) {
        if (employeeID == null) return null;
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            Employee emp = session.get(Employee.class, employeeID);
            if (emp == null) return null;

            PayrollResult pr = session.createQuery(
                            "FROM PayrollResult p WHERE p.employee.empId = :id ORDER BY p.processedAt DESC",
                            PayrollResult.class)
                    .setParameter("id", employeeID)
                    .setMaxResults(1)
                    .uniqueResult();

            PayrollDTO dto = new PayrollDTO();
            dto.setEmployeeId(employeeID);
            dto.setCountryCode(emp.getCountryCode());
            dto.setCurrencyCode(emp.getCurrencyCode());

            if (pr != null) {
                dto.setPayPeriod(pr.getPayPeriod());
                dto.setGrossPay(pr.getFinalGrossPay());
                dto.setNetPay(pr.getFinalNetPay());
                dto.setProcessedAt(pr.getProcessedAt());
            }

            // If a countryCode was provided and doesn't match, caller can treat this as mismatch.
            return dto;
        } catch (Exception ex) {
            return null;
        }
    }

    @Override
    public void processPayroll(String countryCode, String payrollPeriod) {
        // Payroll processing is owned by the Payroll subsystem; nothing to do here.
    }

    @Override
    public ExchangeRateDTO getExchangeRate(String fromCurrency, String toCurrency) {
        if (fromCurrency == null || toCurrency == null) return null;
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            ExchangeRate rate = session.createQuery(
                            "FROM ExchangeRate r WHERE r.baseCurrencyCode = :b AND r.targetCurrencyCode = :t ORDER BY r.rateEffectiveDate DESC, r.rateId DESC",
                            ExchangeRate.class)
                    .setParameter("b", fromCurrency)
                    .setParameter("t", toCurrency)
                    .setMaxResults(1)
                    .uniqueResult();

            if (rate == null) return null;
            ExchangeRateDTO dto = new ExchangeRateDTO();
            dto.setFromCurrency(rate.getBaseCurrencyCode());
            dto.setToCurrency(rate.getTargetCurrencyCode());
            dto.setRate(rate.getExchangeRate());
            dto.setEffectiveDate(rate.getRateEffectiveDate());
            dto.setSource(rate.getFxRateSource());
            return dto;
        } catch (Exception ex) {
            return null;
        }
    }

    @Override
    public void updateExchangeRate(ExchangeRateDTO rate) {
        if (rate == null || rate.getFromCurrency() == null || rate.getToCurrency() == null) return;
        Transaction tx = null;
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            ExchangeRate entity = session.createQuery(
                            "FROM ExchangeRate r WHERE r.baseCurrencyCode = :b AND r.targetCurrencyCode = :t ORDER BY r.rateEffectiveDate DESC, r.rateId DESC",
                            ExchangeRate.class)
                    .setParameter("b", rate.getFromCurrency())
                    .setParameter("t", rate.getToCurrency())
                    .setMaxResults(1)
                    .uniqueResult();

            if (entity == null) entity = new ExchangeRate();
            entity.setBaseCurrencyCode(rate.getFromCurrency());
            entity.setTargetCurrencyCode(rate.getToCurrency());
            entity.setExchangeRate(rate.getRate());
            entity.setRateEffectiveDate(rate.getEffectiveDate() != null ? rate.getEffectiveDate() : LocalDate.now());
            entity.setFxRateSource(rate.getSource());
            entity.setManualOverrideFlag(true);
            entity.setRateLockedAt(LocalDateTime.now());

            session.merge(entity);
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) try { tx.rollback(); } catch (Exception ignored) {}
            throw ex;
        }
    }

    @Override
    public TaxRegimeDTO getTaxRules(String countryCode) {
        if (countryCode == null) return null;
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            TaxRegime regime = session.createQuery(
                            "FROM TaxRegime t WHERE t.countryCode = :cc AND t.deductionIsActive = true ORDER BY t.fiscalYear DESC, t.regimeId DESC",
                            TaxRegime.class)
                    .setParameter("cc", countryCode)
                    .setMaxResults(1)
                    .uniqueResult();

            if (regime == null) return null;
            TaxRegimeDTO dto = new TaxRegimeDTO();
            dto.setCountryCode(regime.getCountryCode());
            dto.setTaxRegimeName(regime.getTaxRegimeName());
            dto.setFiscalYear(regime.getFiscalYear());
            dto.setTaxRatePct(regime.getTaxRatePct());
            dto.setStatutoryDeductionName(regime.getStatutoryDeductionName());
            dto.setEmployeeRate(regime.getDeductionEmployeeRate());
            dto.setEmployerRate(regime.getDeductionEmployerRate());
            dto.setFilingDeadlineDate(regime.getFilingDeadlineDate());
            return dto;
        } catch (Exception ex) {
            return null;
        }
    }

    @Override
    public void applyDeductions(String employeeID) {
        // Deductions are applied during payroll processing; handled by Payroll subsystem.
    }

    @Override
    public ReportDTO generateGlobalPayrollReport() {
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            Double totalNet = session.createQuery(
                            "SELECT COALESCE(SUM(p.finalNetPay), 0) FROM PayrollResult p",
                            Double.class)
                    .uniqueResult();

            Long employeeCount = session.createQuery(
                            "SELECT COUNT(e) FROM Employee e",
                            Long.class)
                    .uniqueResult();

            String content = "Global Payroll Summary\n" +
                    "Employees: " + (employeeCount != null ? employeeCount : 0) + "\n" +
                    "Total Net Pay: " + (totalNet != null ? totalNet : 0);

            return new ReportDTO("GlobalPayrollReport", LocalDateTime.now(), content);
        } catch (Exception ex) {
            return new ReportDTO("GlobalPayrollReport", LocalDateTime.now(), "No data");
        }
    }

    // ─────────────────────────────────────────────
    // 3. LANGUAGE & REGIONAL SETTINGS
    // ─────────────────────────────────────────────

    @Override
    public LocaleDTO getLocale(String localeCode) {
        if (localeCode == null) return null;
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            LocaleConfig cfg = session.get(LocaleConfig.class, localeCode);
            if (cfg == null) return null;
            LocaleDTO dto = new LocaleDTO();
            dto.setLocaleCode(cfg.getLocaleCode());
            dto.setLanguageName(cfg.getLanguageName());
            dto.setDateFormatPattern(cfg.getDateFormatPattern());
            dto.setNumberFormatPattern(cfg.getNumberFormatPattern());
            dto.setCurrencyFormatPattern(cfg.getCurrencyFormatPattern());
            dto.setFirstDayOfWeek(cfg.getFirstDayOfWeek());
            return dto;
        } catch (Exception ex) {
            return null;
        }
    }

    @Override
    public void updateLocale(LocaleDTO locale) {
        if (locale == null || locale.getLocaleCode() == null) return;
        Transaction tx = null;
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            LocaleConfig cfg = session.get(LocaleConfig.class, locale.getLocaleCode());
            if (cfg == null) {
                cfg = new LocaleConfig();
                cfg.setLocaleCode(locale.getLocaleCode());
            }
            cfg.setLanguageName(locale.getLanguageName());
            cfg.setDateFormatPattern(locale.getDateFormatPattern());
            cfg.setNumberFormatPattern(locale.getNumberFormatPattern());
            cfg.setCurrencyFormatPattern(locale.getCurrencyFormatPattern());
            cfg.setFirstDayOfWeek(locale.getFirstDayOfWeek());
            session.merge(cfg);
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) try { tx.rollback(); } catch (Exception ignored) {}
            throw ex;
        }
    }

    @Override
    public List<LanguagePackDTO> getSupportedLanguages(String countryCode) {
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            List<LocaleConfig> configs = session.createQuery(
                            "FROM LocaleConfig l ORDER BY l.localeCode ASC",
                            LocaleConfig.class)
                    .getResultList();
            List<LanguagePackDTO> out = new ArrayList<>(configs.size());
            for (LocaleConfig cfg : configs) {
                out.add(new LanguagePackDTO(cfg.getLocaleCode(), cfg.getLanguageName()));
            }
            return out;
        } catch (Exception ex) {
            return Collections.emptyList();
        }
    }

    @Override
    public void setUserLanguage(String employeeID, String languageCode) {
        if (employeeID == null || languageCode == null) return;
        userLanguageByEmployee.put(employeeID, languageCode);
    }

    @Override
    public CalendarDTO getRegionalCalendar(String countryCode) {
        if (countryCode == null) return null;
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            List<PublicHoliday> holidays = session.createQuery(
                            "FROM PublicHoliday h WHERE h.countryCode = :cc ORDER BY h.holidayDate ASC",
                            PublicHoliday.class)
                    .setParameter("cc", countryCode)
                    .getResultList();

            CalendarDTO dto = new CalendarDTO();
            dto.setCountryCode(countryCode);
            List<CalendarDTO.Holiday> out = new ArrayList<>(holidays.size());
            for (PublicHoliday h : holidays) {
                out.add(new CalendarDTO.Holiday(h.getHolidayName(), h.getHolidayDate(), h.getIsMandatory()));
            }
            dto.setHolidays(out);
            return dto;
        } catch (Exception ex) {
            return null;
        }
    }

    @Override
    public void updateDateFormat(String countryCode, String format) {
        if (countryCode == null || format == null) return;
        dateFormatByCountry.put(countryCode, format);
    }

    // ─────────────────────────────────────────────
    // 4. COMPLIANCE & LEGAL ADHERENCE
    // ─────────────────────────────────────────────

    @Override
    public ComplianceDTO getComplianceStatus(String countryCode) {
        if (countryCode == null) return null;
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            List<CompliancePolicy> policies = session.createQuery(
                            "FROM CompliancePolicy p WHERE p.countryCode = :cc AND p.isActive = true",
                            CompliancePolicy.class)
                    .setParameter("cc", countryCode)
                    .getResultList();

            int count = policies.size();
            int scoreSum = 0;
            String worstSeverity = null;
            for (CompliancePolicy p : policies) {
                if (p.getComplianceScore() != null) scoreSum += p.getComplianceScore();
                worstSeverity = pickWorseSeverity(worstSeverity, p.getViolationSeverity());
            }

            ComplianceDTO dto = new ComplianceDTO();
            dto.setCountryCode(countryCode);
            dto.setActivePolicyCount(count);
            dto.setComplianceScore(count == 0 ? null : Math.round((float) scoreSum / count));
            dto.setViolationSeverity(worstSeverity);
            return dto;
        } catch (Exception ex) {
            return null;
        }
    }

    @Override
    public void updateCompliance(String countryCode, ComplianceDTO compliance) {
        if (countryCode == null || compliance == null) return;
        Transaction tx = null;
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            List<CompliancePolicy> policies = session.createQuery(
                            "FROM CompliancePolicy p WHERE p.countryCode = :cc",
                            CompliancePolicy.class)
                    .setParameter("cc", countryCode)
                    .getResultList();

            for (CompliancePolicy p : policies) {
                if (compliance.getComplianceScore() != null) p.setComplianceScore(compliance.getComplianceScore());
                if (compliance.getViolationSeverity() != null) p.setViolationSeverity(compliance.getViolationSeverity());
                session.merge(p);
            }

            tx.commit();
        } catch (Exception ex) {
            if (tx != null) try { tx.rollback(); } catch (Exception ignored) {}
            throw ex;
        }
    }

    @Override
    public List<PolicyDTO> getLegalPolicies(String countryCode) {
        if (countryCode == null) return Collections.emptyList();
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            List<CompliancePolicy> policies = session.createQuery(
                            "FROM CompliancePolicy p WHERE p.countryCode = :cc ORDER BY p.policyId ASC",
                            CompliancePolicy.class)
                    .setParameter("cc", countryCode)
                    .getResultList();

            List<PolicyDTO> out = new ArrayList<>(policies.size());
            for (CompliancePolicy p : policies) {
                PolicyDTO dto = new PolicyDTO();
                dto.setPolicyId(p.getPolicyId() != null ? String.valueOf(p.getPolicyId()) : null);
                dto.setPolicyName(p.getPolicyName());
                dto.setPolicyCategory(p.getPolicyCategory());
                dto.setComplianceScore(p.getComplianceScore());
                dto.setViolationSeverity(p.getViolationSeverity());
                dto.setStatus(Boolean.TRUE.equals(p.getIsActive()) ? "ACTIVE" : "INACTIVE");
                out.add(dto);
            }
            return out;
        } catch (Exception ex) {
            return Collections.emptyList();
        }
    }

    @Override
    public void updatePolicy(String policyID, String status) {
        Integer id = parseIntOrNull(policyID);
        if (id == null) return;
        Transaction tx = null;
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            CompliancePolicy p = session.get(CompliancePolicy.class, id);
            if (p != null) {
                if (status != null) {
                    if (status.equalsIgnoreCase("ACTIVE")) {
                        p.setIsActive(true);
                    } else if (status.equalsIgnoreCase("INACTIVE")) {
                        p.setIsActive(false);
                    } else {
                        p.setViolationSeverity(status);
                    }
                }
                session.merge(p);
            }
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) try { tx.rollback(); } catch (Exception ignored) {}
            throw ex;
        }
    }

    @Override
    public ReportDTO generateComplianceReport(String countryCode) {
        List<PolicyDTO> policies = getLegalPolicies(countryCode);
        StringBuilder sb = new StringBuilder();
        sb.append("Compliance Report for ").append(countryCode).append("\n");
        for (PolicyDTO p : policies) {
            sb.append("- ").append(p.getPolicyId()).append(": ")
                    .append(p.getPolicyName()).append(" [").append(p.getStatus()).append("]")
                    .append(" score=").append(p.getComplianceScore())
                    .append(" severity=").append(p.getViolationSeverity())
                    .append("\n");
        }
        return new ReportDTO("ComplianceReport", LocalDateTime.now(), sb.toString());
    }

    @Override
    public void autoUpdateLegalChanges(String countryCode) {
        // Requires an external legal feeds integration; not available in this repository.
    }

    // ─────────────────────────────────────────────
    // 5. GLOBAL WORKFORCE ANALYTICS
    // ─────────────────────────────────────────────

    @Override
    public AnalyticsDTO getGlobalWorkforceMetrics() {
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            Long total = session.createQuery("SELECT COUNT(e) FROM Employee e", Long.class).uniqueResult();
            Long active = session.createQuery("SELECT COUNT(e) FROM Employee e WHERE e.employmentStatus = 'ACTIVE'", Long.class).uniqueResult();
            Long countries = session.createQuery(
                            "SELECT COUNT(DISTINCT e.countryCode) FROM Employee e WHERE e.countryCode IS NOT NULL",
                            Long.class)
                    .uniqueResult();
            Double avgPay = session.createQuery("SELECT AVG(e.basicPay) FROM Employee e", Double.class).uniqueResult();

            AnalyticsDTO dto = new AnalyticsDTO();
            dto.setTotalEmployees(total != null ? total.intValue() : 0);
            dto.setActiveEmployees(active != null ? active.intValue() : 0);
            dto.setCountriesRepresented(countries != null ? countries.intValue() : 0);
            dto.setAverageBasicPay(avgPay);
            return dto;
        } catch (Exception ex) {
            return new AnalyticsDTO();
        }
    }

    @Override
    public AnalyticsDTO getCountrySpecificMetrics(String countryCode) {
        if (countryCode == null) return null;
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            Long total = session.createQuery(
                            "SELECT COUNT(e) FROM Employee e WHERE e.countryCode = :cc",
                            Long.class)
                    .setParameter("cc", countryCode)
                    .uniqueResult();
            Long active = session.createQuery(
                            "SELECT COUNT(e) FROM Employee e WHERE e.countryCode = :cc AND e.employmentStatus = 'ACTIVE'",
                            Long.class)
                    .setParameter("cc", countryCode)
                    .uniqueResult();
            Double avgPay = session.createQuery(
                            "SELECT AVG(e.basicPay) FROM Employee e WHERE e.countryCode = :cc",
                            Double.class)
                    .setParameter("cc", countryCode)
                    .uniqueResult();

            AnalyticsDTO dto = new AnalyticsDTO();
            dto.setCountryCode(countryCode);
            dto.setTotalEmployees(total != null ? total.intValue() : 0);
            dto.setActiveEmployees(active != null ? active.intValue() : 0);
            dto.setAverageBasicPay(avgPay);
            return dto;
        } catch (Exception ex) {
            return null;
        }
    }

    @Override
    public void generateAnalyticsDashboard() {
        // UI/dashboard generation happens outside the DB layer.
    }

    @Override
    public void compareRegions(String countryCode1, String countryCode2) {
        // Comparison logic is handled by analytics/reporting consumers.
    }

    // ─────────────────────────────────────────────
    // 6. BENEFITS & COMPENSATION MANAGEMENT
    // ─────────────────────────────────────────────

    @Override
    public List<BenefitPlanDTO> getBenefitPlans(String countryCode) {
        // No country scoping exists for BenefitPlan today; returning all plans.
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            List<BenefitPlan> plans = session.createQuery(
                            "FROM BenefitPlan p ORDER BY p.planName ASC",
                            BenefitPlan.class)
                    .getResultList();
            List<BenefitPlanDTO> out = new ArrayList<>(plans.size());
            for (BenefitPlan p : plans) {
                BenefitPlanDTO dto = new BenefitPlanDTO();
                dto.setPlanId(p.getPlanId() != null ? String.valueOf(p.getPlanId()) : null);
                dto.setPlanName(p.getPlanName());
                dto.setPlanType(p.getPlanType());
                dto.setCoverageDetails(p.getCoverageDetails());
                out.add(dto);
            }
            return out;
        } catch (Exception ex) {
            return Collections.emptyList();
        }
    }

    @Override
    public void assignBenefitPlan(String employeeID, String planID) {
        Integer planId = parseIntOrNull(planID);
        if (employeeID == null || planId == null) return;

        Transaction tx = null;
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            Long count = session.createQuery(
                            "SELECT COUNT(e) FROM BenefitEnrollment e WHERE e.empId = :id AND e.planId = :pid",
                            Long.class)
                    .setParameter("id", employeeID)
                    .setParameter("pid", planId)
                    .uniqueResult();

            if (count == null || count == 0) {
                BenefitEnrollment enrollment = new BenefitEnrollment();
                enrollment.setEmpId(employeeID);
                enrollment.setPlanId(planId);
                enrollment.setEnrollmentStatus("ACTIVE");
                enrollment.setEnrollmentDate(LocalDate.now());
                session.persist(enrollment);
            }

            tx.commit();
        } catch (Exception ex) {
            if (tx != null) try { tx.rollback(); } catch (Exception ignored) {}
            throw ex;
        }
    }

    @Override
    public CompensationDTO getCompensationDetails(String employeeID) {
        if (employeeID == null) return null;
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            Employee e = session.get(Employee.class, employeeID);
            if (e == null) return null;
            CompensationDTO dto = new CompensationDTO();
            dto.setEmployeeId(employeeID);
            dto.setBasicPay(e.getBasicPay());
            dto.setGradeLevel(e.getGradeLevel());
            dto.setSalaryBand(e.getSalaryBand());
            dto.setCountryCode(e.getCountryCode());
            dto.setCurrencyCode(e.getCurrencyCode());
            return dto;
        } catch (Exception ex) {
            return null;
        }
    }

    @Override
    public void updateCompensation(CompensationDTO compensation) {
        if (compensation == null || compensation.getEmployeeId() == null) return;
        Transaction tx = null;
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            Employee e = session.get(Employee.class, compensation.getEmployeeId());
            if (e != null) {
                if (compensation.getBasicPay() != null) e.setBasicPay(compensation.getBasicPay());
                if (compensation.getGradeLevel() != null) e.setGradeLevel(compensation.getGradeLevel());
                if (compensation.getSalaryBand() != null) e.setSalaryBand(compensation.getSalaryBand());
                if (compensation.getCountryCode() != null) e.setCountryCode(compensation.getCountryCode());
                if (compensation.getCurrencyCode() != null) e.setCurrencyCode(compensation.getCurrencyCode());
                session.merge(e);
            }
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) try { tx.rollback(); } catch (Exception ignored) {}
            throw ex;
        }
    }

    @Override
    public void ensurePayEquity() {
        // Requires org-wide analytics rules; not implemented at DB layer.
    }

    // ─────────────────────────────────────────────
    // 7. CROSS-BORDER MOBILITY & TRANSFERS
    // ─────────────────────────────────────────────

    @Override
    public void initiateTransfer(String employeeID, String fromCountry, String toCountry) {
        if (employeeID == null || toCountry == null) return;

        Transaction tx = null;
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            Employee e = session.get(Employee.class, employeeID);
            if (e != null) {
                e.setCountryCode(toCountry);
                session.merge(e);
            }
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) try { tx.rollback(); } catch (Exception ignored) {}
            throw ex;
        }

        MobilityDTO mobility = new MobilityDTO();
        mobility.setEmployeeId(employeeID);
        mobility.setFromCountry(fromCountry);
        mobility.setToCountry(toCountry);
        mobility.setInitiatedAt(LocalDateTime.now());
        mobility.setStatus("INITIATED");
        mobilityByEmployee.put(employeeID, mobility);
    }

    @Override
    public MobilityDTO getMobilityDetails(String employeeID) {
        if (employeeID == null) return null;
        MobilityDTO mobility = mobilityByEmployee.get(employeeID);
        if (mobility != null) return mobility;

        // Fallback: derive basic mobility info from current employee country.
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            Employee e = session.get(Employee.class, employeeID);
            if (e == null) return null;
            MobilityDTO dto = new MobilityDTO();
            dto.setEmployeeId(employeeID);
            dto.setToCountry(e.getCountryCode());
            dto.setStatus("UNKNOWN");
            return dto;
        } catch (Exception ex) {
            return null;
        }
    }

    @Override
    public void manageVisa(String employeeID, VisaDTO visa) {
        if (employeeID == null || visa == null) return;
        visaByEmployee.put(employeeID, visa);
    }

    @Override
    public void trackRelocationBenefits(String employeeID) {
        // Relocation benefits model not available in DB schema yet.
    }

    @Override
    public void planSuccession(String employeeID) {
        // Succession planning is handled by Succession Planning subsystem.
    }

    // ─────────────────────────────────────────────
    // 8. CENTRALIZED & DECENTRALIZED CONTROL
    // ─────────────────────────────────────────────

    @Override
    public void enableGlobalPolicy(String policyID) {
        Integer id = parseIntOrNull(policyID);
        if (id == null) return;
        Transaction tx = null;
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            CompliancePolicy p = session.get(CompliancePolicy.class, id);
            if (p != null) {
                p.setIsActive(true);
                session.merge(p);
            }
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) try { tx.rollback(); } catch (Exception ignored) {}
            throw ex;
        }
    }

    @Override
    public void overrideLocalPolicy(String countryCode, String policyID) {
        Integer id = parseIntOrNull(policyID);
        if (countryCode == null || id == null) return;

        Transaction tx = null;
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            CompliancePolicy existing = session.get(CompliancePolicy.class, id);
            if (existing != null) {
                CompliancePolicy cloned = new CompliancePolicy();
                cloned.setPolicyName(existing.getPolicyName());
                cloned.setPolicyCategory(existing.getPolicyCategory());
                cloned.setCountryCode(countryCode);
                cloned.setComplianceScore(existing.getComplianceScore());
                cloned.setViolationSeverity(existing.getViolationSeverity());
                cloned.setViolationDescription(existing.getViolationDescription());
                cloned.setRemediationDueDate(existing.getRemediationDueDate());
                cloned.setIsActive(existing.getIsActive());
                session.persist(cloned);
            }
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) try { tx.rollback(); } catch (Exception ignored) {}
            throw ex;
        }
    }

    @Override
    public boolean validateAccess(String userID, String countryCode) {
        if (userID == null || countryCode == null) return false;
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            AccessPermission perm = session.createQuery(
                            "FROM AccessPermission p WHERE p.userId = :uid ORDER BY p.permissionId DESC",
                            AccessPermission.class)
                    .setParameter("uid", userID)
                    .setMaxResults(1)
                    .uniqueResult();

            if (perm == null) return false;
            if (perm.getUserRole() != null && perm.getUserRole().toUpperCase().contains("ADMIN")) return true;

            String access = perm.getAccessPermissions();
            if (access == null) return false;
            String upper = access.toUpperCase();
            return upper.contains("GLOBAL") || upper.contains(countryCode.toUpperCase());
        } catch (Exception ex) {
            return false;
        }
    }

    @Override
    public void assignRegionalAdmin(String employeeID, String countryCode) {
        if (employeeID == null || countryCode == null) return;
        Transaction tx = null;
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            AccessPermission perm = session.createQuery(
                            "FROM AccessPermission p WHERE p.userId = :uid ORDER BY p.permissionId DESC",
                            AccessPermission.class)
                    .setParameter("uid", employeeID)
                    .setMaxResults(1)
                    .uniqueResult();

            if (perm == null) {
                perm = new AccessPermission();
                perm.setUserId(employeeID);
            }

            perm.setUserRole("REGIONAL_ADMIN");
            perm.setAccessPermissions(addCsvToken(perm.getAccessPermissions(), countryCode));
            session.merge(perm);
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) try { tx.rollback(); } catch (Exception ignored) {}
            throw ex;
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────

    private static Integer parseIntOrNull(String v) {
        if (v == null) return null;
        try {
            return Integer.parseInt(v);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private static String pickWorseSeverity(String currentWorst, String candidate) {
        if (candidate == null) return currentWorst;
        if (currentWorst == null) return candidate;
        return severityRank(candidate) > severityRank(currentWorst) ? candidate : currentWorst;
    }

    private static int severityRank(String s) {
        if (s == null) return 0;
        return switch (s.toUpperCase()) {
            case "CRITICAL" -> 4;
            case "HIGH" -> 3;
            case "MEDIUM" -> 2;
            case "LOW" -> 1;
            default -> 0;
        };
    }

    private static String addCsvToken(String existing, String token) {
        if (token == null || token.isBlank()) return existing;
        String t = token.trim();
        if (existing == null || existing.isBlank()) return t;

        Set<String> parts = new HashSet<>();
        for (String p : existing.split(",")) {
            if (!p.isBlank()) parts.add(p.trim());
        }
        parts.add(t);

        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (String p : parts) {
            if (!first) sb.append(',');
            sb.append(p);
            first = false;
        }
        return sb.toString();
    }
}
