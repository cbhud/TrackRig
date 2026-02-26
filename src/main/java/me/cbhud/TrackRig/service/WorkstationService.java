package me.cbhud.TrackRig.service;

import me.cbhud.TrackRig.dto.*;

import java.util.List;

public interface WorkstationService {

    // --- Standard CRUD ---
    List<WorkstationResponse> getAllWorkstations();

    WorkstationResponse getWorkstationById(Integer id);

    WorkstationResponse createWorkstation(WorkstationRequest request);

    WorkstationResponse updateWorkstation(Integer id, WorkstationRequest request);

    void deleteWorkstation(Integer id);

    // --- Business Logic ---

    // Change workstation status (e.g., Operational → Under Maintenance)
    WorkstationResponse updateStatus(Integer workstationId, Integer statusId);

    // Update workstation position on the floor map grid
    WorkstationResponse updateGridPosition(Integer workstationId, int gridX, int gridY);

    // Get all components installed in a workstation
    List<ComponentResponse> getComponentsByWorkstationId(Integer workstationId);

    // Get computed maintenance status (mirrors SQL view_maintenance_status)
    List<MaintenanceStatusResponse> getMaintenanceStatus(Integer workstationId);

    // Filter workstations by status (e.g., "show all Out of Order")
    List<WorkstationResponse> getWorkstationsByStatusId(Integer statusId);
}