package com.airline.airline_app.entity;

import com.airline.airline_app.entity.enums.StatutBillet;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "billet")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Billet {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;
    
    @OneToOne
    @JoinColumn(name = "siege_vol_id", nullable = false, unique = true)
    private SiegeVol siegeVol;
    
    @Column(nullable = false)
    private BigDecimal prix;
    
    @Column(name = "prix_base")
    private BigDecimal prixBase;
    
    @Column(name = "remise")
    private BigDecimal remise;
    
    @ManyToOne
    @JoinColumn(name = "categorie_passager_id")
    private CategoriePassager categoriePassager;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutBillet statut;
}
