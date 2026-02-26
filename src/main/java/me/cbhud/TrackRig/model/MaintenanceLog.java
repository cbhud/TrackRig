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
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "workstation_id", nullable = false)
    private Workstation workstation;

    @ManyToOne
    @JoinColumn(name = "maintenance_type_id", nullable = false)
    private MaintenanceType maintenanceType;

    @ManyToOne
    @JoinColumn(name = "performed_by_user_id")
    private AppUser performedBy;

    private String notes;

    @Column(name = "performed_at")
    private LocalDateTime performedAt;

}
