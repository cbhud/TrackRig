package me.cbhud.TrackRig.dto;

import lombok.Data;
import me.cbhud.TrackRig.model.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ComponentResponse {

    private Integer id;
    private String serialNumber;
    private String name;

    private Integer categoryId;
    private String categoryName;

    private Integer statusId;
    private String statusName;

    private Integer workstationId;
    private String workstationName;

    private LocalDate purchaseDate;
    private LocalDate warrantyExpiry;
    private String notes;
    private LocalDateTime createdAt;

    public static ComponentResponse from(Component component) {
        ComponentResponse response = new ComponentResponse();
        response.setId(component.getId());
        response.setSerialNumber(component.getSerialNumber());
        response.setName(component.getName());

        if (component.getComponentCategory() != null) {
            response.setCategoryId(component.getComponentCategory().getId());
            response.setCategoryName(component.getComponentCategory().getName());
        }

        if (component.getComponentStatus() != null) {
            response.setStatusId(component.getComponentStatus().getId());
            response.setStatusName(component.getComponentStatus().getName());
        }

        // Null workstation = component is in storage
        if (component.getWorkstation() != null) {
            response.setWorkstationId(component.getWorkstation().getId());
            response.setWorkstationName(component.getWorkstation().getName());
        }

        response.setPurchaseDate(component.getPurchaseDate());
        response.setWarrantyExpiry(component.getWarrantyDate());
        response.setNotes(component.getNotes());
        response.setCreatedAt(component.getCreatedAt());
        return response;
    }
}
