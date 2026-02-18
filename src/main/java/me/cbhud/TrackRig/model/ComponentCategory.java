package me.cbhud.TrackRig.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "component_category")
@Getter
@Setter
public class ComponentCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // FIX: Changed Long to Integer — SQL 'SERIAL' maps to INTEGER, not BIGINT.
    private Integer id;

    @Column(nullable = false, unique = true)
    private String name;

    private String description;

}
