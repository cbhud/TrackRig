package me.cbhud.TrackRig.repository;

import me.cbhud.TrackRig.model.Component;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ComponentRepository extends JpaRepository<Component, Integer> {

    // Get all components assigned to a specific workstation
    // Useful for the workstation detail view to show installed parts
    List<Component> findByWorkstationId(Integer workstationId);

    // Get all components not assigned to any workstation (in storage/stock)
    // Maps to SQL: WHERE workstation_id IS NULL
    List<Component> findByWorkstationIsNull();

    // Lookup a component by its unique serial number
    // SQL: serial_number VARCHAR(100) UNIQUE
    Optional<Component> findBySerialNumber(String serialNumber);
}
