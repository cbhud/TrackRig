package me.cbhud.TrackRig.controller;

import jakarta.validation.Valid;
import me.cbhud.TrackRig.dto.ComponentResponse;
import me.cbhud.TrackRig.dto.MaintenanceStatusResponse;
import me.cbhud.TrackRig.dto.WorkstationRequest;
import me.cbhud.TrackRig.dto.WorkstationResponse;
import me.cbhud.TrackRig.service.WorkstationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/workstations")
public class WorkstationController {

    private final WorkstationService workstationService;

    public WorkstationController(WorkstationService workstationService) {
        this.workstationService = workstationService;
    }

    // GET /api/workstations
    // Returns all workstations. Optionally filter by statusId query param.
    // Examples:
    // GET /api/workstations → all workstations
    // GET /api/workstations?statusId=3 → only "Out of Order" workstations
    @GetMapping
    public ResponseEntity<List<WorkstationResponse>> getAllWorkstations(
            @RequestParam(required = false) Integer statusId) {
        List<WorkstationResponse> workstations;

        if (statusId != null) {
            workstations = workstationService.getWorkstationsByStatusId(statusId);
        } else {
            workstations = workstationService.getAllWorkstations();
        }

        return ResponseEntity.ok(workstations);
    }

    // GET /api/workstations/{id}
    @GetMapping("/{id}")
    public ResponseEntity<WorkstationResponse> getWorkstationById(@PathVariable Integer id) {
        return ResponseEntity.ok(workstationService.getWorkstationById(id));
    }

    // POST /api/workstations
    // Request body: { "name": "Station-D1", "statusId": 1, "gridX": 0, "gridY": 3 }
    @PostMapping
    public ResponseEntity<WorkstationResponse> createWorkstation(
            @RequestBody @Valid WorkstationRequest request) {
        WorkstationResponse created = workstationService.createWorkstation(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // PUT /api/workstations/{id}
    // Full update — all fields must be provided
    @PutMapping("/{id}")
    public ResponseEntity<WorkstationResponse> updateWorkstation(
            @PathVariable Integer id,
            @RequestBody @Valid WorkstationRequest request) {
        return ResponseEntity.ok(workstationService.updateWorkstation(id, request));
    }

    // DELETE /api/workstations/{id}
    // Restricted to MANAGER or OWNER (enforced in service via @PreAuthorize)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWorkstation(@PathVariable Integer id) {
        workstationService.deleteWorkstation(id);
        return ResponseEntity.noContent().build();
    }

    // ========================
    // BUSINESS LOGIC ENDPOINTS
    // ========================

    // PATCH /api/workstations/{id}/status
    // Partial update — only changes the status.
    // Request body: { "statusId": 2 }
    //
    // Why PATCH instead of PUT?
    // PUT = full resource replacement (all fields required)
    // PATCH = partial update (only the fields being changed)
    @PatchMapping("/{id}/status")
    public ResponseEntity<WorkstationResponse> updateStatus(
            @PathVariable Integer id,
            @RequestBody Map<String, Integer> body) {
        Integer statusId = body.get("statusId");
        return ResponseEntity.ok(workstationService.updateStatus(id, statusId));
    }

    // PATCH /api/workstations/{id}/position
    // Partial update — only changes grid position (for floor map drag-and-drop).
    // Request body: { "gridX": 2, "gridY": 1 }
    @PatchMapping("/{id}/position")
    public ResponseEntity<WorkstationResponse> updateGridPosition(
            @PathVariable Integer id,
            @RequestBody Map<String, Integer> body) {
        int gridX = body.get("gridX");
        int gridY = body.get("gridY");
        return ResponseEntity.ok(workstationService.updateGridPosition(id, gridX, gridY));
    }

    // GET /api/workstations/{id}/components
    // Returns all components installed in this workstation.
    @GetMapping("/{id}/components")
    public ResponseEntity<List<ComponentResponse>> getComponents(@PathVariable Integer id) {
        return ResponseEntity.ok(workstationService.getComponentsByWorkstationId(id));
    }

    // GET /api/workstations/{id}/maintenance-status
    // Returns computed maintenance status for each active maintenance type.
    // Response includes: status (OK/DUE_SOON/OVERDUE/NEVER_DONE), nextDueDate,
    // lastPerformed
    @GetMapping("/{id}/maintenance-status")
    public ResponseEntity<List<MaintenanceStatusResponse>> getMaintenanceStatus(
            @PathVariable Integer id) {
        return ResponseEntity.ok(workstationService.getMaintenanceStatus(id));
    }
}
