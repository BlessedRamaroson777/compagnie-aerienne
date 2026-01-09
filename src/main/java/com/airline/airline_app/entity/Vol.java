package com.airline.airline_app.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
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
    
    @Column(unique = true, nullable = false)
    private String numeroVol;
    
    @Column(nullable = false)
    private String depart;
    
    @Column(nullable = false)
    private String arrivee;
    
    @Column(nullable = false)
    private LocalDateTime dateHeureDepart;
    
    @Column(nullable = false)
    private LocalDateTime dateHeureArrivee;
    
    @Column(nullable = false)
    private BigDecimal prixBase;
    
    @ManyToOne
    @JoinColumn(name = "avion_id", nullable = false)
    private Avion avion;
    
    @OneToMany(mappedBy = "vol", cascade = CascadeType.ALL)
    private List<SiegeVol> siegesVol = new ArrayList<>();
    
    @OneToMany(mappedBy = "vol")
    private List<Reservation> reservations = new ArrayList<>();
    
    @OneToMany(mappedBy = "vol", cascade = CascadeType.ALL)
    private List<VolEquipage> equipages = new ArrayList<>();
}
