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
    private String type; // "VOL", "AVION", "VOL_PROGRAMME"
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
}
