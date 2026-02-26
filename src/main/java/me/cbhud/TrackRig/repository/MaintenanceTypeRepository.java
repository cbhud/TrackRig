package me.cbhud.TrackRig.repository;

import me.cbhud.TrackRig.model.MaintenanceType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MaintenanceTypeRepository extends JpaRepository<MaintenanceType, Integer> {

    // Get only active maintenance types
    // Matches the SQL view filter: WHERE mt.is_active = TRUE
    List<MaintenanceType> findByIsActiveTrue();
}
