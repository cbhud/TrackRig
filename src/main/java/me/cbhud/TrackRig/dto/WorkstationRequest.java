package me.cbhud.TrackRig.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class WorkstationRequest {

    // Maps to: name VARCHAR(100) NOT NULL UNIQUE
    @NotBlank(message = "Workstation name is required")
    @Size(max = 100, message = "Name must be at most 100 characters")
    private String name;

    // Maps to: status_id INT REFERENCES workstation_status(id)
    @NotNull(message = "Status ID is required")
    private Integer statusId;

    // Maps to: grid_x INT DEFAULT 0, grid_y INT DEFAULT 0
    private int gridX;
    private int gridY;
}
