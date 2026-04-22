package com.hrms.db.tools;

import com.hrms.db.config.DatabaseConnection;
import com.hrms.db.entities.*;
import com.hrms.db.facade.HRMSDatabaseFacade;
import com.hrms.db.repositories.docu_management.DocumentType;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * DatabaseSampleDataSeeder
 *
 * Seeds "SAMPLE_*" data across (almost) all mapped tables so subsystem teams
 * can demo and test without manually inserting rows.
 *
 * Idempotent: it only inserts rows if the specific SAMPLE_* keys are missing.
 */
public final class DatabaseSampleDataSeeder {

    private DatabaseSampleDataSeeder() {}

    // --- Core sample IDs ---

    private static final String EMP_1 = "SAMPLE_EMP_001";
    private static final String EMP_2 = "SAMPLE_EMP_002";
    private static final String EMP_3 = "SAMPLE_EMP_003";
    private static final String EMP_4 = "SAMPLE_EMP_004";
    private static final String EMP_5 = "SAMPLE_EMP_005";

    private static final String LEAVE_POLICY_ID = "SAMPLE_LEAVE_POLICY_2026";

    private static final String HOLIDAY_ID = "SAMPLE_HOLIDAY_001";
    private static final String HR_REPORT_ID = "SAMPLE_REPORT_001";
    private static final String DASHBOARD_ID = "SAMPLE_DASH_001";

    private static final String JOB_ID = "SAMPLE_JOB_001";
    private static final String CAND_ID = "SAMPLE_CAND_001";
    private static final String APP_ID = "SAMPLE_APP_001";
    private static final String OFFER_ID = "SAMPLE_OFFER_001";

    private static final String INTERVIEWER_ID = "SAMPLE_INT_001";
    private static final String AVAILABILITY_ID = "SAMPLE_AVAIL_001";
    private static final String SCHEDULE_ID = "SAMPLE_SCHED_001";

    private static final String ONBOARD_TASK_ID = "SAMPLE_ONB_TASK_001";
    private static final String CLEARANCE_ID = "SAMPLE_CLEAR_001";
    private static final String EXIT_INTERVIEW_ID = "SAMPLE_EXIT_001";

    private static final String DOCUMENT_ID = "SAMPLE_DOC_001";
    private static final String DOCUMENT_META_ID = "SAMPLE_DOC_META_001";

    private static final String ATTENDANCE_ID = "SAMPLE_ATT_001";
    private static final String FINANCIAL_ID = "SAMPLE_FIN_001";
    private static final String FILE_REF_ID = "SAMPLE_FILE_REF_001";
    private static final String PAYROLL_ID = "SAMPLE_PAY_001";
    private static final String PAYROLL_LOG_ID = "SAMPLE_PAYLOG_001";

    private static final String PAY_PERIOD = "2026-04";
    private static final String PAY_BATCH = "SAMPLE_BATCH_001";

    private static final String USER_SESSION_ID = "SAMPLE_SESSION_001";

    // Expense claim IDs should be numeric strings so ExpenseRepoMapper can parse them.
    private static final String EXP_CLAIM_ID_1 = "1001";
    private static final String EXP_CLAIM_ID_2 = "1002";

    public static void main(String[] args) {
        System.out.println("[DatabaseSampleDataSeeder] Starting...");
        System.out.println("[DatabaseSampleDataSeeder] SQLite file (relative): hrms.db");
        System.out.println("[DatabaseSampleDataSeeder] Absolute path: " + Paths.get("hrms.db").toAbsolutePath());

        HRMSDatabaseFacade.getInstance().initialize();

        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            seedAll(session);
            printSummary(session);
            System.out.println("[DatabaseSampleDataSeeder] OK");
        } catch (Exception ex) {
            System.err.println("[DatabaseSampleDataSeeder] FAILED: " + ex.getMessage());
            ex.printStackTrace(System.err);
            System.exit(1);
        } finally {
            HRMSDatabaseFacade.getInstance().shutdown();
        }
    }

    private static void seedAll(Session session) {
        Transaction tx = null;
        try {
            tx = session.beginTransaction();

            // --- Reference data (multi-country) ---
            ensureCurrency(session, "INR", "₹", 2);
            ensureCurrency(session, "USD", "$", 2);

            ensureCountry(session, "IN", "IND", "India", "INR", "en-IN", "Asia/Kolkata", "+05:30", "APAC", "Asia Pacific");
            ensureCountry(session, "US", "USA", "United States", "USD", "en-US", "America/New_York", "-05:00", "NAM", "North America");

            ensureLocale(session, "en-IN", "English (India)", "dd-MM-yyyy", "#,##0.00", "¤ #,##0.00", "MON");
            ensureLocale(session, "en-US", "English (US)", "MM/dd/yyyy", "#,##0.00", "¤ #,##0.00", "SUN");

            ensureExchangeRate(session, "USD", "INR", 83.25);
            ensureTaxRegime(session, "India FY2026", "IN", "2025-2026");
            ensureCompliancePolicy(session, "India PF Compliance", "STATUTORY", "IN");

            ensurePublicHoliday(session, "IN", "Republic Day", LocalDate.of(2026, 1, 26));

            Holiday holiday = session.get(Holiday.class, HOLIDAY_ID);
            if (holiday == null) {
                holiday = new Holiday();
                holiday.setHolidayId(HOLIDAY_ID);
                holiday.setHolidayName("Company Foundation Day");
                holiday.setHolidayDate(LocalDate.of(2026, 4, 15));
                holiday.setApplicableLocation("Bangalore");
                holiday.setHolidayYear(2026);
                holiday.setCountryCode("IN");
                session.persist(holiday);
            }

            // --- Core Employees ---
            Employee manager = ensureEmployee(session, EMP_1, "Asha Rao", "asha.rao@example.com", "HR", "HR Manager", "ADMIN", null);
            Employee eng1 = ensureEmployee(session, EMP_2, "Bala Kumar", "bala.kumar@example.com", "Engineering", "Software Engineer", "EMPLOYEE", manager);
            Employee fin1 = ensureEmployee(session, EMP_3, "Charu Iyer", "charu.iyer@example.com", "Finance", "Accountant", "EMPLOYEE", manager);
            Employee ops1 = ensureEmployee(session, EMP_4, "Dev Sharma", "dev.sharma@example.com", "Operations", "Ops Associate", "EMPLOYEE", manager);
            Employee intern = ensureEmployee(session, EMP_5, "Esha N", "esha.n@example.com", "Engineering", "Intern", "EMPLOYEE", eng1);

            // --- Security ---
            ensureAccessPermission(session, manager.getEmpId(), "ADMIN", "*", "OK");
            ensureAccessPermission(session, eng1.getEmpId(), "EMPLOYEE", "EXPENSE_READ,EXPENSE_WRITE,LEAVE_READ", "OK");

            ensureUserSession(session, USER_SESSION_ID, manager.getEmpId(), manager.getEmail(), "SAMPLE_TOKEN_001");

            SecurityAuditLog secSeedLog = new SecurityAuditLog();
            secSeedLog.setUserId(manager.getEmpId());
            secSeedLog.setActionType("SAMPLE_DATA");
            secSeedLog.setAction("SEED");
            secSeedLog.setOperation("DatabaseSampleDataSeeder.seedAll");
            secSeedLog.setOutcome("SUCCESS");
            secSeedLog.setDetails("Inserted/verified SAMPLE_* rows");
            secSeedLog.setIpAddress("127.0.0.1");
            secSeedLog.setTimestamp(LocalDateTime.now());
            session.persist(secSeedLog);

            // --- Benefits ---
            BenefitPlan health = ensureBenefitPlan(session,
                    "SAMPLE_HEALTH_BASIC",
                    "HEALTH",
                    "Basic health coverage (inpatient + outpatient)",
                    1500.0,
                    "Acme Health",
                    500000.0);

            BenefitPlan retirement = ensureBenefitPlan(session,
                    "SAMPLE_RETIREMENT_PLUS",
                    "RETIREMENT",
                    "Retirement plan with employer match",
                    900.0,
                    "Acme Retirement",
                    0.0);

            ensureBenefitPolicy(session, "SAMPLE_BENEFIT_POLICY_001", "Eligibility: full-time employees", "ALL", "FULL_TIME");

            ensureBenefitEnrollment(session, eng1.getEmpId(), health.getPlanId(), "ACTIVE", LocalDate.now().minusMonths(2));
            ensureBenefitEnrollment(session, fin1.getEmpId(), retirement.getPlanId(), "ACTIVE", LocalDate.now().minusMonths(3));

            ensureBenefitDeduction(session, eng1.getEmpId(), health.getPlanId(), 500.0, "MONTHLY", LocalDate.now().minusDays(10));
            ensureBenefitDeduction(session, fin1.getEmpId(), retirement.getPlanId(), 750.0, "MONTHLY", LocalDate.now().minusDays(10));

            // --- Leave Management ---
            ensureLeavePolicy(session);
            ensureLeaveBalance(session, eng1, "ANNUAL", 24.0, 4.0);
            ensureLeaveBalance(session, eng1, "SICK", 12.0, 1.0);
            ensureLeaveBalance(session, fin1, "ANNUAL", 24.0, 2.0);

            ensureLeaveRecord(session, "SAMPLE_LEAVE_001", eng1, LocalDate.now().minusDays(20), LocalDate.now().minusDays(18), "CASUAL", "APPROVED");
            ensureLeaveRecord(session, "SAMPLE_LEAVE_002", fin1, LocalDate.now().minusDays(5), LocalDate.now().minusDays(4), "SICK", "APPROVED");

            // --- Time Tracking ---
            WorkPolicy wp = ensureWorkPolicy(session, "SAMPLE_WORK_POLICY_STD", 8.0, 8.0, 1.0);

            TimeEntry te = ensureTimeEntry(session, eng1, LocalDate.now().minusDays(1));
            session.flush(); // ensure entry_id generated

            ensureBreakRecord(session, te.getEntryId(), LocalDateTime.now().minusDays(1).withHour(13).withMinute(0), LocalDateTime.now().minusDays(1).withHour(13).withMinute(30));
            ensureOvertimeRecord(session, eng1.getEmpId(), te.getEntryId(), 1.5, "APPROVED");

            ensureAttendance(session, ATTENDANCE_ID, eng1, PAY_PERIOD, 22, 1, 0, 160.0, 2.0);

            // --- Payroll ---
            ensureFinancial(session, FINANCIAL_ID, eng1, PAY_PERIOD, 1200.0, 500.0, 1500.0, 20000.0);
            ensurePayrollResult(session, PAYROLL_ID, eng1, PAY_BATCH, PAY_PERIOD, 100000.0, 85000.0, 85000.0);
            ensurePayrollAuditLog(session, PAYROLL_LOG_ID, PAY_BATCH, eng1, "CALCULATE", "payroll-bot", null);
            ensureFileReference(session, FILE_REF_ID, eng1, PAY_PERIOD,
                    "payslips/" + eng1.getEmpId() + "-" + PAY_PERIOD + ".pdf",
                    "pf/" + PAY_PERIOD + "-challan.pdf",
                    "tax/" + PAY_PERIOD + "-report.pdf");

            // --- Recruitment ---
            ensureJobPosting(session);
            ensureCandidate(session);
            ensureApplicationAndScreening(session);
            ensureInterviewer(session);
            ensureOffer(session);

            // --- Performance ---
            Goal g = ensureGoal(session, eng1.getEmpId());
            session.flush();
            ensureKpi(session, g.getGoalId(), eng1.getEmpId());
            ensureAppraisal(session, eng1.getEmpId(), manager.getEmpId());
            ensureFeedback(session, eng1.getEmpId(), manager.getEmpId());
            SkillGap sg = ensureSkillGap(session, eng1.getEmpId());
            session.flush();

            // --- Succession Planning ---
            CriticalRole cr = ensureCriticalRole(session);
            session.flush();

            ensureSuccessionPoolEntry(session, eng1.getEmpId(), true, "High potential engineer");
            ensureReadinessScore(session, eng1.getEmpId(), cr.getRoleId(), 4.2, "System Design", 0.78, true, "Needs leadership exposure");

            SuccessorAssignment sa = ensureSuccessorAssignment(session, eng1.getEmpId(), cr.getRoleId(), 1, "PROVISIONAL", LocalDate.now().minusDays(30), false);
            session.flush();

            DevelopmentPlan dp = ensureDevelopmentPlan(session, eng1.getEmpId(), sa.getAssignmentId(), cr.getRoleId(), sg.getSkillGapId(), 25.0);
            session.flush();

            ensurePlanTask(session, dp.getPlanId(), "Complete leadership training module", LocalDate.now().plusDays(60), "IN_PROGRESS");
            ensureRiskLog(session, cr.getRoleId(), "MEDIUM", "Role is critical; only 1 successor in pipeline.");
            ensureExternalHireRequest(session, cr.getRoleId(), "PENDING", 0);
            ensureSuccessionAuditLog(session, "CriticalRole", cr.getRoleId(), "CREATE", manager.getEmail(), "Seeded sample critical role");

            // --- Expense Management ---
            ensureDepartmentBudget(session, "Engineering", 250000.0, 32000.0);
            ensureDepartmentBudget(session, "Finance", 150000.0, 15000.0);

            ensureExpenseCategory(session, "TRAVEL");
            ensureExpenseCategory(session, "MEALS");
            ensureExpenseCategory(session, "ACCOMMODATION");
            ensureExpenseCategory(session, "SUPPLIES");

            ensureExpenseClaim(session, EXP_CLAIM_ID_1, eng1, 1200.0, LocalDate.now().minusDays(20), "PENDING", null, "TRAVEL");
            ensureExpenseClaim(session, EXP_CLAIM_ID_2, fin1, 650.0, LocalDate.now().minusDays(7), "APPROVED", manager, "MEALS");

            ensureReceipt(session, EXP_CLAIM_ID_1, "receipts/" + EXP_CLAIM_ID_1 + ".jpg", "receipt-" + EXP_CLAIM_ID_1 + ".jpg");
            ensureClaimApproval(session, EXP_CLAIM_ID_2, manager.getEmpId(), "APPROVED", "Approved in sample seeding");
            ensureExpenseAudit(session, EXP_CLAIM_ID_1, eng1, "SUBMIT", "Claim submitted (seed)", null);

            // --- Onboarding / Offboarding / Documents ---
            ensureOnboardingTask(session, ONBOARD_TASK_ID, intern, "Submit ID proofs", "DOCUMENT_VERIFY", "HR", "IN_PROGRESS", LocalDate.now().plusDays(7), "ONBOARDING");

            ensureClearanceSettlement(session, CLEARANCE_ID, fin1.getEmpId(), 45000.0, "PENDING", "PENDING", "RESIGNATION", "Awaiting asset clearance");
            ensureExitInterview(session, EXIT_INTERVIEW_ID, fin1.getEmpId(), "Better opportunity", "Great team, looking for growth", 4,
                    "Compensation, career progression", "Recommend retention plan improvements", LocalDate.now().minusDays(2));

            ensureNotification(session, manager.getEmpId(), "SAMPLE", "Sample data seeded", "SAMPLE DATA", false);
            ensureNotification(session, eng1.getEmpId(), "EXPENSE", "You have 1 pending expense claim", "Expense reminder", false);

            ensureDocument(session, DOCUMENT_ID, eng1, "CONTRACT", "docs/contracts/" + eng1.getEmpId() + ".pdf", LocalDate.now().minusMonths(6), "VERIFIED");
            ensureDocumentMetadata(session, DOCUMENT_META_ID, eng1.getEmpId(), "Employment Contract", "docs/contracts/" + eng1.getEmpId() + ".pdf",
                    1, System.currentTimeMillis() - 1000L * 60 * 60 * 24 * 30, System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 365, DocumentType.CONTRACT);
            ensureDocumentAudit(session, "UPLOAD", DOCUMENT_ID, eng1.getEmpId(), manager.getEmail());

            // --- Customization / Workflows / Analytics ---
            CustomModule cm = ensureCustomModule(session, "SAMPLE_MODULE_EXPENSE", "FEATURE_FLAG", true);
            session.flush();
            CustomForm cf = ensureCustomForm(session, "SAMPLE_FORM_EXPENSE_CLAIM", cm.getModuleId(), "SINGLE_PAGE");
            session.flush();
            ensureCustomField(session, cf.getFormId(), "SAMPLE_FIELD_PROJECT_CODE", "TEXT", false, "PRJ-001");

            Workflow wf = ensureWorkflow(session, "SAMPLE_WORKFLOW_EXPENSE_APPROVAL", "ACTIVE", manager.getEmail());
            session.flush();
            ensureWorkflowTask(session, wf.getWorkflowId(), "Manager approval", 1, "PENDING");
            ensureWorkflowTask(session, wf.getWorkflowId(), "Finance reimbursement", 2, "PENDING");

            ensureDashboardConfig(session, DASHBOARD_ID, "HR Overview", manager.getEmpId(),
                    "{\"department\":\"ALL\"}",
                    "[\"headcount\",\"attrition\",\"payroll\"]",
                    "LAST_30_DAYS");

            ensureHrReport(session, HR_REPORT_ID, "Monthly Headcount", "HEADCOUNT", LocalDate.now().minusDays(1), "CSV",
                    "reports/headcount-" + LocalDate.now() + ".csv", "MONTHLY");

            tx.commit();

        } catch (Exception ex) {
            if (tx != null) {
                try {
                    tx.rollback();
                } catch (Exception ignored) {
                }
            }
            throw ex;
        }
    }

    // --- Summary ---

    private static void printSummary(Session session) {
        Long employees = session.createQuery("SELECT COUNT(e) FROM Employee e", Long.class).uniqueResult();
        Long claims = session.createQuery("SELECT COUNT(c) FROM ExpenseClaim c", Long.class).uniqueResult();
        Long receipts = session.createQuery("SELECT COUNT(r) FROM Receipt r", Long.class).uniqueResult();
        Long jobs = session.createQuery("SELECT COUNT(j) FROM JobPosting j", Long.class).uniqueResult();

        System.out.println("[DatabaseSampleDataSeeder] Summary counts:");
        System.out.println("  employees      = " + (employees != null ? employees : 0));
        System.out.println("  expense_claims = " + (claims != null ? claims : 0));
        System.out.println("  receipts       = " + (receipts != null ? receipts : 0));
        System.out.println("  job_postings   = " + (jobs != null ? jobs : 0));
    }

    // --- Helpers: Reference data ---

    private static Currency ensureCurrency(Session session, String code, String symbol, int precision) {
        Currency c = session.get(Currency.class, code);
        if (c == null) {
            c = new Currency();
            c.setCurrencyCode(code);
            c.setCurrencySymbol(symbol);
            c.setCurrencyDecimalPrecision(precision);
            session.persist(c);
        }
        return c;
    }

    private static Country ensureCountry(Session session,
                                        String code2,
                                        String code3,
                                        String name,
                                        String defaultCurrency,
                                        String defaultLocale,
                                        String timezone,
                                        String utcOffset,
                                        String regionCode,
                                        String regionName) {
        Country c = session.get(Country.class, code2);
        if (c == null) {
            c = new Country();
            c.setCountryCode(code2);
            c.setCountryCodeAlpha3(code3);
            c.setCountryName(name);
            c.setDefaultCurrencyCode(defaultCurrency);
            c.setDefaultLocale(defaultLocale);
            c.setTimezone(timezone);
            c.setUtcOffset(utcOffset);
            c.setRegionCode(regionCode);
            c.setRegionName(regionName);
            c.setIsActive(true);
            session.persist(c);
        }
        return c;
    }

    private static LocaleConfig ensureLocale(Session session,
                                            String localeCode,
                                            String language,
                                            String dateFmt,
                                            String numberFmt,
                                            String currencyFmt,
                                            String firstDay) {
        LocaleConfig lc = session.get(LocaleConfig.class, localeCode);
        if (lc == null) {
            lc = new LocaleConfig();
            lc.setLocaleCode(localeCode);
            lc.setLanguageName(language);
            lc.setDateFormatPattern(dateFmt);
            lc.setNumberFormatPattern(numberFmt);
            lc.setCurrencyFormatPattern(currencyFmt);
            lc.setFirstDayOfWeek(firstDay);
            session.persist(lc);
        }
        return lc;
    }

    private static void ensureExchangeRate(Session session, String base, String target, double rate) {
        ExchangeRate existing = session.createQuery(
                        "FROM ExchangeRate r WHERE r.baseCurrencyCode = :b AND r.targetCurrencyCode = :t",
                        ExchangeRate.class)
                .setParameter("b", base)
                .setParameter("t", target)
                .setMaxResults(1)
                .uniqueResult();

        if (existing == null) {
            ExchangeRate r = new ExchangeRate();
            r.setBaseCurrencyCode(base);
            r.setTargetCurrencyCode(target);
            r.setExchangeRate(rate);
            r.setRateEffectiveDate(LocalDate.now().minusDays(3));
            r.setFxRateSource("MANUAL");
            r.setManualOverrideFlag(true);
            r.setRateFluctuationThresholdPct(5.0);
            session.persist(r);
        }
    }

    private static void ensureTaxRegime(Session session, String name, String countryCode, String fiscalYear) {
        TaxRegime existing = session.createQuery(
                        "FROM TaxRegime t WHERE t.taxRegimeName = :n",
                        TaxRegime.class)
                .setParameter("n", name)
                .setMaxResults(1)
                .uniqueResult();

        if (existing == null) {
            TaxRegime t = new TaxRegime();
            t.setTaxRegimeName(name);
            t.setCountryCode(countryCode);
            t.setFiscalYear(fiscalYear);
            t.setIncomeSlabMin(0.0);
            t.setIncomeSlabMax(1000000.0);
            t.setTaxRatePct(10.0);
            t.setStatutoryDeductionName("PF");
            t.setDeductionCalcBasis("BASIC");
            t.setDeductionEmployeeRate(12.0);
            t.setDeductionEmployerRate(12.0);
            t.setDeductionIsActive(true);
            t.setFilingDeadlineDate(LocalDate.of(2026, 7, 31));
            session.persist(t);
        }
    }

    private static void ensureCompliancePolicy(Session session, String policyName, String category, String countryCode) {
        CompliancePolicy existing = session.createQuery(
                        "FROM CompliancePolicy p WHERE p.policyName = :n",
                        CompliancePolicy.class)
                .setParameter("n", policyName)
                .setMaxResults(1)
                .uniqueResult();

        if (existing == null) {
            CompliancePolicy p = new CompliancePolicy();
            p.setPolicyName(policyName);
            p.setPolicyCategory(category);
            p.setCountryCode(countryCode);
            p.setComplianceScore(95);
            p.setViolationSeverity("LOW");
            p.setViolationDescription("No violations in sample data");
            p.setRemediationDueDate(LocalDate.now().plusDays(30));
            p.setIsActive(true);
            session.persist(p);
        }
    }

    private static void ensurePublicHoliday(Session session, String countryCode, String name, LocalDate date) {
        PublicHoliday existing = session.createQuery(
                        "FROM PublicHoliday h WHERE h.countryCode = :c AND h.holidayDate = :d",
                        PublicHoliday.class)
                .setParameter("c", countryCode)
                .setParameter("d", date)
                .setMaxResults(1)
                .uniqueResult();

        if (existing == null) {
            PublicHoliday h = new PublicHoliday();
            h.setCountryCode(countryCode);
            h.setHolidayName(name);
            h.setHolidayDate(date);
            h.setIsMandatory(true);
            session.persist(h);
        }
    }

    // --- Helpers: Employees + Security ---

    private static Employee ensureEmployee(Session session,
                                          String empId,
                                          String name,
                                          String email,
                                          String department,
                                          String designation,
                                          String role,
                                          Employee manager) {
        Employee e = session.get(Employee.class, empId);
        if (e == null) {
            e = new Employee();
            e.setEmpId(empId);
            e.setName(name);
            e.setEmail(email);
            e.setDepartment(department);
            e.setDesignation(designation);
            e.setRole(role);
            e.setEmploymentStatus("ACTIVE");
            e.setEmploymentType("FULL_TIME");
            e.setOfficeLocation("Bangalore");
            e.setCountryCode("IN");
            e.setCurrencyCode("INR");
            e.setTaxRegime("India FY2026");
            e.setDateOfJoining(LocalDate.now().minusYears(1));
            e.setBasicPay(60000.0);
            e.setYearsOfService(1);
            e.setManager(manager);
            session.persist(e);
        }
        return e;
    }

    private static void ensureAccessPermission(Session session, String userId, String role, String perms, String compliance) {
        AccessPermission existing = session.createQuery(
                        "FROM AccessPermission p WHERE p.userId = :u",
                        AccessPermission.class)
                .setParameter("u", userId)
                .setMaxResults(1)
                .uniqueResult();

        if (existing == null) {
            AccessPermission p = new AccessPermission();
            p.setUserId(userId);
            p.setUserRole(role);
            p.setAccessPermissions(perms);
            p.setComplianceStatus(compliance);
            session.persist(p);
        }
    }

    private static void ensureUserSession(Session session, String sessionId, String userId, String username, String token) {
        UserSession existing = session.get(UserSession.class, sessionId);
        if (existing == null) {
            UserSession s = new UserSession();
            s.setSessionId(sessionId);
            s.setUserId(userId);
            s.setUsername(username);
            s.setSessionToken(token);
            s.setLoginTimestamp(LocalDateTime.now().minusMinutes(10));
            s.setIpAddress("127.0.0.1");
            s.setIsActive(true);
            session.persist(s);
        }
    }

    // --- Helpers: Benefits ---

    private static BenefitPlan ensureBenefitPlan(Session session,
                                                String name,
                                                String type,
                                                String details,
                                                Double cost,
                                                String provider,
                                                Double coverageLimit) {
        BenefitPlan existing = session.createQuery(
                        "FROM BenefitPlan p WHERE p.planName = :n",
                        BenefitPlan.class)
                .setParameter("n", name)
                .setMaxResults(1)
                .uniqueResult();

        if (existing == null) {
            BenefitPlan p = new BenefitPlan();
            p.setPlanName(name);
            p.setPlanType(type);
            p.setCoverageDetails(details);
            p.setCost(cost);
            p.setProviderName(provider);
            p.setCoverageLimit(coverageLimit);
            p.setPlanDuration("ANNUAL");
            p.setPlanEligibilityCriteria("Full-time employees");
            session.persist(p);
            session.flush();
            return p;
        }
        return existing;
    }

    private static void ensureBenefitPolicy(Session session,
                                           String name,
                                           String eligibilityRules,
                                           String salaryBand,
                                           String empTypeRestrictions) {
        BenefitPolicy existing = session.createQuery(
                        "FROM BenefitPolicy p WHERE p.policyName = :n",
                        BenefitPolicy.class)
                .setParameter("n", name)
                .setMaxResults(1)
                .uniqueResult();

        if (existing == null) {
            BenefitPolicy p = new BenefitPolicy();
            p.setPolicyName(name);
            p.setEligibilityRules(eligibilityRules);
            p.setSalaryBandCriteria(salaryBand);
            p.setEmploymentTypeRestrictions(empTypeRestrictions);
            p.setWaitingPeriodRules("30 days");
            p.setMaximumCoverageRules("As per plan");
            p.setPolicyEffectiveDate(LocalDate.now().minusMonths(6));
            p.setPolicyLastUpdated(LocalDate.now().minusDays(1));
            p.setIsActive(true);
            session.persist(p);
        }
    }

    private static void ensureBenefitEnrollment(Session session, String empId, Integer planId, String status, LocalDate date) {
        BenefitEnrollment existing = session.createQuery(
                        "FROM BenefitEnrollment e WHERE e.empId = :emp AND e.planId = :pid",
                        BenefitEnrollment.class)
                .setParameter("emp", empId)
                .setParameter("pid", planId)
                .setMaxResults(1)
                .uniqueResult();

        if (existing == null) {
            BenefitEnrollment e = new BenefitEnrollment();
            e.setEmpId(empId);
            e.setPlanId(planId);
            e.setEnrollmentStatus(status);
            e.setEnrollmentDate(date);
            e.setDependentsInformation("[]");
            session.persist(e);
        }
    }

    private static void ensureBenefitDeduction(Session session, String empId, Integer planId, Double amount, String cycle, LocalDate date) {
        BenefitDeduction existing = session.createQuery(
                        "FROM BenefitDeduction d WHERE d.empId = :emp AND d.planId = :pid AND d.payrollCycle = :cy",
                        BenefitDeduction.class)
                .setParameter("emp", empId)
                .setParameter("pid", planId)
                .setParameter("cy", cycle)
                .setMaxResults(1)
                .uniqueResult();

        if (existing == null) {
            BenefitDeduction d = new BenefitDeduction();
            d.setEmpId(empId);
            d.setPlanId(planId);
            d.setDeductionAmount(amount);
            d.setPayrollCycle(cycle);
            d.setDeductionDate(date);
            session.persist(d);
        }
    }

    // --- Helpers: Leave ---

    private static void ensureLeavePolicy(Session session) {
        LeavePolicy existing = session.get(LeavePolicy.class, LEAVE_POLICY_ID);
        if (existing == null) {
            LeavePolicy p = new LeavePolicy();
            p.setPolicyId(LEAVE_POLICY_ID);
            p.setPolicyAppliesTo("ALL");
            p.setEffectiveYear(2026);
            p.setAnnualLeaveMaxDays(24);
            p.setAnnualLeaveMaxCarryForward(6);
            p.setAnnualLeaveMinNoticeDays(3);
            p.setAnnualLeaveMaxConsecutive(10);
            p.setSickLeaveMaxDays(12);
            p.setSickLeaveCarryForwardAllowed(false);
            p.setSickLeaveDocRequiredAfter(2);
            p.setCasualLeaveMaxDays(12);
            p.setCasualLeaveCarryForwardAllowed(false);
            p.setCasualLeaveHalfDayAllowed(true);
            session.persist(p);
        }
    }

    private static void ensureLeaveBalance(Session session, Employee emp, String leaveType, Double entitled, Double used) {
        LeaveBalance existing = session.createQuery(
                        "FROM LeaveBalance b WHERE b.employee.empId = :e AND b.leaveType = :t",
                        LeaveBalance.class)
                .setParameter("e", emp.getEmpId())
                .setParameter("t", leaveType)
                .setMaxResults(1)
                .uniqueResult();

        if (existing == null) {
            LeaveBalance b = new LeaveBalance();
            b.setEmployee(emp);
            b.setLeaveType(leaveType);
            b.setTotalEntitled(entitled);
            b.setUsed(used);
            b.setBalance(entitled - used);
            b.setCarryForward(0.0);
            session.persist(b);
        }
    }

    private static void ensureLeaveRecord(Session session,
                                         String leaveId,
                                         Employee emp,
                                         LocalDate start,
                                         LocalDate end,
                                         String type,
                                         String status) {
        LeaveRecord existing = session.get(LeaveRecord.class, leaveId);
        if (existing == null) {
            LeaveRecord lr = new LeaveRecord();
            lr.setLeaveId(leaveId);
            lr.setEmployee(emp);
            lr.setStartDate(start);
            lr.setEndDate(end);
            lr.setLeaveType(type);
            lr.setStatus(status);
            session.persist(lr);
        }
    }

    // --- Helpers: Time Tracking ---

    private static WorkPolicy ensureWorkPolicy(Session session, String name, Double stdHours, Double overtimeThreshold, Double maxBreakHours) {
        WorkPolicy existing = session.createQuery(
                        "FROM WorkPolicy p WHERE p.policyName = :n",
                        WorkPolicy.class)
                .setParameter("n", name)
                .setMaxResults(1)
                .uniqueResult();

        if (existing == null) {
            WorkPolicy p = new WorkPolicy();
            p.setPolicyName(name);
            p.setStandardWorkHours(stdHours);
            p.setOvertimeThreshold(overtimeThreshold);
            p.setMaxBreakDuration(maxBreakHours);
            session.persist(p);
            session.flush();
            return p;
        }
        return existing;
    }

    private static TimeEntry ensureTimeEntry(Session session, Employee emp, LocalDate date) {
        // Only create 1 entry per employee per day in sample set.
        TimeEntry existing = session.createQuery(
                        "FROM TimeEntry t WHERE t.employee.empId = :e AND t.date = :d",
                        TimeEntry.class)
                .setParameter("e", emp.getEmpId())
                .setParameter("d", date)
                .setMaxResults(1)
                .uniqueResult();

        if (existing == null) {
            TimeEntry t = new TimeEntry();
            t.setEmployee(emp);
            t.setDate(date);
            t.setPunchInTime(LocalDateTime.of(date, LocalTime.of(9, 30)));
            t.setPunchOutTime(LocalDateTime.of(date, LocalTime.of(18, 15)));
            t.setTotalHours(8.75);
            t.setStatus("PRESENT");
            session.persist(t);
            return t;
        }
        return existing;
    }

    private static void ensureBreakRecord(Session session, Long entryId, LocalDateTime start, LocalDateTime end) {
        BreakRecord existing = session.createQuery(
                        "FROM BreakRecord b WHERE b.entryId = :eid",
                        BreakRecord.class)
                .setParameter("eid", entryId)
                .setMaxResults(1)
                .uniqueResult();

        if (existing == null) {
            BreakRecord b = new BreakRecord();
            b.setEntryId(entryId);
            b.setBreakStartTime(start);
            b.setBreakEndTime(end);
            session.persist(b);
        }
    }

    private static void ensureOvertimeRecord(Session session, String empId, Long entryId, Double hours, String approvalStatus) {
        OvertimeRecord existing = session.createQuery(
                        "FROM OvertimeRecord o WHERE o.empId = :e AND o.entryId = :id",
                        OvertimeRecord.class)
                .setParameter("e", empId)
                .setParameter("id", entryId)
                .setMaxResults(1)
                .uniqueResult();

        if (existing == null) {
            OvertimeRecord o = new OvertimeRecord();
            o.setEmpId(empId);
            o.setEntryId(entryId);
            o.setOvertimeHours(hours);
            o.setApprovalStatus(approvalStatus);
            session.persist(o);
        }
    }

    private static void ensureAttendance(Session session,
                                         String recordId,
                                         Employee emp,
                                         String payPeriod,
                                         Integer workingDays,
                                         Integer leaveWithPay,
                                         Integer leaveWithoutPay,
                                         Double hoursWorked,
                                         Double overtimeHours) {
        Attendance existing = session.get(Attendance.class, recordId);
        if (existing == null) {
            Attendance a = new Attendance();
            a.setRecordId(recordId);
            a.setEmployee(emp);
            a.setPayPeriod(payPeriod);
            a.setWorkingDaysInMonth(workingDays);
            a.setLeaveWithPay(leaveWithPay);
            a.setLeaveWithoutPay(leaveWithoutPay);
            a.setHoursWorked(hoursWorked);
            a.setOvertimeHours(overtimeHours);
            session.persist(a);
        }
    }

    // --- Helpers: Payroll ---

    private static void ensureFinancial(Session session,
                                        String recordId,
                                        Employee emp,
                                        String payPeriod,
                                        Double pendingClaims,
                                        Double approvedReimb,
                                        Double insurancePremium,
                                        Double investments) {
        Financial existing = session.get(Financial.class, recordId);
        if (existing == null) {
            Financial f = new Financial();
            f.setRecordId(recordId);
            f.setEmployee(emp);
            f.setPayPeriod(payPeriod);
            f.setPendingClaims(pendingClaims);
            f.setApprovedReimbursement(approvedReimb);
            f.setInsurancePremium(insurancePremium);
            f.setDeclaredInvestments(investments);
            session.persist(f);
        }
    }

    private static void ensurePayrollResult(Session session,
                                            String recordId,
                                            Employee emp,
                                            String batchId,
                                            String payPeriod,
                                            Double gross,
                                            Double net,
                                            Double payout) {
        PayrollResult existing = session.get(PayrollResult.class, recordId);
        if (existing == null) {
            PayrollResult p = new PayrollResult();
            p.setRecordId(recordId);
            p.setEmployee(emp);
            p.setBatchId(batchId);
            p.setPayPeriod(payPeriod);
            p.setFinalGrossPay(gross);
            p.setFinalNetPay(net);
            p.setPayoutAmount(payout);
            p.setPenaltyAmount(0.0);
            p.setPfAmount(1200.0);
            p.setPtAmount(200.0);
            p.setMonthlyTdsAmount(1500.0);
            p.setOvertimePay(500.0);
            p.setReimbursementPayout(approvedReimbursementForEmp(session, emp.getEmpId(), payPeriod));
            p.setGratuityAmount(0.0);
            session.persist(p);
        }
    }

    private static Double approvedReimbursementForEmp(Session session, String empId, String payPeriod) {
        Financial f = session.createQuery(
                        "FROM Financial f WHERE f.employee.empId = :e AND f.payPeriod = :p",
                        Financial.class)
                .setParameter("e", empId)
                .setParameter("p", payPeriod)
                .setMaxResults(1)
                .uniqueResult();
        return f != null ? f.getApprovedReimbursement() : 0.0;
    }

    private static void ensurePayrollAuditLog(Session session,
                                              String logId,
                                              String batchId,
                                              Employee emp,
                                              String actionType,
                                              String performedBy,
                                              String errorMsg) {
        PayrollAuditLog existing = session.get(PayrollAuditLog.class, logId);
        if (existing == null) {
            PayrollAuditLog l = new PayrollAuditLog();
            l.setLogId(logId);
            l.setBatchId(batchId);
            l.setEmployee(emp);
            l.setActionType(actionType);
            l.setPerformedBy(performedBy);
            l.setErrorMsg(errorMsg);
            session.persist(l);
        }
    }

    private static void ensureFileReference(Session session,
                                            String refId,
                                            Employee emp,
                                            String payPeriod,
                                            String payslip,
                                            String pfChallan,
                                            String taxReport) {
        FileReference existing = session.get(FileReference.class, refId);
        if (existing == null) {
            FileReference r = new FileReference();
            r.setRefId(refId);
            r.setEmployee(emp);
            r.setPayPeriod(payPeriod);
            r.setPayslipPdfPath(payslip);
            r.setPfChallanPath(pfChallan);
            r.setTaxReportPath(taxReport);
            session.persist(r);
        }
    }

    // --- Helpers: Recruitment ---

    private static void ensureJobPosting(Session session) {
        JobPosting job = session.get(JobPosting.class, JOB_ID);
        if (job == null) {
            job = new JobPosting();
            job.setJobId(JOB_ID);
            job.setTitle("Software Engineer (Backend)");
            job.setDepartment("Engineering");
            job.setDescription("Build services for HRMS platform.");
            job.setSalary(1200000.0);
            job.setStatus("OPEN");
            job.setPlatformName("Internal Portal");
            job.setChannelType("PORTAL");
            session.persist(job);
        }
    }

    private static void ensureCandidate(Session session) {
        Candidate c = session.get(Candidate.class, CAND_ID);
        if (c == null) {
            c = new Candidate();
            c.setCandidateId(CAND_ID);
            c.setName("Fahad Khan");
            c.setContactInfo("fahad.khan@example.com");
            c.setResumePath("resumes/" + CAND_ID + ".pdf");
            c.setSkills("Java, Hibernate, SQL, REST");
            c.setSource("LINKEDIN");
            c.setStatus("ACTIVE");
            session.persist(c);
        }
    }

    private static void ensureApplicationAndScreening(Session session) {
        Application a = session.get(Application.class, APP_ID);
        if (a == null) {
            a = new Application();
            a.setApplicationId(APP_ID);
            a.setCandidateId(CAND_ID);
            a.setJobId(JOB_ID);
            a.setDateApplied(LocalDate.now().minusDays(10));
            a.setCurrentStage("SCREENING");
            a.setHistory("Applied -> Screening");
            a.setTimestamp(LocalDateTime.now().minusDays(9));
            a.setStatus("ACTIVE");
            a.setScore(78);
            a.setRanking(5);
            a.setShortlistStatus("SHORTLISTED");
            session.persist(a);
        }

        ScreeningResult sr = session.get(ScreeningResult.class, APP_ID);
        if (sr == null) {
            sr = new ScreeningResult();
            sr.setApplicationId(APP_ID);
            sr.setScore(78);
            sr.setRanking(5);
            sr.setShortlistStatus("SHORTLISTED");
            sr.setStatus("ACTIVE");
            session.persist(sr);
        }

        InterviewerProfile ip = session.get(InterviewerProfile.class, INTERVIEWER_ID);
        if (ip == null) {
            ip = new InterviewerProfile();
            ip.setInterviewerId(INTERVIEWER_ID);
            ip.setName("Asha Rao");
            ip.setDepartment("Engineering");
            ip.setExpertise("Backend, System Design");
            ip.setContact("asha.rao@example.com");
            ip.setStatus("ACTIVE");
            session.persist(ip);
        }

        InterviewerAvailability ia = session.get(InterviewerAvailability.class, AVAILABILITY_ID);
        if (ia == null) {
            ia = new InterviewerAvailability();
            ia.setAvailabilityId(AVAILABILITY_ID);
            ia.setInterviewerId(INTERVIEWER_ID);
            ia.setAvailableDate(LocalDate.now().plusDays(2));
            ia.setAvailableTime(LocalTime.of(11, 0));
            ia.setSlotDuration(60);
            ia.setStatus("ACTIVE");
            session.persist(ia);
        }

        InterviewSchedule sched = session.get(InterviewSchedule.class, SCHEDULE_ID);
        if (sched == null) {
            sched = new InterviewSchedule();
            sched.setScheduleId(SCHEDULE_ID);
            sched.setCandidateId(CAND_ID);
            sched.setInterviewerId(INTERVIEWER_ID);
            sched.setInterviewDate(LocalDate.now().plusDays(2));
            sched.setInterviewTime(LocalTime.of(11, 0));
            sched.setInterviewType("TECHNICAL");
            sched.setFeedback("Initial schedule created");
            sched.setScore(0);
            sched.setOutcome("ON_HOLD");
            sched.setStatus("ACTIVE");
            session.persist(sched);
        }

        InterviewResult ir = session.get(InterviewResult.class, SCHEDULE_ID);
        if (ir == null) {
            ir = new InterviewResult();
            ir.setScheduleId(SCHEDULE_ID);
            ir.setFeedback("Strong Java fundamentals");
            ir.setScore(82);
            ir.setPassFailOutcome("PASS");
            ir.setStatus("ACTIVE");
            session.persist(ir);
        }
    }

    private static void ensureInterviewer(Session session) {
        // Ensures interviewer-related entities exist (delegated to ensureApplicationAndScreening).
        // Keeping this method to make the seeding flow easier to read.
    }

    private static void ensureOffer(Session session) {
        Offer o = session.get(Offer.class, OFFER_ID);
        if (o == null) {
            o = new Offer();
            o.setOfferId(OFFER_ID);
            o.setCandidateId(CAND_ID);
            o.setOfferDetails("Offer for Backend Engineer: base + benefits");
            o.setSalary(1300000.0);
            o.setStartDate(LocalDate.now().plusMonths(1));
            o.setStatus("EXTENDED");
            session.persist(o);
        }
    }

    // --- Helpers: Performance ---

    private static Goal ensureGoal(Session session, String empId) {
        Goal existing = session.createQuery(
                        "FROM Goal g WHERE g.empId = :e AND g.goalTitle = :t",
                        Goal.class)
                .setParameter("e", empId)
                .setParameter("t", "SAMPLE: Improve API latency")
                .setMaxResults(1)
                .uniqueResult();

        if (existing == null) {
            Goal g = new Goal();
            g.setEmpId(empId);
            g.setGoalTitle("SAMPLE: Improve API latency");
            g.setGoalDescription("Reduce p95 latency by 20%.");
            g.setGoalStartDate(LocalDate.now().minusMonths(1));
            g.setGoalEndDate(LocalDate.now().plusMonths(2));
            g.setGoalStatus("IN_PROGRESS");
            session.persist(g);
            return g;
        }
        return existing;
    }

    private static void ensureKpi(Session session, Long goalId, String empId) {
        Kpi existing = session.createQuery(
                        "FROM Kpi k WHERE k.goalId = :g AND k.kpiName = :n",
                        Kpi.class)
                .setParameter("g", goalId)
                .setParameter("n", "p95 latency (ms)")
                .setMaxResults(1)
                .uniqueResult();

        if (existing == null) {
            Kpi k = new Kpi();
            k.setGoalId(goalId);
            k.setEmpId(empId);
            k.setKpiName("p95 latency (ms)");
            k.setKpiTargetValue(250.0);
            k.setKpiActualValue(310.0);
            k.setKpiUnit("ms");
            session.persist(k);
        }
    }

    private static void ensureAppraisal(Session session, String empId, String reviewerId) {
        Appraisal existing = session.createQuery(
                        "FROM Appraisal a WHERE a.empId = :e AND a.appraisalPeriod = :p",
                        Appraisal.class)
                .setParameter("e", empId)
                .setParameter("p", "2026-H1")
                .setMaxResults(1)
                .uniqueResult();

        if (existing == null) {
            Appraisal a = new Appraisal();
            a.setEmpId(empId);
            a.setReviewerId(reviewerId);
            a.setAppraisalPeriod("2026-H1");
            a.setAppraisalScore(4.2);
            a.setAppraisalDate(LocalDate.now().minusDays(3));
            a.setAppraisalStatus("FINALIZED");
            session.persist(a);
        }
    }

    private static void ensureFeedback(Session session, String empId, String reviewerId) {
        Feedback existing = session.createQuery(
                        "FROM Feedback f WHERE f.empId = :e AND f.feedbackType = :t",
                        Feedback.class)
                .setParameter("e", empId)
                .setParameter("t", "MANAGER")
                .setMaxResults(1)
                .uniqueResult();

        if (existing == null) {
            Feedback f = new Feedback();
            f.setEmpId(empId);
            f.setReviewerId(reviewerId);
            f.setFeedbackType("MANAGER");
            f.setFeedbackText("Consistently delivers; focus next on mentoring.");
            f.setFeedbackDate(LocalDate.now().minusDays(5));
            session.persist(f);
        }
    }

    private static SkillGap ensureSkillGap(Session session, String empId) {
        SkillGap existing = session.createQuery(
                        "FROM SkillGap s WHERE s.empId = :e AND s.skillName = :n",
                        SkillGap.class)
                .setParameter("e", empId)
                .setParameter("n", "Leadership")
                .setMaxResults(1)
                .uniqueResult();

        if (existing == null) {
            SkillGap s = new SkillGap();
            s.setEmpId(empId);
            s.setSkillName("Leadership");
            s.setSkillCurrentLevel(2);
            s.setSkillTargetLevel(4);
            s.setTrainingPlanId(null);
            session.persist(s);
            return s;
        }
        return existing;
    }

    // --- Helpers: Succession Planning ---

    private static CriticalRole ensureCriticalRole(Session session) {
        CriticalRole existing = session.createQuery(
                        "FROM CriticalRole r WHERE r.roleName = :n",
                        CriticalRole.class)
                .setParameter("n", "SAMPLE: Tech Lead")
                .setMaxResults(1)
                .uniqueResult();

        if (existing == null) {
            CriticalRole r = new CriticalRole();
            r.setRoleName("SAMPLE: Tech Lead");
            r.setDepartment("Engineering");
            r.setCriticality("HIGH");
            r.setMinReadinessScore(70);
            session.persist(r);
            session.flush();
            return r;
        }
        return existing;
    }

    private static void ensureSuccessionPoolEntry(Session session, String empId, boolean eligible, String notes) {
        SuccessionPoolEntry existing = session.createQuery(
                        "FROM SuccessionPoolEntry e WHERE e.empId = :id",
                        SuccessionPoolEntry.class)
                .setParameter("id", empId)
                .setMaxResults(1)
                .uniqueResult();

        if (existing == null) {
            SuccessionPoolEntry e = new SuccessionPoolEntry();
            e.setEmpId(empId);
            e.setEligible(eligible);
            e.setNotes(notes);
            session.persist(e);
        }
    }

    private static void ensureReadinessScore(Session session,
                                             String empId,
                                             Integer roleId,
                                             Double appraisalScore,
                                             String competency,
                                             Double readinessScore,
                                             boolean skillGap,
                                             String skillGapDetail) {
        ReadinessScore existing = session.createQuery(
                        "FROM ReadinessScore r WHERE r.empId = :e AND r.roleId = :rid",
                        ReadinessScore.class)
                .setParameter("e", empId)
                .setParameter("rid", roleId)
                .setMaxResults(1)
                .uniqueResult();

        if (existing == null) {
            ReadinessScore r = new ReadinessScore();
            r.setEmpId(empId);
            r.setRoleId(roleId);
            r.setAppraisalScore(appraisalScore);
            r.setCompetencyRequirement(competency);
            r.setReadinessScore(readinessScore);
            r.setSkillGapFlag(skillGap);
            r.setSkillGapDetail(skillGapDetail);
            session.persist(r);
        }
    }

    private static SuccessorAssignment ensureSuccessorAssignment(Session session,
                                                                String empId,
                                                                Integer roleId,
                                                                Integer rank,
                                                                String hrDecision,
                                                                LocalDate date,
                                                                boolean noSuccessor) {
        SuccessorAssignment existing = session.createQuery(
                        "FROM SuccessorAssignment s WHERE s.empId = :e AND s.targetRoleId = :r",
                        SuccessorAssignment.class)
                .setParameter("e", empId)
                .setParameter("r", roleId)
                .setMaxResults(1)
                .uniqueResult();

        if (existing == null) {
            SuccessorAssignment s = new SuccessorAssignment();
            s.setEmpId(empId);
            s.setTargetRoleId(roleId);
            s.setSuccessorRank(rank);
            s.setHrDecision(hrDecision);
            s.setAssignmentDate(date);
            s.setNoSuccessorFlag(noSuccessor);
            session.persist(s);
            return s;
        }
        return existing;
    }

    private static DevelopmentPlan ensureDevelopmentPlan(Session session,
                                                         String successorId,
                                                         Long assignmentId,
                                                         Integer targetRoleId,
                                                         Long skillGapId,
                                                         Double progress) {
        DevelopmentPlan existing = session.createQuery(
                        "FROM DevelopmentPlan d WHERE d.successorId = :s AND d.assignmentIdFk = :a",
                        DevelopmentPlan.class)
                .setParameter("s", successorId)
                .setParameter("a", assignmentId)
                .setMaxResults(1)
                .uniqueResult();

        if (existing == null) {
            DevelopmentPlan d = new DevelopmentPlan();
            d.setSuccessorId(successorId);
            d.setAssignmentIdFk(assignmentId);
            d.setTargetRoleId(targetRoleId);
            d.setSkillGapId(skillGapId);
            d.setProgressPercentage(progress);
            session.persist(d);
            return d;
        }
        return existing;
    }

    private static void ensurePlanTask(Session session, Long planId, String desc, LocalDate due, String status) {
        PlanTask existing = session.createQuery(
                        "FROM PlanTask t WHERE t.planId = :p AND t.taskDescription = :d",
                        PlanTask.class)
                .setParameter("p", planId)
                .setParameter("d", desc)
                .setMaxResults(1)
                .uniqueResult();

        if (existing == null) {
            PlanTask t = new PlanTask();
            t.setPlanId(planId);
            t.setTaskDescription(desc);
            t.setTaskDueDate(due);
            t.setTaskStatus(status);
            session.persist(t);
        }
    }

    private static void ensureRiskLog(Session session, Integer roleId, String level, String details) {
        RiskLog existing = session.createQuery(
                        "FROM RiskLog r WHERE r.roleIdFk = :rid",
                        RiskLog.class)
                .setParameter("rid", roleId)
                .setMaxResults(1)
                .uniqueResult();

        if (existing == null) {
            RiskLog r = new RiskLog();
            r.setRoleIdFk(roleId);
            r.setRiskLevel(level);
            r.setDetails(details);
            r.setCreatedAt(LocalDateTime.now().minusDays(1));
            session.persist(r);
        }
    }

    private static void ensureExternalHireRequest(Session session, Integer roleId, String status, Integer retryCount) {
        ExternalHireRequest existing = session.createQuery(
                        "FROM ExternalHireRequest r WHERE r.roleIdFk = :rid",
                        ExternalHireRequest.class)
                .setParameter("rid", roleId)
                .setMaxResults(1)
                .uniqueResult();

        if (existing == null) {
            ExternalHireRequest r = new ExternalHireRequest();
            r.setRoleIdFk(roleId);
            r.setRequestStatus(status);
            r.setRetryCount(retryCount);
            session.persist(r);
        }
    }

    private static void ensureSuccessionAuditLog(Session session, String entityType, Integer entityId, String action, String by, String details) {
        SuccessionAuditLog existing = session.createQuery(
                        "FROM SuccessionAuditLog l WHERE l.entityType = :t AND l.entityId = :id AND l.actionType = :a",
                        SuccessionAuditLog.class)
                .setParameter("t", entityType)
                .setParameter("id", entityId)
                .setParameter("a", action)
                .setMaxResults(1)
                .uniqueResult();

        if (existing == null) {
            SuccessionAuditLog l = new SuccessionAuditLog();
            l.setEntityType(entityType);
            l.setEntityId(entityId);
            l.setActionType(action);
            l.setPerformedBy(by);
            l.setDetails(details);
            l.setTimestamp(LocalDateTime.now().minusHours(3));
            session.persist(l);
        }
    }

    // --- Helpers: Expense Management ---

    private static void ensureDepartmentBudget(Session session, String departmentName, Double limit, Double spent) {
        DepartmentBudget existing = session.createQuery(
                        "FROM DepartmentBudget b WHERE b.departmentName = :d",
                        DepartmentBudget.class)
                .setParameter("d", departmentName)
                .setMaxResults(1)
                .uniqueResult();

        if (existing == null) {
            DepartmentBudget b = new DepartmentBudget();
            b.setDepartmentName(departmentName);
            b.setBudgetLimit(limit);
            b.setCurrentSpent(spent);
            session.persist(b);
        }
    }

    private static void ensureExpenseCategory(Session session, String categoryName) {
        ExpenseCategory existing = session.createQuery(
                        "FROM ExpenseCategory c WHERE c.categoryName = :n",
                        ExpenseCategory.class)
                .setParameter("n", categoryName)
                .setMaxResults(1)
                .uniqueResult();

        if (existing == null) {
            ExpenseCategory c = new ExpenseCategory();
            c.setCategoryName(categoryName);
            session.persist(c);
        }
    }

    private static void ensureExpenseClaim(Session session,
                                           String claimId,
                                           Employee emp,
                                           Double amount,
                                           LocalDate date,
                                           String status,
                                           Employee approvedBy,
                                           String categoryTypeName) {
        ExpenseClaim existing = session.get(ExpenseClaim.class, claimId);
        if (existing == null) {
            ExpenseClaim c = new ExpenseClaim();
            c.setClaimId(claimId);
            c.setEmployee(emp);
            c.setAmount(amount);
            c.setExpenseDate(date);
            c.setStatus(status);
            c.setApprovedBy(approvedBy);
            c.setCategoryId(categoryTypeName);
            session.persist(c);
        }
    }

    private static void ensureReceipt(Session session, String claimId, String path, String name) {
        Receipt existing = session.createQuery(
                        "FROM Receipt r WHERE r.claimId = :c",
                        Receipt.class)
                .setParameter("c", claimId)
                .setMaxResults(1)
                .uniqueResult();

        if (existing == null) {
            Receipt r = new Receipt();
            r.setClaimId(claimId);
            r.setFilePath(path);
            r.setFileName(name);
            r.setUploadDate(LocalDateTime.now().minusDays(1));
            session.persist(r);
        }
    }

    private static void ensureClaimApproval(Session session, String claimId, String approverId, String status, String comments) {
        ClaimApproval existing = session.createQuery(
                        "FROM ClaimApproval a WHERE a.claimId = :c AND a.approverId = :a",
                        ClaimApproval.class)
                .setParameter("c", claimId)
                .setParameter("a", approverId)
                .setMaxResults(1)
                .uniqueResult();

        if (existing == null) {
            ClaimApproval a = new ClaimApproval();
            a.setClaimId(claimId);
            a.setApproverId(approverId);
            a.setStatus(status);
            a.setComments(comments);
            session.persist(a);
        }
    }

    private static void ensureExpenseAudit(Session session, String claimId, Employee emp, String actionType, String details, String exceptionName) {
        ExpenseAuditLog existing = session.createQuery(
                        "FROM ExpenseAuditLog a WHERE a.claimId = :c AND a.employee.empId = :e AND a.actionType = :t",
                        ExpenseAuditLog.class)
                .setParameter("c", claimId)
                .setParameter("e", emp.getEmpId())
                .setParameter("t", actionType)
                .setMaxResults(1)
                .uniqueResult();

        if (existing == null) {
            ExpenseAuditLog a = new ExpenseAuditLog();
            a.setClaimId(claimId);
            a.setEmployee(emp);
            a.setActionType(actionType);
            a.setDetails(details);
            a.setExceptionName(exceptionName);
            a.setTs(LocalDateTime.now().minusHours(1));
            session.persist(a);
        }
    }

    // --- Helpers: Onboarding/Offboarding/Docs ---

    private static void ensureOnboardingTask(Session session,
                                             String taskId,
                                             Employee emp,
                                             String taskName,
                                             String taskType,
                                             String assignedTo,
                                             String status,
                                             LocalDate due,
                                             String onboardingStatus) {
        OnboardingTask existing = session.get(OnboardingTask.class, taskId);
        if (existing == null) {
            OnboardingTask t = new OnboardingTask();
            t.setTaskId(taskId);
            t.setEmployee(emp);
            t.setTaskName(taskName);
            t.setTaskType(taskType);
            t.setAssignedTo(assignedTo);
            t.setStatus(status);
            t.setDueDate(due);
            t.setOnboardingStatus(onboardingStatus);
            session.persist(t);
        }
    }

    private static void ensureClearanceSettlement(Session session,
                                                  String clearanceId,
                                                  String empId,
                                                  Double amount,
                                                  String clearanceStatus,
                                                  String assetReturnStatus,
                                                  String settlementType,
                                                  String notes) {
        ClearanceSettlement existing = session.get(ClearanceSettlement.class, clearanceId);
        if (existing == null) {
            ClearanceSettlement c = new ClearanceSettlement();
            c.setClearanceId(clearanceId);
            c.setEmpId(empId);
            c.setSettlementAmount(amount);
            c.setClearanceStatus(clearanceStatus);
            c.setAssetReturnStatus(assetReturnStatus);
            c.setSettlementType(settlementType);
            c.setNotes(notes);
            session.persist(c);
        }
    }

    private static void ensureExitInterview(Session session,
                                            String interviewId,
                                            String empId,
                                            String reason,
                                            String feedback,
                                            Integer rating,
                                            String issues,
                                            String notes,
                                            LocalDate exitDate) {
        ExitInterview existing = session.get(ExitInterview.class, interviewId);
        if (existing == null) {
            ExitInterview e = new ExitInterview();
            e.setInterviewId(interviewId);
            e.setEmpId(empId);
            e.setPrimaryReason(reason);
            e.setFeedbackText(feedback);
            e.setSatisfactionRating(rating);
            e.setIssuesReported(issues);
            e.setInterviewerNotes(notes);
            e.setExitDate(exitDate);
            session.persist(e);
        }
    }

    private static void ensureNotification(Session session,
                                           String recipientId,
                                           String type,
                                           String message,
                                           String subject,
                                           boolean read) {
        Notification existing = session.createQuery(
                        "FROM Notification n WHERE n.recipientId = :r AND n.subject = :s",
                        Notification.class)
                .setParameter("r", recipientId)
                .setParameter("s", subject)
                .setMaxResults(1)
                .uniqueResult();

        if (existing == null) {
            Notification n = new Notification();
            n.setRecipientId(recipientId);
            n.setNotificationType(type);
            n.setNotificationMessage(message);
            n.setSubject(subject);
            n.setIsRead(read);
            n.setTriggeredBy("DatabaseSampleDataSeeder");
            n.setStatus(read ? "READ" : "SENT");
            n.setScheduledAt(null);
            session.persist(n);
        }
    }

    private static void ensureDocument(Session session,
                                       String documentId,
                                       Employee emp,
                                       String docType,
                                       String path,
                                       LocalDate uploadDate,
                                       String verificationStatus) {
        Document d = session.get(Document.class, documentId);
        if (d == null) {
            d = new Document();
            d.setDocumentId(documentId);
            d.setEmployee(emp);
            d.setDocumentType(docType);
            d.setFilePath(path);
            d.setUploadDate(uploadDate);
            d.setVerificationStatus(verificationStatus);
            session.persist(d);
        }
    }

    private static void ensureDocumentMetadata(Session session,
                                               String id,
                                               String empId,
                                               String name,
                                               String filePath,
                                               int version,
                                               long createdAt,
                                               long expiryAt,
                                               DocumentType type) {
        DocumentMetadataRecord existing = session.get(DocumentMetadataRecord.class, id);
        if (existing == null) {
            DocumentMetadataRecord m = new DocumentMetadataRecord();
            m.setId(id);
            m.setEmployeeId(empId);
            m.setName(name);
            m.setFilePath(filePath);
            m.setVersion(version);
            m.setCreatedAt(createdAt);
            m.setExpiryAt(expiryAt);
            m.setType(type);
            session.persist(m);
        }
    }

    private static void ensureDocumentAudit(Session session, String action, String documentId, String employeeId, String performedBy) {
        DocumentAuditLogRecord existing = session.createQuery(
                        "FROM DocumentAuditLogRecord l WHERE l.action = :a AND l.documentId = :d AND l.employeeId = :e",
                        DocumentAuditLogRecord.class)
                .setParameter("a", action)
                .setParameter("d", documentId)
                .setParameter("e", employeeId)
                .setMaxResults(1)
                .uniqueResult();

        if (existing == null) {
            DocumentAuditLogRecord l = new DocumentAuditLogRecord();
            l.setAction(action);
            l.setDocumentId(documentId);
            l.setEmployeeId(employeeId);
            l.setPerformedBy(performedBy);
            l.setTimestamp(LocalDateTime.now().minusDays(2));
            session.persist(l);
        }
    }

    // --- Helpers: Customization / Workflows / Analytics ---

    private static CustomModule ensureCustomModule(Session session, String moduleName, String moduleType, boolean enabled) {
        CustomModule existing = session.createQuery(
                        "FROM CustomModule m WHERE m.moduleName = :n",
                        CustomModule.class)
                .setParameter("n", moduleName)
                .setMaxResults(1)
                .uniqueResult();

        if (existing == null) {
            CustomModule m = new CustomModule();
            m.setModuleName(moduleName);
            m.setModuleType(moduleType);
            m.setIsEnabled(enabled);
            session.persist(m);
            session.flush();
            return m;
        }
        return existing;
    }

    private static CustomForm ensureCustomForm(Session session, String formName, Integer moduleId, String layoutType) {
        CustomForm existing = session.createQuery(
                        "FROM CustomForm f WHERE f.formName = :n",
                        CustomForm.class)
                .setParameter("n", formName)
                .setMaxResults(1)
                .uniqueResult();

        if (existing == null) {
            CustomForm f = new CustomForm();
            f.setFormName(formName);
            f.setModuleId(moduleId);
            f.setLayoutType(layoutType);
            f.setCreatedDate(LocalDate.now().minusDays(1));
            session.persist(f);
            session.flush();
            return f;
        }
        return existing;
    }

    private static void ensureCustomField(Session session, Integer formId, String fieldName, String fieldType, boolean mandatory, String defaultValue) {
        CustomField existing = session.createQuery(
                        "FROM CustomField f WHERE f.formId = :id AND f.fieldName = :n",
                        CustomField.class)
                .setParameter("id", formId)
                .setParameter("n", fieldName)
                .setMaxResults(1)
                .uniqueResult();

        if (existing == null) {
            CustomField f = new CustomField();
            f.setFormId(formId);
            f.setFieldName(fieldName);
            f.setFieldType(fieldType);
            f.setIsMandatory(mandatory);
            f.setDefaultValue(defaultValue);
            session.persist(f);
        }
    }

    private static Workflow ensureWorkflow(Session session, String name, String status, String assignedTo) {
        Workflow existing = session.createQuery(
                        "FROM Workflow w WHERE w.workflowName = :n",
                        Workflow.class)
                .setParameter("n", name)
                .setMaxResults(1)
                .uniqueResult();

        if (existing == null) {
            Workflow w = new Workflow();
            w.setWorkflowName(name);
            w.setCurrentStatus(status);
            w.setAssignedTo(assignedTo);
            session.persist(w);
            session.flush();
            return w;
        }
        return existing;
    }

    private static void ensureWorkflowTask(Session session, Integer workflowId, String taskName, Integer seq, String status) {
        WorkflowTask existing = session.createQuery(
                        "FROM WorkflowTask t WHERE t.workflowId = :w AND t.sequenceOrder = :s",
                        WorkflowTask.class)
                .setParameter("w", workflowId)
                .setParameter("s", seq)
                .setMaxResults(1)
                .uniqueResult();

        if (existing == null) {
            WorkflowTask t = new WorkflowTask();
            t.setWorkflowId(workflowId);
            t.setTaskName(taskName);
            t.setSequenceOrder(seq);
            t.setTaskStatus(status);
            session.persist(t);
        }
    }

    private static void ensureDashboardConfig(Session session,
                                              String dashboardId,
                                              String name,
                                              String userId,
                                              String filter,
                                              String widgets,
                                              String dateRange) {
        DashboardConfig existing = session.get(DashboardConfig.class, dashboardId);
        if (existing == null) {
            DashboardConfig d = new DashboardConfig();
            d.setDashboardId(dashboardId);
            d.setDashboardName(name);
            d.setUserId(userId);
            d.setFilterCriteria(filter);
            d.setWidgetList(widgets);
            d.setDateRange(dateRange);
            session.persist(d);
        }
    }

    private static void ensureHrReport(Session session,
                                       String reportId,
                                       String name,
                                       String type,
                                       LocalDate generatedDate,
                                       String exportFormat,
                                       String exportPath,
                                       String schedule) {
        HrReport existing = session.get(HrReport.class, reportId);
        if (existing == null) {
            HrReport r = new HrReport();
            r.setReportId(reportId);
            r.setReportName(name);
            r.setReportType(type);
            r.setGeneratedDate(generatedDate);
            r.setExportFormat(exportFormat);
            r.setExportFilePath(exportPath);
            r.setScheduleConfig(schedule);
            session.persist(r);
        }
    }
}
