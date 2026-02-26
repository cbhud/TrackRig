package me.cbhud.TrackRig.dto;

import lombok.Data;
import me.cbhud.TrackRig.model.MaintenanceLog;

import java.time.LocalDateTime;

@Data
public class MaintenanceLogResponse {

    private Integer id;

    private Integer workstationId;
    private String workstationName;

    private Integer maintenanceTypeId;
    private String maintenanceTypeName;

    private Integer performedByUserId;
    private String performedByName;

    private LocalDateTime performedAt;
    private String notes;

    // Factory method to convert entity → DTO
    public static MaintenanceLogResponse from(MaintenanceLog log) {
        MaintenanceLogResponse response = new MaintenanceLogResponse();
        response.setId(log.getId());

        if (log.getWorkstation() != null) {
            response.setWorkstationId(log.getWorkstation().getId());
            response.setWorkstationName(log.getWorkstation().getName());
        }

        if (log.getMaintenanceType() != null) {
            response.setMaintenanceTypeId(log.getMaintenanceType().getId());
            response.setMaintenanceTypeName(log.getMaintenanceType().getName());
        }

        if (log.getPerformedBy() != null) {
            response.setPerformedByUserId(log.getPerformedBy().getId());
            response.setPerformedByName(log.getPerformedBy().getFullName());
        }

        response.setPerformedAt(log.getPerformedAt());
        response.setNotes(log.getNotes());
        return response;
    }
}
