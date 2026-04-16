package com.hrms.db.repositories.Customization_team;
import java.util.List;

/**
 * INTERFACE: IEITRepository
 * Subsystem: Customization
 * Component: EIT Handler (Extra Information Type)
 *
 * Provides DB access for organisation-specific Extra Information Type definitions
 * and their association with employees, jobs, and positions.
 *
 * DB Team: Please implement this interface and provide the concrete class.
 * Entity needed:
 *   EIT { eitId: int, eitName: String, infoCategory: String, infoValue: String,
 *         context: String (EMPLOYEE/JOB/POSITION), allowMultiRow: boolean }
 */
public interface IEITRepository {

    /**
     * WRITE: Create a new EIT definition.
     */
    void addExtraInfoType(String name, String category, String infoValue);

    /**
     * READ: Fetch an EIT record by its ID.
     */
    EIT getEITById(int eitId);

    /**
     * READ: Return all EIT definitions.
     */
    List<EIT> getAllEITs();

    /**
     * WRITE: Update a specific segment within an EIT.
     */
    void updateExtraInfo(int eitId, int segmentId, String newValue);

    /**
     * WRITE: Delete an EIT definition by its ID.
     */
    void deleteExtraInfo(int eitId);

    /**
     * WRITE: Link an EIT to a specific employee record.
     */
    void assignEITToEmployee(int eitId, int employeeId);

    /**
     * READ: Get all EITs associated with a specific employee.
     */
    List<EIT> getEITsByEmployee(int employeeId);
}

/**
 * DTO: EIT
 * Maps to the EIT entity in the database.
 */
class EIT {
    public int eitId;
    public String eitName;
    public String infoCategory;
    public String infoValue;
    public String context;         // EMPLOYEE, JOB, or POSITION
    public boolean allowMultiRow;
}
