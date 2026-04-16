package com.hrms.db.entities;

import jakarta.persistence.*;

/**
 * Custom modules — toggleable system features.
 * Required by: Customization subsystem.
 */
@Entity
@Table(name = "custom_modules")
public class CustomModule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "module_id")
    private Integer moduleId;

    @Column(name = "module_name", nullable = false, length = 100)
    private String moduleName;

    @Column(name = "module_type", length = 50)
    private String moduleType;

    @Column(name = "is_enabled")
    private Boolean isEnabled = true;

    // --- Getters & Setters ---

    public Integer getModuleId() { return moduleId; }
    public void setModuleId(Integer moduleId) { this.moduleId = moduleId; }

    public String getModuleName() { return moduleName; }
    public void setModuleName(String moduleName) { this.moduleName = moduleName; }

    public String getModuleType() { return moduleType; }
    public void setModuleType(String moduleType) { this.moduleType = moduleType; }

    public Boolean getIsEnabled() { return isEnabled; }
    public void setIsEnabled(Boolean isEnabled) { this.isEnabled = isEnabled; }
}
