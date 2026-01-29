package com.airline.airline_app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CAMensuelDTO {
    
    private Integer mois;
    private Integer annee;
    private String moisLibelle;
    
    // CA Tickets
    private BigDecimal montantTickets;
    
    // CA Diffusion
    private BigDecimal montantDiffusionTotal;
    private BigDecimal montantDiffusionPaye;
    private BigDecimal montantDiffusionReste;
    
    // CA Vente Produits
    private BigDecimal montantVenteProduits;
    
    // CA Total
    private BigDecimal montantTotal;
    
    // Statistiques
    private Long nombreReservations;
    private Long nombreVentes;
}
