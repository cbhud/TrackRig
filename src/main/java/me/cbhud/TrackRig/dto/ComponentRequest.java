package me.cbhud.TrackRig.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ComponentRequest {

    @NotBlank(message = "Serial number is required")
    @Size(max = 100, message = "Serial number must be at most 100 characters")
    private String serialNumber;

    @NotBlank(message = "Component name is required")
    @Size(max = 200, message = "Name must be at most 200 characters")
    private String name;

    @NotNull(message = "Category ID is required")
    private Integer categoryId;

    @NotNull(message = "Status ID is required")
    private Integer statusId;

    private Integer workstationId;

    private LocalDate purchaseDate;

    private LocalDate warrantyExpiry;

    private String notes;
}
