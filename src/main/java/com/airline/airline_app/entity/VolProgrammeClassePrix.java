package com.airline.airline_app.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "vol_programme_classe_prix",
    uniqueConstraints = @UniqueConstraint(columnNames = {"vol_programme_id", "classe_siege_id"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VolProgrammeClassePrix {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "vol_programme_id", nullable = false)
    private VolProgramme volProgramme;

    @ManyToOne
    @JoinColumn(name = "classe_siege_id", nullable = false)
    private ClasseSiege classeSiege;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal prix;
}
