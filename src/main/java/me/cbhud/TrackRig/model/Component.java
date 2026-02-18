package me.cbhud.TrackRig.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "component")
@Getter
@Setter
public class Component {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // FIX: Changed Long to Integer — SQL 'SERIAL' maps to INTEGER, not BIGINT.
    private Integer id;

    @Column(name = "serial_number", unique = true, length = 100)
    private String serialNumber;

    // FIX: Added 'name' field — SQL defines 'name VARCHAR(200) NOT NULL'
    // but this field was completely missing from the entity.
    @Column(nullable = false, length = 200)
    private String name;

    // FIX: Added @JoinColumn(name = "category_id") — without it, Hibernate
    // auto-generates the column name as 'component_category_id', which doesn't
    // match the SQL schema's 'category_id'.
    // Added nullable = false to match SQL's 'NOT NULL' constraint.
    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private ComponentCategory componentCategory;

    // FIX: Added @JoinColumn(name = "status_id") — same reason as above.
    // Hibernate would generate 'component_status_id' instead of 'status_id'.
    // Added nullable = false to match SQL's 'NOT NULL' constraint.
    @ManyToOne
    @JoinColumn(name = "status_id", nullable = false)
    private ComponentStatus componentStatus;

    // FIX: Added @JoinColumn(name = "workstation_id") — ensures the FK column
    // name matches the SQL schema. Nullable is allowed here since components
    // can be in storage (workstation_id = NULL).
    @ManyToOne
    @JoinColumn(name = "workstation_id")
    private Workstation workstation;

    @Column(name = "purchase_date")
    private LocalDate purchaseDate;

    // FIX: Added @Column(name = "warranty_expiry") — the SQL column is
    // 'warranty_expiry' but Hibernate would map 'warrantyDate' to 'warranty_date'.
    @Column(name = "warranty_expiry")
    private LocalDate warrantyDate;

    private String notes;

    // FIX: Added @Column(name = "created_at") with insertable/updatable = false —
    // the SQL column is 'created_at' with DEFAULT CURRENT_TIMESTAMP,
    // so the DB handles population and it shouldn't be set by JPA.
    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

}
