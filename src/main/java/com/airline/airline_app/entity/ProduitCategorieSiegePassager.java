package com.airline.airline_app.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "produit_categorie_siege_passager")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProduitCategorieSiegePassager {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_produit", nullable = false)
    private Produit produit;

    @ManyToOne
    @JoinColumn(name = "id_categorie_passager", nullable = false)
    private CategoriePassager categoriePassager;

    @ManyToOne
    @JoinColumn(name = "id_classe_siege", nullable = false)
    private ClasseSiege classeSiege;
}
