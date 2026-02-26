package me.cbhud.TrackRig.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "maintenance_type")
@Getter
@Setter
public class MaintenanceType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "Maintenance type name is required")
    @Size(max = 100, message = "Name must be at most 100 characters")
    @Column(nullable = false, unique = true, length = 100)
    private String name;

    private String description;

    @NotNull(message = "Interval days is required")
    @Column(name = "interval_days")
    private Integer intervalDays;

    @NotNull(message = "isActive flag is required")
    @Column(name = "is_active")
    private Boolean isActive;

}
