package com.airline.airline_app.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "categorie_passager")
public class CategoriePassager {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCategoriePassager;
    
    @Column(nullable = false, length = 100)
    private String libelle;
    
    @Column(length = 500)
    private String description;
    
    @OneToMany(mappedBy = "categoriePassager", cascade = CascadeType.ALL)
    private List<RemiseCategoriePlace> remises;
    
    // Constructeurs
    public CategoriePassager() {
    }
    
    public CategoriePassager(String libelle, String description) {
        this.libelle = libelle;
        this.description = description;
    }
    
    // Getters et Setters
    public Long getIdCategoriePassager() {
        return idCategoriePassager;
    }
    
    public void setIdCategoriePassager(Long idCategoriePassager) {
        this.idCategoriePassager = idCategoriePassager;
    }
    
    public String getLibelle() {
        return libelle;
    }
    
    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public List<RemiseCategoriePlace> getRemises() {
        return remises;
    }
    
    public void setRemises(List<RemiseCategoriePlace> remises) {
        this.remises = remises;
    }
}
