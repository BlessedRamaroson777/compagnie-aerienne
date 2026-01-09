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
@Table(name = "vol")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Vol {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "numero_vol", unique = true, nullable = false, length = 50)
    private String numeroVol;
    
    @ManyToOne
    @JoinColumn(name = "aeroport_depart_id", nullable = false)
    private Aeroport aeroportDepart;
    
    @ManyToOne
    @JoinColumn(name = "aeroport_arrivee_id", nullable = false)
    private Aeroport aeroportArrivee;
    
    @Column(name = "date_heure_depart", nullable = false)
    private LocalDateTime dateHeureDepart;
    
    @Column(name = "date_heure_arrivee", nullable = false)
    private LocalDateTime dateHeureArrivee;
    
    @ManyToOne
    @JoinColumn(name = "avion_id", nullable = false)
    private Avion avion;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutVol status = StatutVol.PLANIFIE;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    @OneToMany(mappedBy = "vol", cascade = CascadeType.ALL)
    private List<SiegeVol> siegesVol = new ArrayList<>();
    
    @OneToMany(mappedBy = "vol")
    private List<Reservation> reservations = new ArrayList<>();
    
    @OneToMany(mappedBy = "vol", cascade = CascadeType.ALL)
    private List<VolEquipage> equipages = new ArrayList<>();
    
    @OneToMany(mappedBy = "vol", cascade = CascadeType.ALL)
    private List<PrixVol> prixVols = new ArrayList<>();
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
