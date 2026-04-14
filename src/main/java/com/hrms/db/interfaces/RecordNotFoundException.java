package com.hrms.db.interfaces;

/**
 * RecordNotFoundException — thrown when a lookup-by-ID returns no result.
 *
 * This is a specific subclass of DatabaseException so callers can distinguish between:
 *   - A genuine DB failure  (catch DatabaseException)
 *   - A missing record      (catch RecordNotFoundException)
 *
 * Example:
 *   Employee e = repo.getEmployeeById("EMP999");
 *   // if EMP999 doesn't exist → throws RecordNotFoundException("PayrollRepo", "EMP999")
 */
public class RecordNotFoundException extends DatabaseException {

    private final String entityType;
    private final String entityId;

    /**
     * @param operation   the repository method that triggered this (e.g. "PayrollRepository.fetchEmployeeData")
     * @param entityType  the type of record that was sought (e.g. "Employee", "PayrollResult")
     * @param entityId    the identifier that was searched (e.g. "EMP001", "42")
     */
    public RecordNotFoundException(String operation, String entityType, String entityId) {
        super(operation, entityType + " with id='" + entityId + "' was not found in the database.");
        this.entityType = entityType;
        this.entityId   = entityId;
    }

    public String getEntityType() { return entityType; }
    public String getEntityId()   { return entityId; }
}
