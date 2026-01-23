package com.airline.airline_app.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "paiement_diffusion")
public class PaiementDiffusion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_paiement_diffusion")
    private Long idPaiementDiffusion;

    @ManyToOne
    @JoinColumn(name = "id_diffusion_societe", nullable = false)
    private DiffusionSociete diffusionSociete;

    @Column(name = "montant_paye", nullable = false)
    private BigDecimal montantPaye;

    @Column(name = "date_paiement", nullable = false)
    private LocalDate datePaiement;

    // Constructeurs
    public PaiementDiffusion() {
    }

    public PaiementDiffusion(DiffusionSociete diffusionSociete, BigDecimal montantPaye, LocalDate datePaiement) {
        this.diffusionSociete = diffusionSociete;
        this.montantPaye = montantPaye;
        this.datePaiement = datePaiement;
    }

    // Getters et Setters
    public Long getIdPaiementDiffusion() {
        return idPaiementDiffusion;
    }

    public void setIdPaiementDiffusion(Long idPaiementDiffusion) {
        this.idPaiementDiffusion = idPaiementDiffusion;
    }

    public DiffusionSociete getDiffusionSociete() {
        return diffusionSociete;
    }

    public void setDiffusionSociete(DiffusionSociete diffusionSociete) {
        this.diffusionSociete = diffusionSociete;
    }

    public BigDecimal getMontantPaye() {
        return montantPaye;
    }

    public void setMontantPaye(BigDecimal montantPaye) {
        this.montantPaye = montantPaye;
    }

    public LocalDate getDatePaiement() {
        return datePaiement;
    }

    public void setDatePaiement(LocalDate datePaiement) {
        this.datePaiement = datePaiement;
    }
}
