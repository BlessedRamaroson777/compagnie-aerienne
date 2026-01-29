package com.airline.airline_app.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "produit")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Produit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_produit")
    private Long idProduit;

    @Column(nullable = false)
    private String libelle;

    @Column(nullable = false)
    private Integer stock = 0;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal prix;

    @Column(name = "date_creation")
    private LocalDateTime dateCreation = LocalDateTime.now();
}
