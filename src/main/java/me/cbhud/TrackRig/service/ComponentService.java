package me.cbhud.TrackRig.service;

import me.cbhud.TrackRig.dto.ComponentRequest;
import me.cbhud.TrackRig.dto.ComponentResponse;

import java.util.List;

public interface ComponentService {

    List<ComponentResponse> getAllComponents();

    ComponentResponse getComponentById(Integer id);

    // Create: restricted to MANAGER or OWNER
    ComponentResponse createComponent(ComponentRequest request);

    ComponentResponse updateComponent(Integer id, ComponentRequest request);

    // Delete: restricted to OWNER only
    void deleteComponent(Integer id);

    // --- Business Logic ---

    // Get all components not assigned to any workstation (in storage/stock)
    List<ComponentResponse> getComponentsInStorage();

    // Assign a component to a workstation (move from storage or reassign)
    ComponentResponse assignToWorkstation(Integer componentId, Integer workstationId);

    // Unassign a component from its workstation (move back to storage)
    ComponentResponse moveToStorage(Integer componentId);
}
