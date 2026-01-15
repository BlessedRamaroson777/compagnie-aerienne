package com.airline.airline_app.controller;

import com.airline.airline_app.entity.*;
import com.airline.airline_app.repository.AvionClasseConfigurationRepository;
import com.airline.airline_app.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/prix-vol")
@RequiredArgsConstructor
public class PrixVolController {
    
    private final VolService volService;
    private final VolProgrammeService volProgrammeService;
    private final VolProgrammeClassePrixService volProgrammeClassePrixService;
    private final AvionClasseConfigurationRepository avionClasseConfigurationRepository;
    
    @GetMapping
    public String listPrixParVol(@RequestParam(required = false) Long volId, Model model) {
        List<Vol> vols = volService.findAll();
        model.addAttribute("vols", vols);
        
        if (volId != null) {
            Vol vol = volService.findById(volId).orElse(null);
            if (vol != null) {
                // Récupérer tous les vols programmés pour ce vol
                List<VolProgramme> volsProgrammes = volProgrammeService.findAll().stream()
                    .filter(vp -> vp.getVol().getId().equals(volId))
                    .collect(Collectors.toList());
                
                // Calculer le revenu pour chaque vol programmé
                List<VolProgrammeRevenu> volsProgrammesAvecRevenu = volsProgrammes.stream()
                    .map(vp -> {
                        BigDecimal revenuTotal = calculerRevenuTotal(vp);
                        return new VolProgrammeRevenu(vp, revenuTotal);
                    })
                    .collect(Collectors.toList());
                
                model.addAttribute("vol", vol);
                model.addAttribute("volsProgrammesAvecRevenu", volsProgrammesAvecRevenu);
            }
        }
        
        model.addAttribute("volId", volId);
        return "prix-vol/list";
    }
    
    private BigDecimal calculerRevenuTotal(VolProgramme volProgramme) {
        BigDecimal total = BigDecimal.ZERO;
        
        // Récupérer les prix par classe pour ce vol programmé
        List<VolProgrammeClassePrix> prixParClasse = volProgrammeClassePrixService.getPrixByVolProgramme(volProgramme.getId());
        
        // Récupérer les configurations de l'avion
        List<AvionClasseConfiguration> configurations = avionClasseConfigurationRepository.findByAvionId(volProgramme.getAvion().getId());
        
        // Calculer le revenu maximum (prix × nombre de places) pour chaque classe
        for (AvionClasseConfiguration config : configurations) {
            Optional<VolProgrammeClassePrix> prixOpt = prixParClasse.stream()
                .filter(p -> p.getClasseSiege().getId().equals(config.getClasseSiege().getId()))
                .findFirst();
            
            if (prixOpt.isPresent()) {
                BigDecimal prix = prixOpt.get().getPrix();
                int nombrePlaces = config.getNombrePlaces();
                total = total.add(prix.multiply(BigDecimal.valueOf(nombrePlaces)));
            }
        }
        
        return total;
    }
    
    // Classe interne pour transporter les données
    public static class VolProgrammeRevenu {
        private final VolProgramme volProgramme;
        private final BigDecimal revenuTotal;
        
        public VolProgrammeRevenu(VolProgramme volProgramme, BigDecimal revenuTotal) {
            this.volProgramme = volProgramme;
            this.revenuTotal = revenuTotal;
        }
        
        public VolProgramme getVolProgramme() {
            return volProgramme;
        }
        
        public BigDecimal getRevenuTotal() {
            return revenuTotal;
        }
    }
}
