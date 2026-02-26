package me.cbhud.TrackRig.service;

import me.cbhud.TrackRig.exception.ResourceNotFoundException;
import me.cbhud.TrackRig.model.ComponentCategory;
import me.cbhud.TrackRig.model.ComponentStatus;
import me.cbhud.TrackRig.model.MaintenanceType;
import me.cbhud.TrackRig.model.WorkstationStatus;
import me.cbhud.TrackRig.repository.ComponentCategoryRepository;
import me.cbhud.TrackRig.repository.ComponentStatusRepository;
import me.cbhud.TrackRig.repository.MaintenanceTypeRepository;
import me.cbhud.TrackRig.repository.WorkstationStatusRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class LookupServiceImpl implements LookupService {

    private final ComponentCategoryRepository categoryRepository;
    private final ComponentStatusRepository statusRepository;
    private final WorkstationStatusRepository workstationStatusRepository;
    private final MaintenanceTypeRepository maintenanceTypeRepository;

    public LookupServiceImpl(
            ComponentCategoryRepository categoryRepository,
            ComponentStatusRepository statusRepository,
            WorkstationStatusRepository workstationStatusRepository,
            MaintenanceTypeRepository maintenanceTypeRepository) {
        this.categoryRepository = categoryRepository;
        this.statusRepository = statusRepository;
        this.workstationStatusRepository = workstationStatusRepository;
        this.maintenanceTypeRepository = maintenanceTypeRepository;
    }

    // ========================
    // COMPONENT CATEGORIES
    // ========================

    // Read: all authenticated users (for category dropdown)
    @Override
    public List<ComponentCategory> getAllComponentCategories() {
        return categoryRepository.findAll();
    }

    // Create: OWNER only — adding a new category changes the system's
    // classification
    @Override
    @Transactional
    @PreAuthorize("hasRole('OWNER')")
    public ComponentCategory createComponentCategory(ComponentCategory category) {
        return categoryRepository.save(category);
    }

    // Update: OWNER only — renaming a category affects all components in that
    // category
    @Override
    @Transactional
    @PreAuthorize("hasRole('OWNER')")
    public ComponentCategory updateComponentCategory(Integer id, ComponentCategory category) {
        ComponentCategory existing = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Component category not found with id: " + id));
        existing.setName(category.getName());
        existing.setDescription(category.getDescription());
        return categoryRepository.save(existing);
    }

    // Delete: OWNER only — will fail if components still reference this category
    // (FK constraint)
    @Override
    @Transactional
    @PreAuthorize("hasRole('OWNER')")
    public void deleteComponentCategory(Integer id) {
        ComponentCategory existing = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Component category not found with id: " + id));
        categoryRepository.delete(existing);
    }

    // ========================
    // COMPONENT STATUSES
    // ========================

    @Override
    public List<ComponentStatus> getAllComponentStatuses() {
        return statusRepository.findAll();
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('OWNER')")
    public ComponentStatus createComponentStatus(ComponentStatus status) {
        return statusRepository.save(status);
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('OWNER')")
    public ComponentStatus updateComponentStatus(Integer id, ComponentStatus status) {
        ComponentStatus existing = statusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Component status not found with id: " + id));
        existing.setName(status.getName());
        return statusRepository.save(existing);
    }

    // Delete: will fail if components still reference this status (FK constraint)
    @Override
    @Transactional
    @PreAuthorize("hasRole('OWNER')")
    public void deleteComponentStatus(Integer id) {
        ComponentStatus existing = statusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Component status not found with id: " + id));
        statusRepository.delete(existing);
    }

    // ========================
    // WORKSTATION STATUSES
    // ========================

    @Override
    public List<WorkstationStatus> getAllWorkstationStatuses() {
        return workstationStatusRepository.findAll();
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('OWNER')")
    public WorkstationStatus createWorkstationStatus(WorkstationStatus status) {
        return workstationStatusRepository.save(status);
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('OWNER')")
    public WorkstationStatus updateWorkstationStatus(Integer id, WorkstationStatus status) {
        WorkstationStatus existing = workstationStatusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Workstation status not found with id: " + id));
        existing.setName(status.getName());
        return workstationStatusRepository.save(existing);
    }

    // Delete: will fail if workstations still reference this status (FK constraint)
    @Override
    @Transactional
    @PreAuthorize("hasRole('OWNER')")
    public void deleteWorkstationStatus(Integer id) {
        WorkstationStatus existing = workstationStatusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Workstation status not found with id: " + id));
        workstationStatusRepository.delete(existing);
    }

    // ========================
    // MAINTENANCE TYPES
    // ========================

    @Override
    public List<MaintenanceType> getAllMaintenanceTypes() {
        return maintenanceTypeRepository.findAll();
    }

    // Active only — used for maintenance logging dropdown and status computation
    @Override
    public List<MaintenanceType> getActiveMaintenanceTypes() {
        return maintenanceTypeRepository.findByIsActiveTrue();
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('OWNER')")
    public MaintenanceType createMaintenanceType(MaintenanceType type) {
        return maintenanceTypeRepository.save(type);
    }

    // Update: OWNER only — can change name, description, interval, or is_active
    // flag.
    // Tip: Instead of deleting a maintenance type, set is_active = false.
    // This preserves history (existing logs still reference it) while
    // hiding it from the active maintenance schedule.
    @Override
    @Transactional
    @PreAuthorize("hasRole('OWNER')")
    public MaintenanceType updateMaintenanceType(Integer id, MaintenanceType type) {
        MaintenanceType existing = maintenanceTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Maintenance type not found with id: " + id));
        existing.setName(type.getName());
        existing.setDescription(type.getDescription());
        existing.setIntervalDays(type.getIntervalDays());
        existing.setIsActive(type.getIsActive());
        return maintenanceTypeRepository.save(existing);
    }

    // Delete: will fail if maintenance_logs still reference this type (FK
    // constraint).
    // Prefer deactivating (is_active = false) over deleting to preserve history.
    @Override
    @Transactional
    @PreAuthorize("hasRole('OWNER')")
    public void deleteMaintenanceType(Integer id) {
        MaintenanceType existing = maintenanceTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Maintenance type not found with id: " + id));
        maintenanceTypeRepository.delete(existing);
    }
}
