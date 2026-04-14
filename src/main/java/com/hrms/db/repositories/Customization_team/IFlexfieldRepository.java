package com.hrms.db.repositories.Customization_team;
import java.util.List;

/**
 * INTERFACE: IFlexfieldRepository
 * Subsystem: Customization
 * Component: Flexfield Manager
 *
 * Provides DB access for key and descriptive flexfield definitions,
 * including segment structure and validation rules.
 *
 * DB Team: Please implement this interface and provide the concrete class.
 * Entity needed:
 *   FlexField { fieldId: int, fieldName: String, fieldType: String (KEY/DESCRIPTIVE),
 *               isMandatory: boolean, defaultValue: String, segments: String }
 */
public interface IFlexfieldRepository {

    /**
     * WRITE: Add a new flexfield definition.
     */
    void addField(String name, String type, String segments);

    /**
     * READ: Fetch a flexfield by its ID.
     */
    FlexField getFieldById(int fieldId);

    /**
     * READ: Return all flexfield definitions.
     */
    List<FlexField> getAllFlexfields();

    /**
     * WRITE: Delete a flexfield definition by its ID.
     */
    void removeField(int fieldId);

    /**
     * WRITE: Update a specific segment definition within a flexfield.
     */
    void updateFieldSegment(int fieldId, int segmentNumber, String segmentDefinition);

    /**
     * READ: Validate a flexfield value against its defined rules.
     * Returns true if valid, false otherwise.
     */
    boolean validateField(int fieldId);

    /**
     * READ: Get all valid values for a key flexfield.
     */
    List<String> getValues(int fieldId);
}

/**
 * DTO: FlexField
 * Maps to the FlexField entity in the database.
 */
class FlexField {
    public int fieldId;
    public String fieldName;
    public String fieldType;       // KEY or DESCRIPTIVE
    public boolean isMandatory;
    public String defaultValue;
    public String segments;
}
