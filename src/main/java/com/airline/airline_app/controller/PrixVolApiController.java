package com.airline.airline_app.controller;

import com.airline.airline_app.entity.PrixVol;
import com.airline.airline_app.repository.PrixVolRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/prix-vol")
@RequiredArgsConstructor
public class PrixVolApiController {
    
    private final PrixVolRepository prixVolRepository;
    
    /**
     * Récupère les prix par vol programmé
     */
    @GetMapping("/vol-programme/{volProgrammeId}")
    public ResponseEntity<List<PrixVol>> getPrixByVolProgramme(@PathVariable Long volProgrammeId) {
        List<PrixVol> prixVols = prixVolRepository.findByVolProgrammeId(volProgrammeId);
        return ResponseEntity.ok(prixVols);
    }
}
