package me.cbhud.TrackRig.service;

import me.cbhud.TrackRig.dto.MaintenanceLogRequest;
import me.cbhud.TrackRig.dto.MaintenanceLogResponse;
import me.cbhud.TrackRig.dto.MaintenanceStatusResponse;

import java.util.List;

public interface MaintenanceService {

    // Log a new maintenance action — performed_by is taken from the authenticated
    // user
    MaintenanceLogResponse logMaintenance(MaintenanceLogRequest request);

    // Get all maintenance logs for a specific workstation (history view)
    List<MaintenanceLogResponse> getLogsByWorkstationId(Integer workstationId);

    // Get all workstation/type combos that are OVERDUE or DUE_SOON across ALL
    // workstations
    // Used for the dashboard alerts / notification badge
    List<MaintenanceStatusResponse> getAllOverdue();
}
