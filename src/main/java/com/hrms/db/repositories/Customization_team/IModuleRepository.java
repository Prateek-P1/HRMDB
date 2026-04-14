package com.hrms.db.repositories.Customization_team;
import java.util.List;

/**
 * INTERFACE: IModuleRepository
 * Subsystem: Customization
 * Component: Module Customizer
 *
 * Provides DB access for enabling/disabling core HR modules
 * and storing module configuration settings.
 *
 * DB Team: Please implement this interface and provide the concrete class.
 * Entity needed: Module { moduleId: int, moduleName: String, moduleType: String, isEnabled: boolean, config: String }
 */
public interface IModuleRepository {

    /**
     * READ: Fetch a single module record by its ID.
     */
    Module getModuleById(int moduleId);

    /**
     * READ: Return all available HR modules.
     */
    List<Module> getAllModules();

    /**
     * WRITE: Enable or disable a module by its ID.
     */
    void updateModuleStatus(int moduleId, boolean enabled);

    /**
     * WRITE: Save module-specific configuration string.
     */
    void updateModuleConfig(String moduleName, String config);

    /**
     * READ: Check if a module is currently active.
     */
    boolean getModuleStatus(String moduleName);
}

/**
 * DTO: Module
 * Maps to the Module entity in the database.
 */
class Module {
    public int moduleId;
    public String moduleName;
    public String moduleType;
    public boolean isEnabled;
    public String config;
}
