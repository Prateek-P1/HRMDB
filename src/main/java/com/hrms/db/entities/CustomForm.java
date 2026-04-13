package com.hrms.db.entities;

import jakarta.persistence.*;
import java.time.LocalDate;

/**
 * Custom forms — user-defined forms per module.
 * Required by: Customization subsystem.
 */
@Entity
@Table(name = "custom_forms")
public class CustomForm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "form_id")
    private Integer formId;

    @Column(name = "form_name", nullable = false, length = 100)
    private String formName;

    @Column(name = "module_id")
    private Integer moduleId;

    @Column(name = "layout_type", length = 30)
    private String layoutType;

    @Column(name = "created_date")
    private LocalDate createdDate;

    // --- Getters & Setters ---

    public Integer getFormId() { return formId; }
    public void setFormId(Integer formId) { this.formId = formId; }

    public String getFormName() { return formName; }
    public void setFormName(String formName) { this.formName = formName; }

    public Integer getModuleId() { return moduleId; }
    public void setModuleId(Integer moduleId) { this.moduleId = moduleId; }

    public String getLayoutType() { return layoutType; }
    public void setLayoutType(String layoutType) { this.layoutType = layoutType; }

    public LocalDate getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDate createdDate) { this.createdDate = createdDate; }
}
