package me.cbhud.TrackRig.repository;

import java.time.LocalDateTime;

/**
 * Spring Data JPA interface-based projection that maps to the columns
 * returned by the PostgreSQL view 'view_maintenance_status'.
 *
 * This is NOT an entity — it's a read-only projection. Spring Data
 * automatically maps the native query result columns to these getter methods
 * based on the column aliases (e.g., AS workstationId → getWorkstationId()).
 */
public interface MaintenanceStatusProjection {

    Integer getWorkstationId();

    String getWorkstationName();

    Integer getMaintenanceTypeId();

    String getMaintenanceName();

    Integer getIntervalDays();

    LocalDateTime getLastPerformed();

    LocalDateTime getNextDueDate();

    String getStatus();
}
