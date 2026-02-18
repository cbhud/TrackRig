package me.cbhud.TrackRig.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "workstation")
@Getter
@Setter
public class Workstation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // FIX: Changed Long to Integer — SQL 'SERIAL' maps to INTEGER, not BIGINT.
    private Integer id;

    @Column(nullable = false, unique = true)
    private String name;

    // FIX: Removed @Column — it is invalid on a @ManyToOne relationship.
    // Only @JoinColumn should be used to specify the foreign key column name.
    @ManyToOne
    @JoinColumn(name = "status_id")
    private WorkstationStatus workstationStatus;

    @Column(name = "grid_x")
    private int gridX;

    @Column(name = "grid_y")
    private int gridY;

    // FIX: Removed @NotNull — the SQL column has no NOT NULL constraint,
    // and the value is auto-populated by the DB via DEFAULT CURRENT_TIMESTAMP.
    // @NotNull would cause validation errors before the entity is persisted.
    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

}
