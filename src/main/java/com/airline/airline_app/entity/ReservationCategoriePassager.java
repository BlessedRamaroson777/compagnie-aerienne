package com.airline.airline_app.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "reservation_categorie_passager")
public class ReservationCategoriePassager {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;
    
    @ManyToOne
    @JoinColumn(name = "classe_siege_id", nullable = false)
    private ClasseSiege classeSiege;
    
    @ManyToOne
    @JoinColumn(name = "categorie_passager_id", nullable = false)
    private CategoriePassager categoriePassager;
    
    @Column(name = "nombre_passagers", nullable = false)
    private Integer nombrePassagers;
    
    @Column(name = "prix_unitaire", nullable = false)
    private Double prixUnitaire;
    
    @Column(name = "remise_unitaire")
    private Double remiseUnitaire = 0.0;
    
    @Column(name = "prix_apres_remise", nullable = false)
    private Double prixApresRemise;
    
    @Column(name = "sous_total", nullable = false)
    private Double sousTotal;
    
    // Constructeurs
    public ReservationCategoriePassager() {
    }
    
    public ReservationCategoriePassager(Reservation reservation, ClasseSiege classeSiege, 
                                       CategoriePassager categoriePassager, Integer nombrePassagers,
                                       Double prixUnitaire, Double remiseUnitaire) {
        this.reservation = reservation;
        this.classeSiege = classeSiege;
        this.categoriePassager = categoriePassager;
        this.nombrePassagers = nombrePassagers;
        this.prixUnitaire = prixUnitaire;
        this.remiseUnitaire = remiseUnitaire;
        this.prixApresRemise = prixUnitaire - remiseUnitaire;
        this.sousTotal = this.prixApresRemise * nombrePassagers;
    }
    
    // Getters et Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Reservation getReservation() {
        return reservation;
    }
    
    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }
    
    public ClasseSiege getClasseSiege() {
        return classeSiege;
    }
    
    public void setClasseSiege(ClasseSiege classeSiege) {
        this.classeSiege = classeSiege;
    }
    
    public CategoriePassager getCategoriePassager() {
        return categoriePassager;
    }
    
    public void setCategoriePassager(CategoriePassager categoriePassager) {
        this.categoriePassager = categoriePassager;
    }
    
    public Integer getNombrePassagers() {
        return nombrePassagers;
    }
    
    public void setNombrePassagers(Integer nombrePassagers) {
        this.nombrePassagers = nombrePassagers;
        calculerSousTotal();
    }
    
    public Double getPrixUnitaire() {
        return prixUnitaire;
    }
    
    public void setPrixUnitaire(Double prixUnitaire) {
        this.prixUnitaire = prixUnitaire;
        calculerPrixApresRemise();
    }
    
    public Double getRemiseUnitaire() {
        return remiseUnitaire;
    }
    
    public void setRemiseUnitaire(Double remiseUnitaire) {
        this.remiseUnitaire = remiseUnitaire;
        calculerPrixApresRemise();
    }
    
    public Double getPrixApresRemise() {
        return prixApresRemise;
    }
    
    public void setPrixApresRemise(Double prixApresRemise) {
        this.prixApresRemise = prixApresRemise;
    }
    
    public Double getSousTotal() {
        return sousTotal;
    }
    
    public void setSousTotal(Double sousTotal) {
        this.sousTotal = sousTotal;
    }
    
    // Méthodes utilitaires
    public void calculerPrixApresRemise() {
        if (prixUnitaire != null && remiseUnitaire != null) {
            this.prixApresRemise = prixUnitaire - remiseUnitaire;
            calculerSousTotal();
        }
    }
    
    public void calculerSousTotal() {
        if (prixApresRemise != null && nombrePassagers != null) {
            this.sousTotal = prixApresRemise * nombrePassagers;
        }
    }
}
