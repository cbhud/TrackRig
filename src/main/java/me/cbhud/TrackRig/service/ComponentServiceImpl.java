package me.cbhud.TrackRig.service;

import me.cbhud.TrackRig.dto.ComponentRequest;
import me.cbhud.TrackRig.dto.ComponentResponse;
import me.cbhud.TrackRig.exception.ResourceNotFoundException;
import me.cbhud.TrackRig.model.Component;
import me.cbhud.TrackRig.model.ComponentCategory;
import me.cbhud.TrackRig.model.ComponentStatus;
import me.cbhud.TrackRig.model.Workstation;
import me.cbhud.TrackRig.repository.ComponentCategoryRepository;
import me.cbhud.TrackRig.repository.ComponentRepository;
import me.cbhud.TrackRig.repository.ComponentStatusRepository;
import me.cbhud.TrackRig.repository.WorkstationRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ComponentServiceImpl implements ComponentService {

    private final ComponentRepository componentRepository;
    private final ComponentCategoryRepository categoryRepository;
    private final ComponentStatusRepository statusRepository;
    private final WorkstationRepository workstationRepository;

    public ComponentServiceImpl(
            ComponentRepository componentRepository,
            ComponentCategoryRepository categoryRepository,
            ComponentStatusRepository statusRepository,
            WorkstationRepository workstationRepository) {
        this.componentRepository = componentRepository;
        this.categoryRepository = categoryRepository;
        this.statusRepository = statusRepository;
        this.workstationRepository = workstationRepository;
    }

    // GET ALL: Fetch all components and convert to DTOs.
    // The ComponentResponse.from() factory method flattens all FK relationships
    // (category, status, workstation) into simple id+name pairs.
    @Override
    public List<ComponentResponse> getAllComponents() {
        return componentRepository.findAll()
                .stream()
                .map(ComponentResponse::from)
                .toList();
    }

    // GET BY ID: Find or throw 404.
    @Override
    public ComponentResponse getComponentById(Integer id) {
        Component component = findComponentOrThrow(id);
        return ComponentResponse.from(component);
    }

    // CREATE: Only MANAGER or OWNER can add new components.
    // MANAGER can add components to help manage inventory,
    // OWNER has full access including deletion.
    //
    // Steps: validate all FK IDs → build entity → save → return DTO
    @Override
    @Transactional
    @PreAuthorize("hasRole('MANAGER') or hasRole('OWNER')")
    public ComponentResponse createComponent(ComponentRequest request) {
        ComponentCategory category = findCategoryOrThrow(request.getCategoryId());
        ComponentStatus status = findStatusOrThrow(request.getStatusId());

        Component component = new Component();
        component.setSerialNumber(request.getSerialNumber());
        component.setName(request.getName());
        component.setComponentCategory(category);
        component.setComponentStatus(status);
        component.setPurchaseDate(request.getPurchaseDate());
        component.setWarrantyDate(request.getWarrantyExpiry());
        component.setNotes(request.getNotes());

        // workstationId is optional — NULL means "in storage"
        if (request.getWorkstationId() != null) {
            Workstation workstation = findWorkstationOrThrow(request.getWorkstationId());
            component.setWorkstation(workstation);
        }

        Component saved = componentRepository.save(component);
        return ComponentResponse.from(saved);
    }

    // UPDATE: Update all editable fields.
    // Any authenticated user can update (e.g., change status from "Working" to
    // "Damaged").
    // We re-validate all FK IDs in case the client sends invalid references.
    @Override
    @Transactional
    public ComponentResponse updateComponent(Integer id, ComponentRequest request) {
        Component component = findComponentOrThrow(id);
        ComponentCategory category = findCategoryOrThrow(request.getCategoryId());
        ComponentStatus status = findStatusOrThrow(request.getStatusId());

        component.setSerialNumber(request.getSerialNumber());
        component.setName(request.getName());
        component.setComponentCategory(category);
        component.setComponentStatus(status);
        component.setPurchaseDate(request.getPurchaseDate());
        component.setWarrantyDate(request.getWarrantyExpiry());
        component.setNotes(request.getNotes());

        // Update workstation assignment — NULL means move to storage
        if (request.getWorkstationId() != null) {
            Workstation workstation = findWorkstationOrThrow(request.getWorkstationId());
            component.setWorkstation(workstation);
        } else {
            component.setWorkstation(null);
        }

        Component saved = componentRepository.save(component);
        return ComponentResponse.from(saved);
    }

    // DELETE: Only OWNER can remove components.
    // This is more restrictive than create (MANAGER + OWNER) because
    // deleting inventory is a higher-risk action.
    //
    // Note: The SQL trigger 'trg_restrict_component_delete' will BLOCK
    // deletion if the component is currently assigned to a workstation.
    // The GlobalExceptionHandler catches this and returns a 409 Conflict.
    @Override
    @Transactional
    @PreAuthorize("hasRole('OWNER')")
    public void deleteComponent(Integer id) {
        Component component = findComponentOrThrow(id);
        componentRepository.delete(component);
    }

    // ========================
    // BUSINESS LOGIC METHODS
    // ========================

    // GET STORAGE: Returns all components not assigned to any workstation.
    // Maps to: WHERE workstation_id IS NULL
    // Used by the "Storage / Stock" view in the frontend.
    @Override
    public List<ComponentResponse> getComponentsInStorage() {
        return componentRepository.findByWorkstationIsNull()
                .stream()
                .map(ComponentResponse::from)
                .toList();
    }

    // ASSIGN TO WORKSTATION: Moves a component from storage (or another PC) to a
    // workstation.
    // This is a partial update — only changes the workstation FK.
    //
    // Use case: Technician installs a spare GPU from storage into Station-A1.
    @Override
    @Transactional
    public ComponentResponse assignToWorkstation(Integer componentId, Integer workstationId) {
        Component component = findComponentOrThrow(componentId);
        Workstation workstation = findWorkstationOrThrow(workstationId);

        component.setWorkstation(workstation);

        Component saved = componentRepository.save(component);
        return ComponentResponse.from(saved);
    }

    // MOVE TO STORAGE: Unassigns a component from its workstation.
    // Sets workstation FK to NULL, which means "in storage".
    @Override
    @Transactional
    public ComponentResponse moveToStorage(Integer componentId) {
        Component component = findComponentOrThrow(componentId);

        component.setWorkstation(null);

        Component saved = componentRepository.save(component);
        return ComponentResponse.from(saved);
    }

    private Component findComponentOrThrow(Integer id) {
        return componentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Component not found with id: " + id));
    }

    private ComponentCategory findCategoryOrThrow(Integer id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Component category not found with id: " + id));
    }

    private ComponentStatus findStatusOrThrow(Integer id) {
        return statusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Component status not found with id: " + id));
    }

    private Workstation findWorkstationOrThrow(Integer id) {
        return workstationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Workstation not found with id: " + id));
    }
}
