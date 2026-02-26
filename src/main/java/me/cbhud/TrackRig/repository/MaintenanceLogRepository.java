package me.cbhud.TrackRig.repository;

import me.cbhud.TrackRig.model.MaintenanceLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MaintenanceLogRepository extends JpaRepository<MaintenanceLog, Integer> {

    // Get all maintenance logs for a specific workstation
    // Useful for viewing maintenance history (SQL: workstation_id INT NOT NULL
    // REFERENCES workstation)
    List<MaintenanceLog> findByWorkstationId(Integer workstationId);

    // ========================
    // NATIVE QUERIES ON view_maintenance_status
    // ========================
    // These query the PostgreSQL view directly instead of recomputing the logic in
    // Java.
    // The view does: CROSS JOIN workstation × active maintenance types,
    // LEFT JOIN latest log per combo, and computes status
    // (OK/DUE_SOON/OVERDUE/NEVER_DONE).

    // Get maintenance status for a specific workstation (all active types)
    @Query(value = "SELECT workstation_id AS workstationId, " +
            "workstation_name AS workstationName, " +
            "maintenance_type_id AS maintenanceTypeId, " +
            "maintenance_name AS maintenanceName, " +
            "interval_days AS intervalDays, " +
            "last_performed AS lastPerformed, " +
            "next_due_date AS nextDueDate, " +
            "status " +
            "FROM view_maintenance_status " +
            "WHERE workstation_id = :workstationId", nativeQuery = true)
    List<MaintenanceStatusProjection> findMaintenanceStatusByWorkstationId(
            @Param("workstationId") Integer workstationId);

    // Get all workstation/type combos that are OVERDUE or DUE_SOON (for dashboard
    // alerts)
    @Query(value = "SELECT workstation_id AS workstationId, " +
            "workstation_name AS workstationName, " +
            "maintenance_type_id AS maintenanceTypeId, " +
            "maintenance_name AS maintenanceName, " +
            "interval_days AS intervalDays, " +
            "last_performed AS lastPerformed, " +
            "next_due_date AS nextDueDate, " +
            "status " +
            "FROM view_maintenance_status " +
            "WHERE status IN ('OVERDUE', 'DUE_SOON') " +
            "ORDER BY next_due_date ASC", nativeQuery = true)
    List<MaintenanceStatusProjection> findAllOverdue();

    // Get ALL maintenance statuses across all workstations
    @Query(value = "SELECT workstation_id AS workstationId, " +
            "workstation_name AS workstationName, " +
            "maintenance_type_id AS maintenanceTypeId, " +
            "maintenance_name AS maintenanceName, " +
            "interval_days AS intervalDays, " +
            "last_performed AS lastPerformed, " +
            "next_due_date AS nextDueDate, " +
            "status " +
            "FROM view_maintenance_status " +
            "ORDER BY next_due_date ASC", nativeQuery = true)
    List<MaintenanceStatusProjection> findAllMaintenanceStatuses();
}
