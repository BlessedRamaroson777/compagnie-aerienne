package com.airline.airline_app.controller.api;

import com.airline.airline_app.entity.VolProgrammeClassePrix;
import com.airline.airline_app.repository.VolProgrammeClassePrixRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/vols-programmes")
@RequiredArgsConstructor
public class VolProgrammeApiController {
    
    private final VolProgrammeClassePrixRepository volProgrammeClassePrixRepository;
    
    /**
     * Récupère les prix pour un vol programmé (pour la simulation)
     */
    @GetMapping("/{id}/prix")
    public ResponseEntity<List<Map<String, Object>>> getPrixVolProgramme(@PathVariable Long id) {
        List<VolProgrammeClassePrix> prixList = volProgrammeClassePrixRepository.findByVolProgrammeId(id);
        
        List<Map<String, Object>> result = prixList.stream().map(prix -> {
            Map<String, Object> map = new HashMap<>();
            map.put("categoriePassagerId", prix.getCategoriePassager().getIdCategoriePassager());
            map.put("classeId", prix.getClasseSiege().getId());
            map.put("prix", prix.getPrix());
            
            // Calculer le prix final (après remise si applicable)
            BigDecimal prixFinal = prix.getPrix();
            // On pourrait ajouter la logique de remise ici si nécessaire
            map.put("prixFinal", prixFinal);
            
            return map;
        }).collect(Collectors.toList());
        
        return ResponseEntity.ok(result);
    }
}
