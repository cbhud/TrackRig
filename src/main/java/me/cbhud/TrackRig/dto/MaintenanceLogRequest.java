package me.cbhud.TrackRig.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MaintenanceLogRequest {

    // Maps to: workstation_id INT NOT NULL REFERENCES workstation(id)
    @NotNull(message = "Workstation ID is required")
    private Integer workstationId;

    // Maps to: maintenance_type_id INT NOT NULL REFERENCES maintenance_type(id)
    @NotNull(message = "Maintenance type ID is required")
    private Integer maintenanceTypeId;

    // Maps to: notes TEXT — optional description of what was done
    private String notes;

    // performed_by_user_id is NOT in the request —
    // it's automatically set from the authenticated user (SecurityContext).
    // performed_at is also auto-set to the current timestamp.
}
