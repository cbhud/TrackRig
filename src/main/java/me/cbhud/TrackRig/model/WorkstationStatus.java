package me.cbhud.TrackRig.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "workstation_status")
@Getter
@Setter
public class WorkstationStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "Workstation status name is required")
    @Size(max = 50, message = "Name must be at most 50 characters")
    @Column(nullable = false, unique = true)
    private String name;

}
