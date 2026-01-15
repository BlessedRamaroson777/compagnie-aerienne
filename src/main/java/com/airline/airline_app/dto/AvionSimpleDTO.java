package com.airline.airline_app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AvionSimpleDTO {
    private Long id;
    private String codeAvion;
    private Integer capaciteTotale;
    private ModelAvionSimpleDTO modelAvion;
    private EtatAvionSimpleDTO etatAvion;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ModelAvionSimpleDTO {
        private Long id;
        private String designation;
        private String fabricant;
        private List<ClasseSiegeSimpleDTO> classesDisponibles;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EtatAvionSimpleDTO {
        private Long id;
        private String libelle;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ClasseSiegeSimpleDTO {
        private Long id;
        private String libelle;
        private String description;
    }
}
