package com.hrms.db.repositories.Customization_team;
import java.util.List;

/**
 * INTERFACE: IWorkflowRepository
 * Subsystem: Customization
 * Component: Workflow Engine
 *
 * Provides DB access for approval workflows, their steps,
 * user assignments, and escalation rules.
 *
 * DB Team: Please implement this interface and provide the concrete class.
 * Entities needed:
 *   Workflow     { workflowId: int, workflowName: String, currentStatus: String, assignedTo: String }
 *   WorkflowStep { stepId: int, workflowId: int, stepName: String, assignee: String, escalationHours: int }
 */
public interface IWorkflowRepository {

    /**
     * READ: Fetch a workflow by its ID.
     */
    Workflow getWorkflowById(int workflowId);

    /**
     * READ: Return all defined workflows.
     */
    List<Workflow> getAllWorkflows();

    /**
     * WRITE: Persist a new workflow. Returns the generated workflowId.
     */
    int saveWorkflow(Workflow wf);

    /**
     * WRITE: Set workflow status (e.g., Active / Inactive).
     */
    void updateWorkflowStatus(int workflowId, String status);

    /**
     * READ: Fetch all steps for a given workflow.
     */
    List<WorkflowStep> getWorkflowSteps(int workflowId);

    /**
     * WRITE: Add a step to an existing workflow.
     */
    void addStep(int workflowId, String stepName, String assignee, int escalationHours);

    /**
     * WRITE: Remove a step by its ID.
     */
    void removeStep(int stepId);

    /**
     * WRITE: Assign a user or role to a specific workflow step.
     */
    void assignUserToStep(int stepId, String userId);

    /**
     * READ: Get the current status of a workflow instance.
     */
    String getStatus(int workflowId);
}

/**
 * DTO: Workflow
 * Maps to the Workflow entity in the database.
 */
class Workflow {
    public int workflowId;
    public String workflowName;
    public String currentStatus;
    public String assignedTo;
}

/**
 * DTO: WorkflowStep
 * Maps to the WorkflowStep entity in the database.
 */
class WorkflowStep {
    public int stepId;
    public int workflowId;
    public String stepName;
    public String assignee;
    public int escalationHours;
}
