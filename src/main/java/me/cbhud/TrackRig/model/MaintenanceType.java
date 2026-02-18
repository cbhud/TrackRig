package me.cbhud.TrackRig.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "maintenance_type")
@Getter
@Setter
public class MaintenanceType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // FIX: Changed Long to Integer — SQL 'SERIAL' maps to INTEGER, not BIGINT.
    private Integer id;

    // FIX: Added @Column(nullable = false) — SQL defines 'name VARCHAR(100) NOT
    // NULL'
    // Without this, JPA allows null values that would violate the DB constraint.
    @Column(nullable = false, length = 100)
    private String name;

    private String description;

    @Column(name = "interval_days")
    private int intervalDays;

    @Column(name = "is_active")
    private Boolean isActive;

}
