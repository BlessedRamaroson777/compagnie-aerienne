package com.airline.airline_app.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "vente_produit")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VenteProduit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_vente_produit")
    private Long idVenteProduit;

    @ManyToOne
    @JoinColumn(name = "id_produit", nullable = false)
    private Produit produit;

    @Column(nullable = false)
    private Integer quantite;

    @ManyToOne
    @JoinColumn(name = "id_vol_programme", nullable = false)
    private VolProgramme volProgramme;

    @Column(name = "date_vente")
    private LocalDateTime dateVente = LocalDateTime.now();
}
