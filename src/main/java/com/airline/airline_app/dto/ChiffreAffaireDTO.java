package com.airline.airline_app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChiffreAffaireDTO {
    
    private Long id;
    private String designation; // Vol, Avion, ou VolProgramme
    private String type; // "VOL", "AVION", "VOL_PROGRAMME", "CATEGORIE"
    private BigDecimal montantTotal;
    private Integer nombreReservations;
    private Integer nombreBillets;
    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;
    
    // Informations supplémentaires selon le type
    private String numeroVol;
    private String immatriculation;
    private String aeroportDepart;
    private String aeroportArrivee;
    private LocalDateTime dateHeureVol;
    
    // Informations sur les remises
    private BigDecimal montantRemises; // Total des remises accordées
    private BigDecimal montantAvantRemise; // Montant avant application des remises
    
    // Informations sur le chiffre d'affaires détaillé
    private BigDecimal montantTickets; // Montant généré par les tickets vendus
    private BigDecimal montantDiffusion; // Montant généré par la diffusion de publicité
    
    // Détails du paiement de diffusion
    private BigDecimal montantDiffusionTotal; // Montant total à payer pour la diffusion
    private BigDecimal montantDiffusionPaye; // Montant déjà payé sur la diffusion
    private BigDecimal montantDiffusionReste; // Reste à payer sur la diffusion
    
    // Montant vente produits
    private BigDecimal montantVenteProduits; // CA généré par la vente de produits
}
