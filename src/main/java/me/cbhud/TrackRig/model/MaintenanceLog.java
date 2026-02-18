package me.cbhud.TrackRig.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "maintenance_log")
@Getter
@Setter
public class MaintenanceLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // FIX: Changed Long to Integer — SQL 'SERIAL' maps to INTEGER, not BIGINT.
    private Integer id;

    // FIX: Added nullable = false — SQL defines 'workstation_id INT NOT NULL'.
    @ManyToOne
    @JoinColumn(name = "workstation_id", nullable = false)
    private Workstation workstation;

    // FIX: Added nullable = false — SQL defines 'maintenance_type_id INT NOT NULL'.
    @ManyToOne
    @JoinColumn(name = "maintenance_type_id", nullable = false)
    private MaintenanceType maintenanceType;

    // FIX: Changed column name from 'performed_by' to 'performed_by_user_id' —
    // the SQL column is named 'performed_by_user_id', not 'performed_by'.
    @ManyToOne
    @JoinColumn(name = "performed_by_user_id")
    private AppUser performedBy;

    private String notes;

    @Column(name = "performed_at")
    private LocalDateTime performedAt;

}
