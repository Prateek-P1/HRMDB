package com.hrms.db.repositories.Customization_team;
import java.util.List;

/**
 * INTERFACE: ITaskFlowRepository
 * Subsystem: Customization
 * Component: Task Flow Builder
 *
 * Provides DB access for multi-window task flow definitions,
 * window sequencing, and menu linkages.
 *
 * DB Team: Please implement this interface and provide the concrete class.
 * Entity needed:
 *   TaskFlow { taskId: int, flowName: String, flowStatus: String, sequenceOrder: int,
 *              linkedMenu: String, validateOnNext: boolean, allowBackNav: boolean }
 */
public interface ITaskFlowRepository {

    /**
     * WRITE: Create a new task flow. Returns the generated taskId.
     */
    int defineTaskFlow(String name, String flowStatus);

    /**
     * READ: Fetch a task flow by its ID.
     */
    TaskFlow getTaskFlowById(int taskId);

    /**
     * READ: Return all task flow definitions.
     */
    List<TaskFlow> getAllTaskFlows();

    /**
     * WRITE: Set the window/step order within a task flow.
     */
    void setSequence(int taskId, int sequenceOrder);

    /**
     * WRITE: Update the name of an existing task flow.
     */
    void updateTaskFlow(int taskId, String newName);

    /**
     * WRITE: Delete a task flow definition by its ID.
     */
    void deleteTaskFlow(int taskId);

    /**
     * WRITE: Link a task flow to a navigation menu entry.
     */
    void assignFlowToMenu(int taskId, String menuName);

    /**
     * READ: Get the ordered list of windows/steps for a task flow.
     */
    List<String> getWindowsForFlow(int taskId);
}

/**
 * DTO: TaskFlow
 * Maps to the TaskFlow entity in the database.
 */
class TaskFlow {
    public int taskId;
    public String flowName;
    public String flowStatus;
    public int sequenceOrder;
    public String linkedMenu;
    public boolean validateOnNext;
    public boolean allowBackNav;
}
