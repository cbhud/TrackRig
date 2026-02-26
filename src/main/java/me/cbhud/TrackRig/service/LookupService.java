package me.cbhud.TrackRig.service;

import me.cbhud.TrackRig.model.ComponentCategory;
import me.cbhud.TrackRig.model.ComponentStatus;
import me.cbhud.TrackRig.model.MaintenanceType;
import me.cbhud.TrackRig.model.WorkstationStatus;

import java.util.List;

/**
 * Provides access to lookup/reference tables.
 * Read methods are available to all authenticated users (for
 * dropdowns/filters).
 * Create/Update/Delete are restricted to OWNER only (system configuration).
 */
public interface LookupService {

    // --- Component Categories (GPU, CPU, RAM, etc.) ---
    List<ComponentCategory> getAllComponentCategories();

    ComponentCategory createComponentCategory(ComponentCategory category);

    ComponentCategory updateComponentCategory(Integer id, ComponentCategory category);

    void deleteComponentCategory(Integer id);

    // --- Component Statuses (Working, Damaged, RMA Pending) ---
    List<ComponentStatus> getAllComponentStatuses();

    ComponentStatus createComponentStatus(ComponentStatus status);

    ComponentStatus updateComponentStatus(Integer id, ComponentStatus status);

    void deleteComponentStatus(Integer id);

    // --- Workstation Statuses (Operational, Under Maintenance, Out of Order) ---
    List<WorkstationStatus> getAllWorkstationStatuses();

    WorkstationStatus createWorkstationStatus(WorkstationStatus status);

    WorkstationStatus updateWorkstationStatus(Integer id, WorkstationStatus status);

    void deleteWorkstationStatus(Integer id);

    // --- Maintenance Types (Dust Cleaning, Thermal Repaste, etc.) ---
    List<MaintenanceType> getAllMaintenanceTypes();

    List<MaintenanceType> getActiveMaintenanceTypes();

    MaintenanceType createMaintenanceType(MaintenanceType type);

    MaintenanceType updateMaintenanceType(Integer id, MaintenanceType type);

    void deleteMaintenanceType(Integer id);
}
