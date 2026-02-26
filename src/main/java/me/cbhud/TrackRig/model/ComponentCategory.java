package me.cbhud.TrackRig.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "component_category")
@Getter
@Setter
public class ComponentCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "Component category name is required")
    @Column(nullable = false, unique = true)
    private String name;

    private String description;

}
