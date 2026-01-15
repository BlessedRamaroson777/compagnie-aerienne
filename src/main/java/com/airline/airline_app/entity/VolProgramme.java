package com.airline.airline_app.entity;

import com.airline.airline_app.entity.enums.StatutVol;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "vol_programme")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VolProgramme {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "vol_id", nullable = false)
    private Vol vol;

    @ManyToOne
    @JoinColumn(name = "avion_id", nullable = false)
    private Avion avion;

    @Column(name = "date_heure_depart", nullable = false)
    private LocalDateTime dateHeureDepart;

    @Column(name = "date_heure_arrivee", nullable = false)
    private LocalDateTime dateHeureArrivee;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutVol status = StatutVol.PLANIFIE;

    @OneToMany(mappedBy = "volProgramme", cascade = CascadeType.ALL)
    private List<PrixVol> prixVols = new ArrayList<>();

    @OneToMany(mappedBy = "volProgramme", cascade = CascadeType.ALL)
    private List<SiegeVol> siegesVol = new ArrayList<>();

    @OneToMany(mappedBy = "volProgramme", cascade = CascadeType.ALL)
    private List<VolEquipage> equipages = new ArrayList<>();

    @OneToMany(mappedBy = "volProgramme")
    private List<Reservation> reservations = new ArrayList<>();

    @OneToMany(mappedBy = "volProgramme", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VolProgrammeClassePrix> prixParClasse = new ArrayList<>();
}
