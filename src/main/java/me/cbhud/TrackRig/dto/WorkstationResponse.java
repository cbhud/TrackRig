package me.cbhud.TrackRig.dto;

import lombok.Data;
import me.cbhud.TrackRig.model.Workstation;

import java.time.LocalDateTime;

@Data
public class WorkstationResponse {

    private Integer id;
    private String name;

    // Flattened from the WorkstationStatus entity —
    // avoids exposing nested entity objects in the API response.
    private Integer statusId;
    private String statusName;

    private int gridX;
    private int gridY;
    private LocalDateTime createdAt;

    // Factory method to convert entity → DTO (same pattern as UserResponse)
    public static WorkstationResponse from(Workstation workstation) {
        WorkstationResponse response = new WorkstationResponse();
        response.setId(workstation.getId());
        response.setName(workstation.getName());

        if (workstation.getWorkstationStatus() != null) {
            response.setStatusId(workstation.getWorkstationStatus().getId());
            response.setStatusName(workstation.getWorkstationStatus().getName());
        }

        response.setGridX(workstation.getGridX());
        response.setGridY(workstation.getGridY());
        response.setCreatedAt(workstation.getCreatedAt());
        return response;
    }
}
