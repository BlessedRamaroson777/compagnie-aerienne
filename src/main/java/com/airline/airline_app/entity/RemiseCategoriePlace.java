package com.airline.airline_app.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "remise_categorie_place")
public class RemiseCategoriePlace {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "vol_programme_id", nullable = false)
    private VolProgramme volProgramme;
    
    @ManyToOne
    @JoinColumn(name = "id_categorie_passager", nullable = false)
    private CategoriePassager categoriePassager;
    
    @ManyToOne
    @JoinColumn(name = "id_classe_siege", nullable = false)
    private ClasseSiege classeSiege;
    
    @Column(nullable = false)
    private Double remisePrix;
    
    // Constructeurs
    public RemiseCategoriePlace() {
    }
    
    public RemiseCategoriePlace(VolProgramme volProgramme, CategoriePassager categoriePassager, ClasseSiege classeSiege, Double remisePrix) {
        this.volProgramme = volProgramme;
        this.categoriePassager = categoriePassager;
        this.classeSiege = classeSiege;
        this.remisePrix = remisePrix;
    }
    
    // Getters et Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public VolProgramme getVolProgramme() {
        return volProgramme;
    }
    
    public void setVolProgramme(VolProgramme volProgramme) {
        this.volProgramme = volProgramme;
    }
    
    public CategoriePassager getCategoriePassager() {
        return categoriePassager;
    }
    
    public void setCategoriePassager(CategoriePassager categoriePassager) {
        this.categoriePassager = categoriePassager;
    }
    
    public ClasseSiege getClasseSiege() {
        return classeSiege;
    }
    
    public void setClasseSiege(ClasseSiege classeSiege) {
        this.classeSiege = classeSiege;
    }
    
    public Double getRemisePrix() {
        return remisePrix;
    }
    
    public void setRemisePrix(Double remisePrix) {
        this.remisePrix = remisePrix;
    }
}
