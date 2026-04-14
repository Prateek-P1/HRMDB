package com.hrms.db.repositories.onboarding;

import com.hrms.db.config.DatabaseConnection;
import com.hrms.db.entities.*;
import com.hrms.db.handlers.*;
import com.hrms.db.handlers.ErrorHandler.ErrorLevel;
import com.hrms.db.interfaces.DatabaseException;
import com.hrms.db.interfaces.RecordNotFoundException;
import com.hrms.db.logging.*;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.*;
import java.util.stream.Collectors;

/**
 * OnboardingRepositoryImpl — Hibernate implementation of IOnboardingRepository.
 *
 * Handles all DB operations for employee onboarding and offboarding:
 *   - Employee profile management
 *   - Candidate pre-onboarding data
 *   - Document upload and verification
 *   - Onboarding task assignment and tracking
 *   - Exit management (requests, interviews, clearance)
 *   - Notifications and progress tracking
 *
 * Each method follows the same Payroll template:
 *   log → open session → query/persist → map to DTO → catch/wrap errors
 */
public class OnboardingRepositoryImpl implements IOnboardingRepository {

    private static final String REPO = "OnboardingRepositoryImpl";

    private final ErrorHandler errorChain = new ConsoleErrorLogger(
            new DatabaseErrorLogger(new CriticalErrorEscalator(null)));
    private final LogHandler log = new ConsoleLogHandler(new DatabaseLogHandler(null));

    // ── Employee Profile ─────────────────────────────────────────────

    @Override
    public OnboardingEmployee getEmployeeById(String employeeID) {
        log.log(LogHandler.LogLevel.INFO, REPO, "getEmployeeById", "id=" + employeeID);
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            Employee emp = session.get(Employee.class, employeeID);
            if (emp == null) throw new RecordNotFoundException(REPO, "Employee", employeeID);
            return toOnboardingEmployee(emp);
        } catch (RecordNotFoundException e) {
            errorChain.handle(REPO + ".getEmployeeById", e, ErrorLevel.ERROR);
            throw e;
        } catch (Exception ex) {
            return handleRead("getEmployeeById", ex);
        }
    }

    @Override
    public List<OnboardingEmployee> getAllEmployees() {
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            return session.createQuery("FROM Employee", Employee.class)
                    .getResultList().stream().map(this::toOnboardingEmployee).collect(Collectors.toList());
        } catch (Exception ex) {
            handleError("getAllEmployees", ex, ErrorLevel.ERROR);
            return Collections.emptyList();
        }
    }

    @Override
    public void updateEmployeeStatus(String employeeID, String status) {
        executeUpdate("updateEmployeeStatus",
                "UPDATE Employee e SET e.employmentStatus = :status WHERE e.empId = :id",
                Map.of("status", status, "id", employeeID));
    }

    // ── Pre-Onboarding ───────────────────────────────────────────────

    @Override
    public OnboardingCandidate getCandidateById(String candidateID) {
        log.log(LogHandler.LogLevel.INFO, REPO, "getCandidateById", "id=" + candidateID);
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            Candidate c = session.get(Candidate.class, candidateID);
            if (c == null) throw new RecordNotFoundException(REPO, "Candidate", candidateID);
            return toCandidate(c);
        } catch (RecordNotFoundException e) {
            errorChain.handle(REPO + ".getCandidateById", e, ErrorLevel.ERROR);
            throw e;
        } catch (Exception ex) {
            return handleRead("getCandidateById", ex);
        }
    }

    @Override
    public void updateOnboardingStatus(String candidateID, String status) {
        // Candidate entity maps to the 'candidates' table; status field = applicationStatus
        executeUpdate("updateOnboardingStatus",
                "UPDATE Candidate c SET c.applicationStatus = :status WHERE c.candidateId = :id",
                Map.of("status", status, "id", candidateID));
    }

    // ── Document Management ──────────────────────────────────────────

    @Override
    public void uploadDocument(OnboardingDocument doc) {
        Transaction tx = null;
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            Employee emp = session.get(Employee.class, doc.employeeID);

            com.hrms.db.entities.Document entity = new com.hrms.db.entities.Document();
            entity.setDocumentId(doc.documentID != null ? doc.documentID : UUID.randomUUID().toString());
            entity.setEmployee(emp);
            entity.setDocumentType(doc.documentType);
            entity.setFilePath(doc.filePath);
            entity.setVerificationStatus(doc.verificationStatus != null ? doc.verificationStatus : "PENDING");

            session.persist(entity);
            tx.commit();
            log.log(LogHandler.LogLevel.INFO, REPO, "uploadDocument", "Uploaded " + doc.documentType);

        } catch (Exception ex) {
            if (tx != null) try { tx.rollback(); } catch (Exception r) { /* ignore */ }
            handleError("uploadDocument", ex, ErrorLevel.ERROR);
        }
    }

    @Override
    public List<OnboardingDocument> getDocumentsByEmployee(String employeeID) {
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            return session.createQuery(
                    "FROM Document d WHERE d.employee.empId = :id", com.hrms.db.entities.Document.class)
                    .setParameter("id", employeeID)
                    .getResultList().stream().map(this::toDocumentDTO).collect(Collectors.toList());
        } catch (Exception ex) {
            handleError("getDocumentsByEmployee", ex, ErrorLevel.ERROR);
            return Collections.emptyList();
        }
    }

    @Override
    public void updateDocumentVerificationStatus(String documentID, String status) {
        executeUpdate("updateDocumentVerificationStatus",
                "UPDATE Document d SET d.verificationStatus = :status WHERE d.documentId = :id",
                Map.of("status", status, "id", documentID));
    }

    // ── Policy Compliance ────────────────────────────────────────────

    @Override
    public List<CompliancePolicy> getAllPolicies() {
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            return session.createQuery("FROM CompliancePolicy", CompliancePolicy.class)
                    .getResultList().stream().map(this::toPolicyDTO).collect(Collectors.toList());
        } catch (Exception ex) {
            handleError("getAllPolicies", ex, ErrorLevel.ERROR);
            return Collections.emptyList();
        }
    }

    @Override
    public void updateComplianceStatus(String policyID, String status) {
        executeUpdate("updateComplianceStatus",
                "UPDATE CompliancePolicy p SET p.status = :status WHERE p.policyId = :id",
                Map.of("status", status, "id", policyID));
    }

    // ── Onboarding Tasks ─────────────────────────────────────────────

    @Override
    public void assignTask(OnboardingTaskDTO task) {
        Transaction tx = null;
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            Employee emp = session.get(Employee.class, task.employeeID);

            OnboardingTask entity = new OnboardingTask();
            entity.setTaskId(task.taskID != null ? task.taskID : UUID.randomUUID().toString());
            entity.setEmployee(emp);
            entity.setTaskName(task.taskName);
            entity.setTaskType(task.taskType);
            entity.setAssignedTo(task.assignedTo);
            entity.setStatus(task.status != null ? task.status : "PENDING");
            entity.setDueDate(task.dueDate);

            session.persist(entity);
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) try { tx.rollback(); } catch (Exception r) { /* ignore */ }
            handleError("assignTask", ex, ErrorLevel.ERROR);
        }
    }

    @Override
    public List<OnboardingTaskDTO> getTasksByEmployee(String employeeID) {
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            return session.createQuery(
                    "FROM OnboardingTask t WHERE t.employee.empId = :id", OnboardingTask.class)
                    .setParameter("id", employeeID)
                    .getResultList().stream().map(this::toTaskDTO).collect(Collectors.toList());
        } catch (Exception ex) {
            handleError("getTasksByEmployee", ex, ErrorLevel.ERROR);
            return Collections.emptyList();
        }
    }

    @Override
    public void updateTaskStatus(String taskID, String status) {
        executeUpdate("updateTaskStatus",
                "UPDATE OnboardingTask t SET t.status = :status WHERE t.taskId = :id",
                Map.of("status", status, "id", taskID));
    }

    // ── Exit Management ──────────────────────────────────────────────

    @Override
    public void createExitRequest(ExitRequestDTO request) {
        // Map to ClearanceSettlement entity (closest available exit tracking entity)
        Transaction tx = null;
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            Employee emp = session.get(Employee.class, request.employeeID);

            ClearanceSettlement cs = new ClearanceSettlement();
            cs.setSettlementId(request.requestID != null ? request.requestID : UUID.randomUUID().toString());
            cs.setEmployee(emp);
            cs.setSettlementType(request.exitType);
            cs.setStatus(request.status != null ? request.status : "SUBMITTED");
            cs.setNotes(request.reason);

            session.persist(cs);
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) try { tx.rollback(); } catch (Exception r) { /* ignore */ }
            handleError("createExitRequest", ex, ErrorLevel.CRITICAL);
        }
    }

    @Override
    public ExitRequestDTO getExitDetails(String employeeID) {
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            ClearanceSettlement cs = session.createQuery(
                    "FROM ClearanceSettlement cs WHERE cs.employee.empId = :id ORDER BY cs.settlementId DESC",
                    ClearanceSettlement.class)
                    .setParameter("id", employeeID)
                    .setMaxResults(1).uniqueResult();
            if (cs == null) return null;
            return toExitRequestDTO(cs);
        } catch (Exception ex) {
            handleError("getExitDetails", ex, ErrorLevel.ERROR);
            return null;
        }
    }

    @Override
    public void updateExitStatus(String employeeID, String status) {
        executeUpdate("updateExitStatus",
                "UPDATE ClearanceSettlement cs SET cs.status = :status WHERE cs.employee.empId = :id",
                Map.of("status", status, "id", employeeID));
    }

    // ── Exit Interview ───────────────────────────────────────────────

    @Override
    public void recordExitInterview(ExitInterviewDTO interview) {
        Transaction tx = null;
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            ExitInterview entity = new ExitInterview();
            entity.setInterviewId(interview.interviewID != null ? interview.interviewID : UUID.randomUUID().toString());
            entity.setEmpId(interview.employeeID);
            entity.setFeedbackText(interview.feedback);
            entity.setPrimaryReason(interview.primaryReason);
            entity.setSatisfactionRating(interview.satisfactionRating);
            entity.setExitDate(interview.conductedOn);

            session.persist(entity);
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) try { tx.rollback(); } catch (Exception r) { /* ignore */ }
            handleError("recordExitInterview", ex, ErrorLevel.ERROR);
        }
    }

    @Override
    public ExitInterviewDTO getInterviewByEmployee(String employeeID) {
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            ExitInterview ei = session.createQuery(
                    "FROM ExitInterview e WHERE e.empId = :id ORDER BY e.exitDate DESC",
                    ExitInterview.class)
                    .setParameter("id", employeeID)
                    .setMaxResults(1).uniqueResult();
            if (ei == null) return null;
            return toExitInterviewDTO(ei);
        } catch (Exception ex) {
            handleError("getInterviewByEmployee", ex, ErrorLevel.ERROR);
            return null;
        }
    }

    @Override
    public void updateInterviewDetails(String interviewID, String feedback, String reason) {
        executeUpdate("updateInterviewDetails",
                "UPDATE ExitInterview e SET e.feedbackText = :feedback, e.primaryReason = :reason WHERE e.interviewId = :id",
                Map.of("feedback", feedback, "reason", reason, "id", interviewID));
    }

    // ── Clearance Settlement ─────────────────────────────────────────

    @Override
    public void createSettlement(ClearanceDTO clearance) {
        Transaction tx = null;
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            Employee emp = session.get(Employee.class, clearance.employeeID);
            ClearanceSettlement cs = new ClearanceSettlement();
            cs.setSettlementId(clearance.clearanceID != null ? clearance.clearanceID : UUID.randomUUID().toString());
            cs.setEmployee(emp);
            cs.setStatus(clearance.clearanceStatus != null ? clearance.clearanceStatus : "INITIATED");
            cs.setFinalSettlementAmount(clearance.finalSettlementAmount);
            cs.setNotes(clearance.remarks);
            session.persist(cs);
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) try { tx.rollback(); } catch (Exception r) { /* ignore */ }
            handleError("createSettlement", ex, ErrorLevel.CRITICAL);
        }
    }

    @Override
    public ClearanceDTO getSettlement(String employeeID) {
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            ClearanceSettlement cs = session.createQuery(
                    "FROM ClearanceSettlement cs WHERE cs.employee.empId = :id",
                    ClearanceSettlement.class)
                    .setParameter("id", employeeID).setMaxResults(1).uniqueResult();
            if (cs == null) return null;
            return toClearanceDTO(cs);
        } catch (Exception ex) {
            handleError("getSettlement", ex, ErrorLevel.ERROR);
            return null;
        }
    }

    @Override
    public void updateClearanceStatus(String clearanceID, String status) {
        executeUpdate("updateClearanceStatus",
                "UPDATE ClearanceSettlement cs SET cs.status = :status WHERE cs.settlementId = :id",
                Map.of("status", status, "id", clearanceID));
    }

    // ── Notifications ────────────────────────────────────────────────

    @Override
    public void sendNotification(OnboardingNotification notification) {
        Transaction tx = null;
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            Notification entity = new Notification();
            entity.setNotificationId(notification.notificationID != null ? notification.notificationID : UUID.randomUUID().toString());
            entity.setRecipientEmpId(notification.recipientEmployeeID);
            entity.setNotificationType(notification.notificationType);
            entity.setSubject(notification.subject);
            entity.setBody(notification.body);
            entity.setStatus(notification.status != null ? notification.status : "PENDING");
            entity.setScheduledAt(notification.scheduledAt);
            session.persist(entity);
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) try { tx.rollback(); } catch (Exception r) { /* ignore */ }
            handleError("sendNotification", ex, ErrorLevel.ERROR);
        }
    }

    @Override
    public List<OnboardingNotification> getNotifications(String employeeID) {
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            return session.createQuery(
                    "FROM Notification n WHERE n.recipientEmpId = :id ORDER BY n.scheduledAt DESC",
                    Notification.class)
                    .setParameter("id", employeeID)
                    .getResultList().stream().map(this::toNotificationDTO).collect(Collectors.toList());
        } catch (Exception ex) {
            handleError("getNotifications", ex, ErrorLevel.ERROR);
            return Collections.emptyList();
        }
    }

    @Override
    public void updateNotificationStatus(String notificationID, String status) {
        executeUpdate("updateNotificationStatus",
                "UPDATE Notification n SET n.status = :status WHERE n.notificationId = :id",
                Map.of("status", status, "id", notificationID));
    }

    // ── Progress Tracking ────────────────────────────────────────────

    @Override
    public ProgressDTO getProgress(String employeeID, String processType) {
        // Progress is computed from task completion, not a dedicated table
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            long total = (Long) session.createQuery(
                    "SELECT COUNT(t) FROM OnboardingTask t WHERE t.employee.empId = :id", Long.class)
                    .setParameter("id", employeeID).uniqueResult();
            long done = (Long) session.createQuery(
                    "SELECT COUNT(t) FROM OnboardingTask t WHERE t.employee.empId = :id AND t.status = 'DONE'",
                    Long.class).setParameter("id", employeeID).uniqueResult();

            ProgressDTO p = new ProgressDTO();
            p.employeeID       = employeeID;
            p.processType      = processType;
            p.totalSteps       = (int) total;
            p.completedSteps   = (int) done;
            p.completionPercent = total == 0 ? 0.0 : (done * 100.0) / total;
            p.overallStatus    = total == 0 ? "NOT_STARTED"
                               : done == total ? "COMPLETED" : "IN_PROGRESS";
            return p;
        } catch (Exception ex) {
            handleError("getProgress", ex, ErrorLevel.ERROR);
            return null;
        }
    }

    @Override
    public void updateProgressStatus(String processID, String status) {
        executeUpdate("updateProgressStatus",
                "UPDATE OnboardingTask t SET t.status = :status WHERE t.taskId = :id",
                Map.of("status", status, "id", processID));
    }

    // ── Private Helpers ──────────────────────────────────────────────

    /** Runs a HQL UPDATE query and auto-commits. */
    private void executeUpdate(String method, String hql, Map<String, Object> params) {
        Transaction tx = null;
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            var q = session.createMutationQuery(hql);
            params.forEach(q::setParameter);
            q.executeUpdate();
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) try { tx.rollback(); } catch (Exception r) { /* ignore */ }
            handleError(method, ex, ErrorLevel.ERROR);
        }
    }

    private void handleError(String method, Exception ex, ErrorLevel level) {
        errorChain.handle(REPO + "." + method,
                new DatabaseException(REPO + "." + method, ex.getMessage(), ex), level);
    }

    private <T> T handleRead(String method, Exception ex) {
        handleError(method, ex, ErrorLevel.ERROR);
        return null;
    }

    // ── Mapping Helpers ──────────────────────────────────────────────

    private OnboardingEmployee toOnboardingEmployee(Employee e) {
        OnboardingEmployee o = new OnboardingEmployee();
        o.employeeID      = e.getEmpId();
        o.name            = e.getName();
        o.email           = e.getEmail();
        o.department      = e.getDepartment();
        o.designation     = e.getDesignation();
        o.employmentStatus = e.getEmploymentStatus();
        o.dateOfJoining   = e.getDateOfJoining();
        return o;
    }

    private OnboardingCandidate toCandidate(Candidate c) {
        OnboardingCandidate dto = new OnboardingCandidate();
        dto.candidateID      = c.getCandidateId();
        dto.name             = c.getName();
        dto.email            = c.getEmail();
        dto.phone            = c.getPhone();
        dto.onboardingStatus = c.getApplicationStatus();
        return dto;
    }

    private OnboardingDocument toDocumentDTO(com.hrms.db.entities.Document d) {
        OnboardingDocument dto = new OnboardingDocument();
        dto.documentID          = d.getDocumentId();
        dto.employeeID          = d.getEmployee() != null ? d.getEmployee().getEmpId() : null;
        dto.documentType        = d.getDocumentType();
        dto.filePath            = d.getFilePath();
        dto.verificationStatus  = d.getVerificationStatus();
        return dto;
    }

    private CompliancePolicy toPolicyDTO(com.hrms.db.entities.CompliancePolicy p) {
        CompliancePolicy dto = new CompliancePolicy();
        dto.policyID   = p.getPolicyId();
        dto.policyName = p.getPolicyName();
        dto.policyText = p.getPolicyText();
        dto.complianceStatus = p.getStatus();
        return dto;
    }

    private OnboardingTaskDTO toTaskDTO(OnboardingTask t) {
        OnboardingTaskDTO dto = new OnboardingTaskDTO();
        dto.taskID     = t.getTaskId();
        dto.employeeID = t.getEmployee() != null ? t.getEmployee().getEmpId() : null;
        dto.taskName   = t.getTaskName();
        dto.taskType   = t.getTaskType();
        dto.assignedTo = t.getAssignedTo();
        dto.status     = t.getStatus();
        dto.dueDate    = t.getDueDate();
        return dto;
    }

    private ExitRequestDTO toExitRequestDTO(ClearanceSettlement cs) {
        ExitRequestDTO dto = new ExitRequestDTO();
        dto.requestID  = cs.getSettlementId();
        dto.employeeID = cs.getEmployee() != null ? cs.getEmployee().getEmpId() : null;
        dto.exitType   = cs.getSettlementType();
        dto.status     = cs.getStatus();
        dto.reason     = cs.getNotes();
        return dto;
    }

    private ExitInterviewDTO toExitInterviewDTO(ExitInterview ei) {
        ExitInterviewDTO dto = new ExitInterviewDTO();
        dto.interviewID       = ei.getInterviewId();
        dto.employeeID        = ei.getEmpId();
        dto.feedback          = ei.getFeedbackText();
        dto.primaryReason     = ei.getPrimaryReason();
        dto.satisfactionRating = ei.getSatisfactionRating() != null ? ei.getSatisfactionRating() : 0;
        dto.conductedOn       = ei.getExitDate();
        return dto;
    }

    private ClearanceDTO toClearanceDTO(ClearanceSettlement cs) {
        ClearanceDTO dto = new ClearanceDTO();
        dto.clearanceID            = cs.getSettlementId();
        dto.employeeID             = cs.getEmployee() != null ? cs.getEmployee().getEmpId() : null;
        dto.clearanceStatus        = cs.getStatus();
        dto.finalSettlementAmount  = cs.getFinalSettlementAmount() != null ? cs.getFinalSettlementAmount() : 0.0;
        dto.remarks                = cs.getNotes();
        return dto;
    }

    private OnboardingNotification toNotificationDTO(Notification n) {
        OnboardingNotification dto = new OnboardingNotification();
        dto.notificationID        = n.getNotificationId();
        dto.recipientEmployeeID   = n.getRecipientEmpId();
        dto.notificationType      = n.getNotificationType();
        dto.subject               = n.getSubject();
        dto.body                  = n.getBody();
        dto.status                = n.getStatus();
        dto.scheduledAt           = n.getScheduledAt();
        return dto;
    }
}
