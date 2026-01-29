package com.airline.airline_app.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "prix_produit")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PrixProduit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_prix_produit")
    private Long idPrixProduit;

    @ManyToOne
    @JoinColumn(name = "id_produit", nullable = false)
    private Produit produit;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal prix;

    @Column(name = "date_modification")
    private LocalDateTime dateModification = LocalDateTime.now();
}
