package com.airline.airline_app.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "vol_equipage")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VolEquipage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "vol_programme_id", nullable = false)
    private VolProgramme volProgramme;

    @ManyToOne
    @JoinColumn(name = "equipage_id", nullable = false)
    private Equipage equipage;
}
