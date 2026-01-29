package com.airline.airline_app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaiementDiffusionDTO {
    private Long idDiffusionSociete;
    private String nomSociete;
    private BigDecimal montantTotalAPayer;
    private BigDecimal montantTotalPaye;
    private BigDecimal resteAPayer;
    private BigDecimal pourcentagePaye;
    
    // Détails des diffusions vol programme
    private Long idDiffusionVolProgramme;
    private String numeroVol;
    private String aeroportDepart;
    private String aeroportArrivee;
    private Integer nombreDiffusions;
    private BigDecimal montantVolProgramme;
    private BigDecimal montantPayeVolProgramme;
    private BigDecimal resteAPayerVolProgramme;
}
