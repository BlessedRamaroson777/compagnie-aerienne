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
     * Récupère les prix par classe pour un vol programmé (pour la simulation)
     * Note: Les prix sont par classe uniquement, pas par catégorie de passager
     */
    @GetMapping("/{id}/prix")
    public ResponseEntity<List<Map<String, Object>>> getPrixVolProgramme(@PathVariable Long id) {
        List<VolProgrammeClassePrix> prixList = volProgrammeClassePrixRepository.findByVolProgrammeId(id);
        
        List<Map<String, Object>> result = prixList.stream().map(prix -> {
            Map<String, Object> map = new HashMap<>();
            map.put("classeId", prix.getClasseSiege().getId());
            map.put("prix", prix.getPrix());
            map.put("prixFinal", prix.getPrix()); // Prix de base, sans remise pour l'instant
            
            return map;
        }).collect(Collectors.toList());
        
        return ResponseEntity.ok(result);
    }
}
