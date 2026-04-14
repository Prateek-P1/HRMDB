package com.hrms.db.repositories.Customization_team;
import java.util.Date;
import java.util.List;

/**
 * INTERFACE: ILookupRepository
 * Subsystem: Customization
 * Component: Lookup Customizer
 *
 * Provides DB access for user-defined value lists and lookup tables
 * used across all dropdown fields in the HRMS.
 *
 * DB Team: Please implement this interface and provide the concrete class.
 * Entity needed:
 *   Lookup { lookupId: int, lookupType: String, lookupCode: String, meaning: String,
 *            description: String, isEnabled: boolean, startDate: Date, endDate: Date }
 */
public interface ILookupRepository {

    /**
     * WRITE: Create a new lookup type.
     */
    void createLookupType(String lookupType, String description);

    /**
     * WRITE: Add a new value (code + meaning) to an existing lookup type.
     */
    void addValue(String lookupType, String code, String meaning);

    /**
     * WRITE: Remove a value from a lookup type.
     */
    void removeValue(String lookupType, String code);

    /**
     * WRITE: Update an existing lookup value.
     */
    void updateValue(String oldVal, String newVal, String lookupType);

    /**
     * READ: Return all defined lookup type names.
     */
    List<String> getAllLookupTypes();

    /**
     * READ: Return all values for a given lookup type.
     */
    List<String> getValues(String lookupType);

    /**
     * READ: Fetch a lookup record by its ID.
     */
    Lookup getLookupById(int lookupId);

    /**
     * READ: Check if a specific lookup value/code is currently enabled.
     */
    boolean isValueEnabled(String lookupType, String code);
}

/**
 * DTO: Lookup
 * Maps to the Lookup entity in the database.
 */
class Lookup {
    public int lookupId;
    public String lookupType;
    public String lookupCode;
    public String meaning;
    public String description;
    public boolean isEnabled;
    public Date startDate;
    public Date endDate;
}
