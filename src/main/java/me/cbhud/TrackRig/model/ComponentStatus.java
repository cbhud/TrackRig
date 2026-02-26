package me.cbhud.TrackRig.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "component_status")
@Getter
@Setter
public class ComponentStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "Component status name is required")
    @Column(nullable = false, unique = true)
    private String name;

}
