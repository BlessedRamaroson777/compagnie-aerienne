package com.airline.airline_app.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "diffusion_vol_programme")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiffusionVolProgramme {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_diffusion_vol_programme")
    private Long idDiffusionVolProgramme;

    @ManyToOne
    @JoinColumn(name = "id_diffusion_societe", nullable = false)
    private DiffusionSociete diffusionSociete;

    @Column(nullable = false)
    private Integer nombre;

    @ManyToOne
    @JoinColumn(name = "id_vol_programme", nullable = false)
    private VolProgramme volProgramme;
}
