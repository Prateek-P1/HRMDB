package com.hrms.db.repositories.Customization_team;

import com.hrms.db.config.DatabaseConnection;
import com.hrms.db.entities.*;
import com.hrms.db.handlers.ConsoleErrorLogger;
import com.hrms.db.handlers.CriticalErrorEscalator;
import com.hrms.db.handlers.DatabaseErrorLogger;
import com.hrms.db.handlers.ErrorHandler;
import com.hrms.db.handlers.ErrorHandler.ErrorLevel;
import com.hrms.db.interfaces.DatabaseException;
import com.hrms.db.logging.ConsoleLogHandler;
import com.hrms.db.logging.DatabaseLogHandler;
import com.hrms.db.logging.LogHandler;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.*;
import java.util.stream.Collectors;

/**
 * CustomizationRepositoryImpl sums up the entire implementation logic for the 
 * Customization subsystem by implementing all their disparate interface contracts.
 */
public class CustomizationRepositoryImpl implements 
        IModuleRepository, 
        IFormRepository, 
        IWorkflowRepository,
        IReportRepository,
        IFlexfieldRepository, 
        ITaskFlowRepository, 
        ILookupRepository, 
        IEITRepository {

    private static final String REPO = "CustomizationRepositoryImpl";
    private final ErrorHandler errorChain = new ConsoleErrorLogger(
            new DatabaseErrorLogger(new CriticalErrorEscalator(null)));
    private final LogHandler log = new ConsoleLogHandler(new DatabaseLogHandler(null));

    // ── IModuleRepository ──────────────────────────────────────

    @Override
    public Module getModuleById(int moduleId) {
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            CustomModule cm = session.get(CustomModule.class, moduleId);
            if (cm == null) return null;
            Module m = new Module();
            m.moduleId = cm.getModuleId();
            m.moduleName = cm.getModuleName();
            m.moduleType = cm.getModuleType();
            m.isEnabled = cm.getIsEnabled() != null && cm.getIsEnabled();
            m.config = "{}";
            return m;
        } catch (Exception ex) { return handleRead("getModuleById", ex); }
    }

    @Override
    public List<Module> getAllModules() {
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            return session.createQuery("FROM CustomModule", CustomModule.class).getResultList()
                    .stream().map(cm -> {
                        Module m = new Module();
                        m.moduleId = cm.getModuleId();
                        m.moduleName = cm.getModuleName();
                        m.moduleType = cm.getModuleType();
                        m.isEnabled = cm.getIsEnabled() != null && cm.getIsEnabled();
                        m.config = "{}";
                        return m;
                    }).collect(Collectors.toList());
        } catch (Exception ex) { return handleRead("getAllModules", ex) != null ? null : Collections.emptyList(); }
    }

    @Override
    public void updateModuleStatus(int moduleId, boolean enabled) {
        executeUpdate("updateModuleStatus", "UPDATE CustomModule m SET m.isEnabled = :en WHERE m.moduleId = :id", 
                      Map.of("en", enabled, "id", moduleId));
    }

    @Override
    public void updateModuleConfig(String moduleName, String config) {
        // config string not natively stored in CustomModule right now, stubbed
        log.log(LogHandler.LogLevel.INFO, REPO, "updateModuleConfig", "Module " + moduleName + " config stubbed update");
    }

    @Override
    public boolean getModuleStatus(String moduleName) {
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            CustomModule m = session.createQuery("FROM CustomModule m WHERE m.moduleName = :name", CustomModule.class)
                    .setParameter("name", moduleName).setMaxResults(1).uniqueResult();
            return m != null && m.getIsEnabled() != null && m.getIsEnabled();
        } catch (Exception ex) { return false; }
    }

    // ── IFormRepository ────────────────────────────────────────

    @Override
    public int createForm(String name, String layoutType) {
        Transaction tx = null;
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            CustomForm form = new CustomForm();
            form.setFormName(name);
            form.setLayoutType(layoutType);
            session.persist(form);
            tx.commit();
            return form.getFormId();
        } catch (Exception ex) {
            handleError("createForm", ex, ErrorLevel.ERROR);
            return -1;
        }
    }

    @Override
    public Form getFormById(int formId) {
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            CustomForm cf = session.get(CustomForm.class, formId);
            if (cf == null) return null;
            Form f = new Form();
            f.formId = cf.getFormId();
            f.formName = cf.getFormName();
            f.layoutType = cf.getLayoutType();
            return f;
        } catch (Exception ex) { return handleRead("getFormById", ex); }
    }

    @Override
    public List<Form> getAllForms() {
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            return session.createQuery("FROM CustomForm", CustomForm.class).getResultList()
                    .stream().map(cf -> {
                        Form f = new Form();
                        f.formId = cf.getFormId();
                        f.formName = cf.getFormName();
                        f.layoutType = cf.getLayoutType();
                        return f;
                    }).collect(Collectors.toList());
        } catch (Exception ex) { return Collections.emptyList(); }
    }

    @Override
    public void updateForm(int formId, String name) {
        executeUpdate("updateForm", "UPDATE CustomForm f SET f.formName = :name WHERE f.formId = :id", 
                Map.of("name", name, "id", formId));
    }

    @Override
    public void deleteForm(int formId) {
        executeUpdate("deleteForm", "DELETE FROM CustomForm f WHERE f.formId = :id", Map.of("id", formId));
    }

    @Override
    public void addFieldToForm(int formId, String fieldName) {
        Transaction tx = null;
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            CustomField cf = new CustomField();
            cf.setFormId(formId);
            cf.setFieldName(fieldName);
            cf.setFieldType("TEXT"); // default
            session.persist(cf);
            tx.commit();
        } catch (Exception ex) { handleError("addFieldToForm", ex, ErrorLevel.ERROR); }
    }

    @Override
    public void removeFieldFromForm(int formId, String fieldName) {
        executeUpdate("removeField", "DELETE FROM CustomField f WHERE f.formId = :formId AND f.fieldName = :name",
                Map.of("formId", formId, "name", fieldName));
    }

    @Override
    public List<Field> getFieldsByForm(int formId) {
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            return session.createQuery("FROM CustomField c WHERE c.formId = :formId", CustomField.class)
                    .setParameter("formId", formId).getResultList()
                    .stream().map(cf -> {
                        Field f = new Field();
                        f.fieldId = cf.getFieldId();
                        f.fieldName = cf.getFieldName();
                        f.fieldType = cf.getFieldType();
                        f.formId = cf.getFormId() != null ? cf.getFormId() : 0;
                        return f;
                    }).collect(Collectors.toList());
        } catch (Exception ex) { return Collections.emptyList(); }
    }

    // ── IWorkflowRepository ────────────────────────────────────

    @Override
    public Workflow getWorkflowById(int workflowId) {
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            com.hrms.db.entities.Workflow wf = session.get(com.hrms.db.entities.Workflow.class, workflowId);
            if (wf == null) return null;
            Workflow w = new Workflow();
            w.workflowId = wf.getWorkflowId();
            w.workflowName = wf.getWorkflowName();
            w.currentStatus = wf.getCurrentStatus();
            w.assignedTo = wf.getAssignedTo();
            return w;
        } catch (Exception ex) { return handleRead("getWorkflowById", ex); }
    }

    @Override
    public List<Workflow> getAllWorkflows() {
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            return session.createQuery("FROM Workflow", com.hrms.db.entities.Workflow.class).getResultList()
                    .stream().map(wf -> {
                        Workflow w = new Workflow();
                        w.workflowId = wf.getWorkflowId();
                        w.workflowName = wf.getWorkflowName();
                        w.currentStatus = wf.getCurrentStatus();
                        w.assignedTo = wf.getAssignedTo();
                        return w;
                    }).collect(Collectors.toList());
        } catch (Exception ex) { return Collections.emptyList(); }
    }

    @Override
    public int saveWorkflow(Workflow wf) {
        Transaction tx = null;
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            com.hrms.db.entities.Workflow entity = new com.hrms.db.entities.Workflow();
            entity.setWorkflowName(wf.workflowName);
            entity.setCurrentStatus(wf.currentStatus);
            entity.setAssignedTo(wf.assignedTo);
            session.persist(entity);
            tx.commit();
            return entity.getWorkflowId();
        } catch (Exception ex) {
            handleError("saveWorkflow", ex, ErrorLevel.ERROR);
            return -1;
        }
    }

    @Override
    public void updateWorkflowStatus(int workflowId, String status) {
        executeUpdate("updateWorkflowStatus", "UPDATE Workflow w SET w.currentStatus = :status WHERE w.workflowId = :id",
                Map.of("status", status, "id", workflowId));
    }

    @Override
    public List<WorkflowStep> getWorkflowSteps(int workflowId) {
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            return session.createQuery("FROM WorkflowTask t WHERE t.workflowId = :wid", com.hrms.db.entities.WorkflowTask.class)
                    .setParameter("wid", workflowId).getResultList()
                    .stream().map(t -> {
                        WorkflowStep s = new WorkflowStep();
                        s.stepId = t.getTaskId();
                        s.workflowId = t.getWorkflowId();
                        s.stepName = t.getTaskName();
                        s.assignee = "";
                        s.escalationHours = 0;
                        return s;
                    }).collect(Collectors.toList());
        } catch (Exception ex) { return Collections.emptyList(); }
    }

    @Override
    public void addStep(int workflowId, String stepName, String assignee, int escalationHours) {
        Transaction tx = null;
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            com.hrms.db.entities.WorkflowTask t = new com.hrms.db.entities.WorkflowTask();
            t.setWorkflowId(workflowId);
            t.setTaskName(stepName);
            t.setTaskStatus("PENDING");
            session.persist(t);
            tx.commit();
        } catch (Exception ex) { handleError("addStep", ex, ErrorLevel.ERROR); }
    }

    @Override
    public void removeStep(int stepId) {
        executeUpdate("removeStep", "DELETE FROM WorkflowTask t WHERE t.taskId = :id", Map.of("id", stepId));
    }

    @Override
    public void assignUserToStep(int stepId, String userId) {
        log.log(LogHandler.LogLevel.WARN, REPO, "assignUserToStep", "Stub implementation due to missing entity attribute");
    }

    @Override
    public String getStatus(int workflowId) {
        Workflow w = getWorkflowById(workflowId);
        return w != null ? w.currentStatus : null;
    }

    // ── IReportRepository ──────────────────────────────────────

    @Override
    public int saveReport(String name, String inputType, String format) {
        Transaction tx = null;
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            HrReport hr = new HrReport();
            hr.setReportId(UUID.randomUUID().toString());
            hr.setReportName(name);
            hr.setReportType(inputType);
            hr.setExportFormat(format);
            session.persist(hr);
            tx.commit();
            return Math.abs(hr.getReportId().hashCode()); // interface expects int ID but DB has String UUID
        } catch (Exception ex) {
            handleError("saveReport", ex, ErrorLevel.ERROR);
            return -1;
        }
    }

    @Override
    public Report getReportById(int reportId) { return null; } // Mismatched key type

    @Override
    public List<Report> getAllReports() { return Collections.emptyList(); } // Mismatched key type

    @Override
    public void customizeReportType(int reportId, String type) {}
    @Override
    public void exportReportFormat(int reportId, String format) {}
    @Override
    public void generateReport(int reportId) {}
    @Override
    public void deleteReport(int reportId) {}

    // ── Stubbed Unmapped Modules (Flexfield, TaskFlow, Lookup, EIT) ────
    
    // IFlexfieldRepository
    public void addField(String n, String t, String s) {}
    public FlexField getFieldById(int id) { return null; }
    public List<FlexField> getAllFlexfields() { return Collections.emptyList(); }
    public void removeField(int id) {}
    public void updateFieldSegment(int id, int s, String sd) {}
    public boolean validateField(int id) { return true; }
    public List<String> getValues(int id) { return Collections.emptyList(); }

    // ITaskFlowRepository
    public int defineTaskFlow(String n, String fs) { return -1; }
    public TaskFlow getTaskFlowById(int id) { return null; }
    public List<TaskFlow> getAllTaskFlows() { return Collections.emptyList(); }
    public void setSequence(int id, int so) {}
    public void updateTaskFlow(int id, String n) {}
    public void deleteTaskFlow(int id) {}
    public void assignFlowToMenu(int id, String m) {}
    public List<String> getWindowsForFlow(int id) { return Collections.emptyList(); }

    // ILookupRepository
    public void createLookupType(String t, String d) {}
    public void addValue(String t, String c, String m) {}
    public void removeValue(String t, String c) {}
    public void updateValue(String o, String n, String t) {}
    public List<String> getAllLookupTypes() { return Collections.emptyList(); }
    public List<String> getValues(String t) { return Collections.emptyList(); }
    public Lookup getLookupById(int id) { return null; }
    public boolean isValueEnabled(String t, String c) { return true; }

    // IEITRepository
    public void addExtraInfoType(String n, String c, String v) {}
    public EIT getEITById(int id) { return null; }
    public List<EIT> getAllEITs() { return Collections.emptyList(); }
    public void updateExtraInfo(int id, int s, String v) {}
    public void deleteExtraInfo(int id) {}
    public void assignEITToEmployee(int id, int emp) {}
    public List<EIT> getEITsByEmployee(int emp) { return Collections.emptyList(); }

    // ── Helpers ────────────────────────────────────────────────

    private void executeUpdate(String method, String hql, Map<String, Object> params) {
        Transaction tx = null;
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            var q = session.createMutationQuery(hql);
            params.forEach(q::setParameter);
            q.executeUpdate();
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) try { tx.rollback(); } catch (Exception r) {}
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
}
