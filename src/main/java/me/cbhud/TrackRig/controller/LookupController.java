package me.cbhud.TrackRig.controller;

import me.cbhud.TrackRig.model.ComponentCategory;
import me.cbhud.TrackRig.model.ComponentStatus;
import me.cbhud.TrackRig.model.MaintenanceType;
import me.cbhud.TrackRig.model.WorkstationStatus;
import me.cbhud.TrackRig.service.LookupService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lookup")
public class LookupController {

    private final LookupService lookupService;

    public LookupController(LookupService lookupService) {
        this.lookupService = lookupService;
    }

    @GetMapping("/component-categories")
    public ResponseEntity<List<ComponentCategory>> getAllComponentCategories() {
        return ResponseEntity.ok(lookupService.getAllComponentCategories());
    }

    // POST — OWNER only
    @PostMapping("/component-categories")
    public ResponseEntity<ComponentCategory> createComponentCategory(
            @RequestBody @Valid ComponentCategory category) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(lookupService.createComponentCategory(category));
    }

    @PutMapping("/component-categories/{id}")
    public ResponseEntity<ComponentCategory> updateComponentCategory(
            @PathVariable Integer id,
            @RequestBody @Valid ComponentCategory category) {
        return ResponseEntity.ok(lookupService.updateComponentCategory(id, category));
    }

    @DeleteMapping("/component-categories/{id}")
    public ResponseEntity<Void> deleteComponentCategory(@PathVariable Integer id) {
        lookupService.deleteComponentCategory(id);
        return ResponseEntity.noContent().build();
    }

    // ========================
    // COMPONENT STATUSES
    // ========================

    @GetMapping("/component-statuses")
    public ResponseEntity<List<ComponentStatus>> getAllComponentStatuses() {
        return ResponseEntity.ok(lookupService.getAllComponentStatuses());
    }

    @PostMapping("/component-statuses")
    public ResponseEntity<ComponentStatus> createComponentStatus(
            @RequestBody @Valid ComponentStatus status) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(lookupService.createComponentStatus(status));
    }

    @PutMapping("/component-statuses/{id}")
    public ResponseEntity<ComponentStatus> updateComponentStatus(
            @PathVariable Integer id,
            @RequestBody @Valid ComponentStatus status) {
        return ResponseEntity.ok(lookupService.updateComponentStatus(id, status));
    }

    @DeleteMapping("/component-statuses/{id}")
    public ResponseEntity<Void> deleteComponentStatus(@PathVariable Integer id) {
        lookupService.deleteComponentStatus(id);
        return ResponseEntity.noContent().build();
    }

    // ========================
    // WORKSTATION STATUSES
    // ========================

    @GetMapping("/workstation-statuses")
    public ResponseEntity<List<WorkstationStatus>> getAllWorkstationStatuses() {
        return ResponseEntity.ok(lookupService.getAllWorkstationStatuses());
    }

    @PostMapping("/workstation-statuses")
    public ResponseEntity<WorkstationStatus> createWorkstationStatus(
            @RequestBody @Valid WorkstationStatus status) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(lookupService.createWorkstationStatus(status));
    }

    @PutMapping("/workstation-statuses/{id}")
    public ResponseEntity<WorkstationStatus> updateWorkstationStatus(
            @PathVariable Integer id,
            @RequestBody @Valid WorkstationStatus status) {
        return ResponseEntity.ok(lookupService.updateWorkstationStatus(id, status));
    }

    @DeleteMapping("/workstation-statuses/{id}")
    public ResponseEntity<Void> deleteWorkstationStatus(@PathVariable Integer id) {
        lookupService.deleteWorkstationStatus(id);
        return ResponseEntity.noContent().build();
    }

    // ========================
    // MAINTENANCE TYPES
    // ========================

    @GetMapping("/maintenance-types")
    public ResponseEntity<List<MaintenanceType>> getAllMaintenanceTypes() {
        return ResponseEntity.ok(lookupService.getAllMaintenanceTypes());
    }

    @GetMapping("/maintenance-types/active")
    public ResponseEntity<List<MaintenanceType>> getActiveMaintenanceTypes() {
        return ResponseEntity.ok(lookupService.getActiveMaintenanceTypes());
    }

    @PostMapping("/maintenance-types")
    public ResponseEntity<MaintenanceType> createMaintenanceType(
            @RequestBody @Valid MaintenanceType type) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(lookupService.createMaintenanceType(type));
    }

    @PutMapping("/maintenance-types/{id}")
    public ResponseEntity<MaintenanceType> updateMaintenanceType(
            @PathVariable Integer id,
            @RequestBody @Valid MaintenanceType type) {
        return ResponseEntity.ok(lookupService.updateMaintenanceType(id, type));
    }

    @DeleteMapping("/maintenance-types/{id}")
    public ResponseEntity<Void> deleteMaintenanceType(@PathVariable Integer id) {
        lookupService.deleteMaintenanceType(id);
        return ResponseEntity.noContent().build();
    }
}
