package com.hrms.db.repositories.Customization_team;
import java.util.Date;
import java.util.List;

/**
 * INTERFACE: IFormRepository
 * Subsystem: Customization
 * Component: Form Designer
 *
 * Provides DB access for creation, editing, and retrieval of
 * custom HR forms and their associated field layouts.
 *
 * DB Team: Please implement this interface and provide the concrete class.
 * Entities needed:
 *   Form  { formId: int, formName: String, layoutType: String, createdDate: Date }
 *   Field { fieldId: int, fieldName: String, fieldType: String, formId: int }
 */
public interface IFormRepository {

    /**
     * WRITE: Create a new form. Returns the generated formId.
     */
    int createForm(String name, String layoutType);

    /**
     * READ: Fetch a form record by its ID.
     */
    Form getFormById(int formId);

    /**
     * READ: Return all form definitions.
     */
    List<Form> getAllForms();

    /**
     * WRITE: Update an existing form's name.
     */
    void updateForm(int formId, String name);

    /**
     * WRITE: Delete a form and its associated fields.
     */
    void deleteForm(int formId);

    /**
     * WRITE: Add a field reference to a form.
     */
    void addFieldToForm(int formId, String fieldName);

    /**
     * WRITE: Remove a field from a form.
     */
    void removeFieldFromForm(int formId, String fieldName);

    /**
     * READ: Get all fields belonging to a specific form.
     */
    List<Field> getFieldsByForm(int formId);
}

/**
 * DTO: Form
 * Maps to the Form entity in the database.
 */
class Form {
    public int formId;
    public String formName;
    public String layoutType;
    public Date createdDate;
}

/**
 * DTO: Field
 * Maps to the Field entity in the database.
 */
class Field {
    public int fieldId;
    public String fieldName;
    public String fieldType;
    public int formId;
}
