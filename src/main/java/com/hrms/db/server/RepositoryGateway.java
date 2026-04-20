package com.hrms.db.server;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.hrms.db.config.DatabaseConnection;
import com.hrms.db.entities.SecurityAuditLog;
import com.hrms.db.factory.RepositoryFactory;
import org.hibernate.Session;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.nio.file.Paths;
import java.sql.DatabaseMetaData;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Reflection-backed HTTP gateway for the repositories exposed by RepositoryFactory.
 * This keeps the transport layer thin while allowing the teams to call the DB layer remotely.
 */
public class RepositoryGateway {

    private final RepositoryFactory factory;
    private final ObjectMapper mapper;
    private final Map<String, RepositoryBinding> bindings;

    public RepositoryGateway() {
        this.factory = RepositoryFactory.getInstance();
        this.mapper = buildMapper();
        this.bindings = Collections.unmodifiableMap(buildBindings());
    }

    public ObjectMapper getMapper() {
        return mapper;
    }

    public List<Map<String, Object>> listRepositories() {
        List<Map<String, Object>> repositories = new ArrayList<>();
        for (RepositoryBinding binding : bindings.values()) {
            repositories.add(binding.toSummary());
        }
        return repositories;
    }

    public Map<String, Object> invoke(String repositoryName, String methodName, List<Object> args) {
        RepositoryBinding binding = bindings.get(repositoryName);
        if (binding == null) {
            throw new GatewayException(404, "Unknown repository: " + repositoryName);
        }

        InvocationMatch match = binding.findMethod(methodName, args, mapper);
        if (match == null) {
            throw new GatewayException(404,
                    "No matching method found for " + repositoryName + "." + methodName +
                            " with " + args.size() + " argument(s)");
        }

        try {
            Object value = match.method.invoke(binding.target, match.convertedArgs);
            Map<String, Object> response = new LinkedHashMap<>();
            response.put("repository", repositoryName);
            response.put("method", methodName);
            response.put("resultType", match.method.getGenericReturnType().getTypeName());
            response.put("result", toJsonSafe(value, new IdentityHashMap<>(), 0));
            return response;
        } catch (IllegalAccessException ex) {
            throw new GatewayException(500, "Method is not accessible: " + methodName, ex);
        } catch (InvocationTargetException ex) {
            Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
            throw new GatewayException(500, cause.getClass().getSimpleName() + ": " + cause.getMessage(), cause);
        }
    }

    public Map<String, Object> getHealth() {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("status", "UP");
        payload.put("databaseFile", Paths.get("hrms.db").toAbsolutePath().toString());
        payload.put("repositoryCount", bindings.size());

        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            session.doWork(connection -> {
                DatabaseMetaData meta = connection.getMetaData();
                payload.put("databaseProduct", meta.getDatabaseProductName());
                payload.put("databaseVersion", meta.getDatabaseProductVersion());
                payload.put("driverName", meta.getDriverName());
            });
        } catch (Exception ex) {
            throw new GatewayException(500, "Health check failed: " + ex.getMessage(), ex);
        }
        return payload;
    }

    public Map<String, Object> getDashboard() {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("health", getHealth());

        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            Map<String, Object> counts = new LinkedHashMap<>();
            counts.put("employees", safeCount(session, "SELECT COUNT(e) FROM Employee e"));
            counts.put("leaveRecords", safeCount(session, "SELECT COUNT(l) FROM LeaveRecord l"));
            counts.put("payrollResults", safeCount(session, "SELECT COUNT(p) FROM PayrollResult p"));
            counts.put("securityAuditLogs", safeCount(session, "SELECT COUNT(s) FROM SecurityAuditLog s"));
            payload.put("counts", counts);
        } catch (Exception ex) {
            throw new GatewayException(500, "Unable to load dashboard counts: " + ex.getMessage(), ex);
        }

        payload.put("repositories", listRepositories());
        payload.put("recentErrors", getRecentErrors(10));
        return payload;
    }

    public List<Map<String, Object>> getRecentErrors(int limit) {
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            List<SecurityAuditLog> logs = session.createQuery(
                            "FROM SecurityAuditLog s WHERE s.actionType IN ('DB_ERROR', 'DB_LOG') ORDER BY s.timestamp DESC",
                            SecurityAuditLog.class)
                    .setMaxResults(limit)
                    .getResultList();

            List<Map<String, Object>> items = new ArrayList<>();
            for (SecurityAuditLog log : logs) {
                Map<String, Object> item = new LinkedHashMap<>();
                item.put("timestamp", log.getTimestamp() != null ? log.getTimestamp().toString() : null);
                item.put("outcome", log.getOutcome());
                item.put("operation", log.getOperation());
                item.put("details", log.getDetails());
                items.add(item);
            }
            return items;
        } catch (Exception ex) {
            throw new GatewayException(500, "Unable to load recent errors: " + ex.getMessage(), ex);
        }
    }

    public Map<String, Object> parseRequestBody(String body) {
        try {
            if (body == null || body.isBlank()) {
                return new LinkedHashMap<>();
            }
            return mapper.readValue(body, new TypeReference<>() {});
        } catch (Exception ex) {
            throw new GatewayException(400, "Invalid JSON request body", ex);
        }
    }

    private ObjectMapper buildMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        objectMapper.enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS);
        return objectMapper;
    }

    private Map<String, RepositoryBinding> buildBindings() {
        Map<String, RepositoryBinding> map = new LinkedHashMap<>();
        map.put("payroll", new RepositoryBinding("payroll", "Payroll data exchange", factory.getPayrollRepository(), List.of(
                com.hrms.db.repositories.payroll.IPayrollRepository.class)));
        map.put("attrition", new RepositoryBinding("attrition", "Attrition analytics and workflow data", factory.getAttritionRepository(), List.of(
                com.hrms.db.repositories.attrition.IAttritionRepository.class)));
        map.put("onboarding", new RepositoryBinding("onboarding", "Onboarding and offboarding flows", factory.getOnboardingRepository(), List.of(
                com.hrms.db.repositories.onboarding.IOnboardingRepository.class)));
        map.put("documentManagement", new RepositoryBinding("documentManagement", "Document metadata queries", factory.getDocumentRepository(), List.of(
                com.hrms.db.repositories.docu_management.DocumentRepository.class)));
        map.put("documentAudit", new RepositoryBinding("documentAudit", "Document audit trail", factory.getDocumentAuditRepository(), List.of(
                com.hrms.db.repositories.docu_management.AuditRepository.class)));
        map.put("customization", new RepositoryBinding("customization", "Customization subsystem interfaces", factory.getFormRepository(), List.of(
                com.hrms.db.repositories.Customization_team.IEITRepository.class,
                com.hrms.db.repositories.Customization_team.IFlexfieldRepository.class,
                com.hrms.db.repositories.Customization_team.IFormRepository.class,
                com.hrms.db.repositories.Customization_team.ILookupRepository.class,
                com.hrms.db.repositories.Customization_team.IModuleRepository.class,
                com.hrms.db.repositories.Customization_team.IReportRepository.class,
                com.hrms.db.repositories.Customization_team.ITaskFlowRepository.class,
                com.hrms.db.repositories.Customization_team.IWorkflowRepository.class)));
        map.put("performance", new RepositoryBinding("performance", "Performance management repositories", factory.getAppraisalRepository(), List.of(
                com.hrms.db.repositories.performance.interfaces.IAppraisalRepository.class,
                com.hrms.db.repositories.performance.interfaces.IAuditLogRepository.class,
                com.hrms.db.repositories.performance.interfaces.IEmployeeRepository.class,
                com.hrms.db.repositories.performance.interfaces.IFeedbackRepository.class,
                com.hrms.db.repositories.performance.interfaces.IGoalRepository.class,
                com.hrms.db.repositories.performance.interfaces.IKPIRepository.class,
                com.hrms.db.repositories.performance.interfaces.INotificationRepository.class,
                com.hrms.db.repositories.performance.interfaces.IReportRepository.class,
                com.hrms.db.repositories.performance.interfaces.ISkillGapRepository.class,
                com.hrms.db.repositories.performance.interfaces.IPerformanceCycleRepository.class)));
        map.put("leave", new RepositoryBinding("leave", "Leave management unified repository", factory.getLeaveRecordRepository(), List.of(
                com.hrms.db.repositories.leave.ILeaveRecordRepository.class,
                com.hrms.db.repositories.leave.ILeaveEmployeeRepository.class,
                com.hrms.db.repositories.leave.ILeaveHolidayRepository.class,
                com.hrms.db.repositories.leave.ILeavePolicyRepository.class,
                com.hrms.db.repositories.leave.ILeavePayrollSyncRepository.class,
                com.hrms.db.repositories.leave.ILeaveAuditLogRepository.class)));
        map.put("leaveSubsystem", new RepositoryBinding("leaveSubsystem", "Leave team compatibility adapter", factory.getLeaveManagementSubsystemLeaveRecordRepository(), List.of(
                com.hrms.db.repositories.Leave_Management_Subsytem.ILeaveRecordRepository.class,
                com.hrms.db.repositories.Leave_Management_Subsytem.ILeavePolicyRepository.class,
                com.hrms.db.repositories.Leave_Management_Subsytem.IHolidayRepository.class,
                com.hrms.db.repositories.Leave_Management_Subsytem.IEmployeeRepository.class,
                com.hrms.db.repositories.Leave_Management_Subsytem.IAuditLogRepository.class,
                com.hrms.db.repositories.Leave_Management_Subsytem.IPayrollSyncRepository.class)));
        map.put("security", new RepositoryBinding("security", "Authentication, authorization, encryption, and audit", factory.getAuditService(), List.of(
                com.hrms.db.repositories.security.IAuditService.class,
                com.hrms.db.repositories.security.IAuthenticationService.class,
                com.hrms.db.repositories.security.IAuthorizationService.class,
                com.hrms.db.repositories.security.IEncryptionService.class)));
        map.put("benefitPlan", new RepositoryBinding("benefitPlan", "Benefits plan access", factory.getBenefitPlanRepository(), List.of(
                com.hrms.db.repositories.benefits.BenefitPlanDAO.class)));
        map.put("benefitPolicy", new RepositoryBinding("benefitPolicy", "Benefits policy access", factory.getBenefitPolicyRepository(), List.of(
                com.hrms.db.repositories.benefits.BenefitPolicyDAO.class)));
        map.put("benefitEnrollment", new RepositoryBinding("benefitEnrollment", "Benefits enrollment access", factory.getBenefitEnrollmentRepository(), List.of(
                com.hrms.db.repositories.benefits.EnrollmentDAO.class)));
        map.put("benefitsNotification", new RepositoryBinding("benefitsNotification", "Benefits notifications", factory.getBenefitsNotificationRepository(), List.of(
                com.hrms.db.repositories.benefits.NotificationDAO.class)));
        map.put("benefitsEmployeeProfile", new RepositoryBinding("benefitsEmployeeProfile", "Benefits employee profile access", factory.getBenefitsEmployeeProfileRepository(), List.of(
                com.hrms.db.repositories.benefits.EmployeeProfileDAO.class)));
        map.put("benefitsAudit", new RepositoryBinding("benefitsAudit", "Benefits audit logs", factory.getBenefitsAuditLogRepository(), List.of(
                com.hrms.db.repositories.benefits.AuditLogDAO.class)));
        map.put("expenseEmployee", new RepositoryBinding("expenseEmployee", "Expense employee lookups", factory.getExpenseEmployeeRepository(), List.of(
                com.hrms.db.repositories.Expense_Management.EmployeeRepository.class)));
        map.put("expenseClaim", new RepositoryBinding("expenseClaim", "Expense claim operations", factory.getExpenseClaimRepository(), List.of(
                com.hrms.db.repositories.Expense_Management.ClaimRepository.class)));
        map.put("expenseBudget", new RepositoryBinding("expenseBudget", "Expense budget operations", factory.getExpenseBudgetRepository(), List.of(
                com.hrms.db.repositories.Expense_Management.BudgetRepository.class)));
        map.put("expenseReceipt", new RepositoryBinding("expenseReceipt", "Expense receipt operations", factory.getExpenseReceiptRepository(), List.of(
                com.hrms.db.repositories.Expense_Management.ReceiptRepository.class)));
        map.put("expenseLeave", new RepositoryBinding("expenseLeave", "Expense leave eligibility queries", factory.getExpenseLeaveRepository(), List.of(
                com.hrms.db.repositories.Expense_Management.LeaveRepository.class)));
        map.put("expenseAudit", new RepositoryBinding("expenseAudit", "Expense audit trail", factory.getExpenseAuditRepository(), List.of(
                com.hrms.db.repositories.Expense_Management.AuditRepository.class)));
        map.put("timeTrackingAttendance", new RepositoryBinding("timeTrackingAttendance", "Time tracking attendance records", factory.getTimeTrackingAttendanceRepository(), List.of(
                com.hrms.db.repositories.timetracking.IAttendanceRepository.class)));
        map.put("timeTrackingBreak", new RepositoryBinding("timeTrackingBreak", "Time tracking break records", factory.getTimeTrackingBreakRepository(), List.of(
                com.hrms.db.repositories.timetracking.IBreakRepository.class)));
        map.put("timeTrackingEmployee", new RepositoryBinding("timeTrackingEmployee", "Time tracking employee access", factory.getTimeTrackingEmployeeRepository(), List.of(
                com.hrms.db.repositories.timetracking.IEmployeeRepository.class)));
        map.put("timeTrackingNotification", new RepositoryBinding("timeTrackingNotification", "Time tracking notifications", factory.getTimeTrackingNotificationRepository(), List.of(
                com.hrms.db.repositories.timetracking.INotificationRepository.class)));
        map.put("timeTrackingOvertime", new RepositoryBinding("timeTrackingOvertime", "Time tracking overtime records", factory.getTimeTrackingOvertimeRepository(), List.of(
                com.hrms.db.repositories.timetracking.IOvertimeRepository.class)));
        map.put("timeTrackingPolicy", new RepositoryBinding("timeTrackingPolicy", "Time tracking policy access", factory.getTimeTrackingPolicyRepository(), List.of(
                com.hrms.db.repositories.timetracking.IPolicyRepository.class)));
        map.put("timeTrackingReport", new RepositoryBinding("timeTrackingReport", "Time tracking reporting", factory.getTimeTrackingReportRepository(), List.of(
                com.hrms.db.repositories.timetracking.IReportRepository.class)));
        map.put("successionRole", new RepositoryBinding("successionRole", "Succession critical role data", factory.getSuccessionRoleRepository(), List.of(
                com.hrms.db.repositories.succession.IRoleRepository.class)));
        map.put("successionPool", new RepositoryBinding("successionPool", "Succession pool entries", factory.getSuccessionPoolRepository(), List.of(
                com.hrms.db.repositories.succession.ISuccessionPoolRepository.class)));
        map.put("successionReadiness", new RepositoryBinding("successionReadiness", "Succession readiness scores", factory.getSuccessionReadinessScoreRepository(), List.of(
                com.hrms.db.repositories.succession.IReadinessScoreRepository.class)));
        map.put("successionAssignment", new RepositoryBinding("successionAssignment", "Succession successor assignments", factory.getSuccessionSuccessorAssignmentRepository(), List.of(
                com.hrms.db.repositories.succession.ISuccessorAssignmentRepository.class)));
        map.put("successionDevelopmentPlan", new RepositoryBinding("successionDevelopmentPlan", "Succession development plans", factory.getSuccessionDevelopmentPlanRepository(), List.of(
                com.hrms.db.repositories.succession.IDevelopmentPlanRepository.class)));
        map.put("successionPlanTask", new RepositoryBinding("successionPlanTask", "Succession development plan tasks", factory.getSuccessionPlanTaskRepository(), List.of(
                com.hrms.db.repositories.succession.IPlanTaskRepository.class)));
        map.put("successionNotification", new RepositoryBinding("successionNotification", "Succession notifications", factory.getSuccessionNotificationRepository(), List.of(
                com.hrms.db.repositories.succession.INotificationRepository.class)));
        map.put("successionRiskLog", new RepositoryBinding("successionRiskLog", "Succession risk logs", factory.getSuccessionRiskLogRepository(), List.of(
                com.hrms.db.repositories.succession.IRiskLogRepository.class)));
        map.put("successionExternalHire", new RepositoryBinding("successionExternalHire", "Succession external hire requests", factory.getSuccessionExternalHireRequestRepository(), List.of(
                com.hrms.db.repositories.succession.IExternalHireRequestRepository.class)));
        map.put("successionAudit", new RepositoryBinding("successionAudit", "Succession audit logs", factory.getSuccessionAuditLogRepository(), List.of(
                com.hrms.db.repositories.succession.IAuditLogRepository.class)));
        map.put("hrAnalyticsEmployee", new RepositoryBinding("hrAnalyticsEmployee", "HR analytics employee service", factory.getHrAnalyticsEmployeeService(), List.of(
                com.hrms.db.repositories.hranalytics.EmployeeService.class)));
        map.put("multiCountry", new RepositoryBinding("multiCountry", "Multi-country HR data", factory.getMultiCountryRepository(), List.of(
                com.hrms.db.repositories.multicountry.IMultiCountryRepository.class)));
        return map;
    }

    private long safeCount(Session session, String hql) {
        Long value = session.createQuery(hql, Long.class).uniqueResult();
        return value != null ? value : 0L;
    }

    private Object toJsonSafe(Object value, IdentityHashMap<Object, Boolean> visited, int depth) {
        if (value == null) return null;
        if (depth > 4) return String.valueOf(value);

        Class<?> type = value.getClass();
        if (isScalar(type, value)) {
            return value instanceof TemporalAccessor ? value.toString() : value;
        }
        if (value instanceof Optional<?> optional) {
            return optional.map(v -> toJsonSafe(v, visited, depth + 1)).orElse(null);
        }
        if (type.isArray()) {
            int length = Array.getLength(value);
            List<Object> list = new ArrayList<>(length);
            for (int i = 0; i < length; i++) {
                list.add(toJsonSafe(Array.get(value, i), visited, depth + 1));
            }
            return list;
        }
        if (value instanceof Collection<?> collection) {
            List<Object> list = new ArrayList<>(collection.size());
            for (Object item : collection) {
                list.add(toJsonSafe(item, visited, depth + 1));
            }
            return list;
        }
        if (value instanceof Map<?, ?> map) {
            Map<String, Object> converted = new LinkedHashMap<>();
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                converted.put(String.valueOf(entry.getKey()), toJsonSafe(entry.getValue(), visited, depth + 1));
            }
            return converted;
        }
        if (visited.containsKey(value)) {
            return "[circular]";
        }
        visited.put(value, Boolean.TRUE);

        Map<String, Object> object = new LinkedHashMap<>();
        for (Field field : type.getDeclaredFields()) {
            if (Modifier.isStatic(field.getModifiers()) || field.isSynthetic()) {
                continue;
            }
            try {
                field.setAccessible(true);
                object.put(field.getName(), toJsonSafe(field.get(value), visited, depth + 1));
            } catch (Exception ignored) {
                object.put(field.getName(), "[unavailable]");
            }
        }
        if (object.isEmpty()) {
            object.put("value", String.valueOf(value));
        }
        visited.remove(value);
        return object;
    }

    private boolean isScalar(Class<?> type, Object value) {
        return type.isPrimitive()
                || value instanceof String
                || value instanceof Number
                || value instanceof Boolean
                || value instanceof Character
                || value instanceof Enum<?>
                || value instanceof TemporalAccessor;
    }

    public static final class GatewayException extends RuntimeException {
        private final int statusCode;

        public GatewayException(int statusCode, String message) {
            super(message);
            this.statusCode = statusCode;
        }

        public GatewayException(int statusCode, String message, Throwable cause) {
            super(message, cause);
            this.statusCode = statusCode;
        }

        public int getStatusCode() {
            return statusCode;
        }
    }

    private static final class RepositoryBinding {
        private final String name;
        private final String description;
        private final Object target;
        private final List<Class<?>> contracts;

        private RepositoryBinding(String name, String description, Object target, List<Class<?>> contracts) {
            this.name = name;
            this.description = description;
            this.target = Objects.requireNonNull(target);
            this.contracts = contracts;
        }

        private Map<String, Object> toSummary() {
            Map<String, Object> summary = new LinkedHashMap<>();
            summary.put("name", name);
            summary.put("description", description);
            summary.put("implementation", target.getClass().getName());
            summary.put("interfaces", contracts.stream().map(Class::getName).toList());
            summary.put("methods", describeMethods());
            return summary;
        }

        private List<Map<String, Object>> describeMethods() {
            List<Map<String, Object>> methods = new ArrayList<>();
            for (Class<?> contract : contracts) {
                for (Method method : contract.getMethods()) {
                    Map<String, Object> info = new LinkedHashMap<>();
                    info.put("name", method.getName());
                    info.put("declaringInterface", contract.getSimpleName());
                    info.put("returnType", method.getGenericReturnType().getTypeName());

                    List<Map<String, Object>> params = new ArrayList<>();
                    int index = 0;
                    for (Parameter parameter : method.getParameters()) {
                        Map<String, Object> paramInfo = new LinkedHashMap<>();
                        paramInfo.put("index", index++);
                        paramInfo.put("name", parameter.isNamePresent() ? parameter.getName() : "arg" + (index - 1));
                        paramInfo.put("type", parameter.getParameterizedType().getTypeName());
                        params.add(paramInfo);
                    }
                    info.put("parameters", params);
                    methods.add(info);
                }
            }
            return methods;
        }

        private InvocationMatch findMethod(String methodName, List<Object> args, ObjectMapper mapper) {
            for (Class<?> contract : contracts) {
                for (Method method : contract.getMethods()) {
                    if (!method.getName().equals(methodName) || method.getParameterCount() != args.size()) {
                        continue;
                    }

                    Object[] convertedArgs = new Object[args.size()];
                    boolean compatible = true;

                    for (int i = 0; i < method.getParameterCount(); i++) {
                        try {
                            convertedArgs[i] = convertArg(args.get(i), method.getParameterTypes()[i], mapper);
                        } catch (IllegalArgumentException ex) {
                            compatible = false;
                            break;
                        }
                    }

                    if (compatible) {
                        return new InvocationMatch(method, convertedArgs);
                    }
                }
            }
            return null;
        }

        private Object convertArg(Object arg, Class<?> targetType, ObjectMapper mapper) {
            if (arg == null) {
                if (targetType.isPrimitive()) {
                    throw new IllegalArgumentException("Primitive argument cannot be null");
                }
                return null;
            }
            if (targetType.isInstance(arg)) {
                return arg;
            }
            try {
                return mapper.convertValue(arg, targetType);
            } catch (IllegalArgumentException ex) {
                throw new IllegalArgumentException("Unable to convert argument to " + targetType.getName(), ex);
            }
        }
    }

    private record InvocationMatch(Method method, Object[] convertedArgs) {}
}
