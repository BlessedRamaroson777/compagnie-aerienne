package com.airline.airline_app.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "diffusion_societe")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiffusionSociete {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_diffusion_societe")
    private Long idDiffusionSociete;

    @ManyToOne
    @JoinColumn(name = "id_societe", nullable = false)
    private Societe societe;

    @Column(nullable = false)
    private Integer nombre;

    @Column(nullable = false)
    private LocalDateTime date;

    @OneToMany(mappedBy = "diffusionSociete", cascade = CascadeType.ALL)
    private List<DiffusionVolProgramme> diffusionsVolProgramme = new ArrayList<>();
}
