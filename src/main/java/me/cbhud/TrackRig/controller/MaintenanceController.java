package me.cbhud.TrackRig.controller;

import jakarta.validation.Valid;
import me.cbhud.TrackRig.dto.MaintenanceLogRequest;
import me.cbhud.TrackRig.dto.MaintenanceLogResponse;
import me.cbhud.TrackRig.dto.MaintenanceStatusResponse;
import me.cbhud.TrackRig.service.MaintenanceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/maintenance")
public class MaintenanceController {

    private final MaintenanceService maintenanceService;

    public MaintenanceController(MaintenanceService maintenanceService) {
        this.maintenanceService = maintenanceService;
    }

    // POST /api/maintenance/log — log a new maintenance action
    // performed_by is auto-set from the JWT authenticated user
    // performed_at is auto-set to current timestamp
    @PostMapping("/log")
    public ResponseEntity<MaintenanceLogResponse> logMaintenance(
            @RequestBody @Valid MaintenanceLogRequest request) {
        MaintenanceLogResponse created = maintenanceService.logMaintenance(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // GET /api/maintenance/logs/workstation/{workstationId} — maintenance history
    // for a workstation
    @GetMapping("/logs/workstation/{workstationId}")
    public ResponseEntity<List<MaintenanceLogResponse>> getLogsByWorkstation(
            @PathVariable Integer workstationId) {
        return ResponseEntity.ok(maintenanceService.getLogsByWorkstationId(workstationId));
    }

    // GET /api/maintenance/overdue — all OVERDUE and DUE_SOON items across all
    // workstations
    // Uses the PostgreSQL view_maintenance_status directly
    @GetMapping("/overdue")
    public ResponseEntity<List<MaintenanceStatusResponse>> getAllOverdue() {
        return ResponseEntity.ok(maintenanceService.getAllOverdue());
    }
}
