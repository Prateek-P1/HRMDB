package com.hrms.db.entities;

import jakarta.persistence.*;

/**
 * Custom fields within forms — user-defined data points.
 * Required by: Customization subsystem.
 */
@Entity
@Table(name = "custom_fields")
public class CustomField {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "field_id")
    private Integer fieldId;

    @Column(name = "form_id")
    private Integer formId;

    @Column(name = "field_name", nullable = false, length = 100)
    private String fieldName;

    @Column(name = "field_type", length = 30)
    private String fieldType; // TEXT, NUMBER, DATE, BOOLEAN, DROPDOWN

    @Column(name = "is_mandatory")
    private Boolean isMandatory = false;

    @Column(name = "default_value", length = 200)
    private String defaultValue;

    // --- Getters & Setters ---

    public Integer getFieldId() { return fieldId; }
    public void setFieldId(Integer fieldId) { this.fieldId = fieldId; }

    public Integer getFormId() { return formId; }
    public void setFormId(Integer formId) { this.formId = formId; }

    public String getFieldName() { return fieldName; }
    public void setFieldName(String fieldName) { this.fieldName = fieldName; }

    public String getFieldType() { return fieldType; }
    public void setFieldType(String fieldType) { this.fieldType = fieldType; }

    public Boolean getIsMandatory() { return isMandatory; }
    public void setIsMandatory(Boolean isMandatory) { this.isMandatory = isMandatory; }

    public String getDefaultValue() { return defaultValue; }
    public void setDefaultValue(String defaultValue) { this.defaultValue = defaultValue; }
}
