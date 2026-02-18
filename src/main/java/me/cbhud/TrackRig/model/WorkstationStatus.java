package me.cbhud.TrackRig.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Entity
@Table(name = "workstation_status")
@Getter
@Setter
public class WorkstationStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // FIX: Changed Long to Integer — SQL 'SERIAL' maps to INTEGER, not BIGINT.
    private Integer id;

    @Column(nullable = false, unique = true)
    @Length(min = 1, max = 50)
    private String name;

}
