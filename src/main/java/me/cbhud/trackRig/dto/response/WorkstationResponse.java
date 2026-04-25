package me.cbhud.trackRig.dto.response;

import me.cbhud.trackRig.model.Workstation;

import java.time.OffsetDateTime;

public record WorkstationResponse(
        Integer id,
        String name,
        WorkstationStatusResponse status,
        Integer gridX,
        Integer gridY,
        Integer floor,
        OffsetDateTime createdAt
)
{
    public static WorkstationResponse from(Workstation workstation) {

        return new WorkstationResponse(
                workstation.getId(),
                workstation.getName(),
                workstation.getStatus() != null ? WorkstationStatusResponse.from(workstation.getStatus()) : null,
                workstation.getGridX(),
                workstation.getGridY(),
                workstation.getFloor(),
                workstation.getCreatedAt()
                );
    }
}
