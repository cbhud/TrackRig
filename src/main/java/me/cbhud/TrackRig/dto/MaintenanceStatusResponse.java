package me.cbhud.TrackRig.dto;

import lombok.Data;
import me.cbhud.TrackRig.repository.MaintenanceStatusProjection;

import java.time.LocalDateTime;

/**
 * Mirrors the PostgreSQL view 'view_maintenance_status'.
 *
 * The view computes for every workstation + active maintenance type combo:
 * - When maintenance was last performed
 * - When it's next due (last_performed + interval_days)
 * - Status: OK, DUE_SOON (within 3 days), OVERDUE, or NEVER_DONE
 */
@Data
public class MaintenanceStatusResponse {

    private Integer workstationId;
    private String workstationName;

    private Integer maintenanceTypeId;
    private String maintenanceName;
    private int intervalDays;

    private LocalDateTime lastPerformed;
    private LocalDateTime nextDueDate;

    // One of: "OK", "DUE_SOON", "OVERDUE", "NEVER_DONE"
    // Computed by the PostgreSQL view's CASE expression
    private String status;

    // Factory method to convert the view projection → DTO.
    // The projection is what Spring Data returns from native queries
    // on view_maintenance_status.
    public static MaintenanceStatusResponse from(MaintenanceStatusProjection projection) {
        MaintenanceStatusResponse response = new MaintenanceStatusResponse();
        response.setWorkstationId(projection.getWorkstationId());
        response.setWorkstationName(projection.getWorkstationName());
        response.setMaintenanceTypeId(projection.getMaintenanceTypeId());
        response.setMaintenanceName(projection.getMaintenanceName());
        response.setIntervalDays(projection.getIntervalDays());
        response.setLastPerformed(projection.getLastPerformed());
        response.setNextDueDate(projection.getNextDueDate());
        response.setStatus(projection.getStatus());
        return response;
    }
}
