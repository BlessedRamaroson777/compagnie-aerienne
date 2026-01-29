package com.airline.airline_app.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "historique_mouvement_produit")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HistoriqueMouvementProduit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_historique")
    private Long idHistorique;

    @ManyToOne
    @JoinColumn(name = "id_produit", nullable = false)
    private Produit produit;

    @Column(nullable = false)
    private Integer quantite;

    @Column(name = "est_entree", nullable = false)
    private Boolean estEntree;

    @Column(name = "date_mouvement")
    private LocalDateTime dateMouvement = LocalDateTime.now();
}
