package com.airline.airline_app.controller;

import com.airline.airline_app.dto.ChiffreAffaireDTO;
import com.airline.airline_app.entity.*;
import com.airline.airline_app.repository.*;
import com.airline.airline_app.service.ChiffreAffaireService;
import com.airline.airline_app.service.ClasseSiegeService;
import com.airline.airline_app.service.VolProgrammeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/chiffre-affaire")
@RequiredArgsConstructor
public class ChiffreAffaireController {

    private final ChiffreAffaireService chiffreAffaireService;
    private final CategoriePassagerRepository categoriePassagerRepository;
    private final ClasseSiegeService classeSiegeService;
    private final VolProgrammeService volProgrammeService;
    private final DiffusionSocieteRepository diffusionSocieteRepository;
    private final DiffusionVolProgrammeRepository diffusionVolProgrammeRepository;
    private final PaiementDiffusionRepository paiementDiffusionRepository;
    private final ReservationRepository reservationRepository;
    private final BilletRepository billetRepository;

    /**
     * Page d'accueil des statistiques de chiffre d'affaires
     */
    @GetMapping
    public String index(Model model) {
        ChiffreAffaireDTO caGlobal = chiffreAffaireService.getCAGlobal();
        model.addAttribute("caGlobal", caGlobal);
        return "chiffre-affaire/index";
    }

    /**
     * Liste des CA par vol programmé (avec dates et heures)
     */
    @GetMapping("/par-vol-programme")
    public String parVolProgramme(Model model) {
        List<ChiffreAffaireDTO> caList = chiffreAffaireService.getCAParVolProgramme();
        BigDecimal total = caList.stream()
            .map(ChiffreAffaireDTO::getMontantTotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        model.addAttribute("caList", caList);
        model.addAttribute("total", total);
        model.addAttribute("type", "VOL_PROGRAMME");
        model.addAttribute("titre", "Chiffre d'affaires par vol programmé");
        return "chiffre-affaire/list";
    }

    /**
     * Détails du CA pour un vol programmé spécifique
     */
    @GetMapping("/par-vol-programme/{id}")
    public String detailVolProgramme(@PathVariable Long id, Model model) {
        ChiffreAffaireDTO ca = chiffreAffaireService.getCAParVolProgramme(id);
        model.addAttribute("ca", ca);
        return "chiffre-affaire/detail";
    }

    /**
     * Liste des CA par avion
     */
    @GetMapping("/par-avion")
    public String parAvion(Model model) {
        List<ChiffreAffaireDTO> caList = chiffreAffaireService.getCAParAvion();
        BigDecimal total = caList.stream()
            .map(ChiffreAffaireDTO::getMontantTotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        model.addAttribute("caList", caList);
        model.addAttribute("total", total);
        model.addAttribute("type", "AVION");
        model.addAttribute("titre", "Chiffre d'affaires par avion");
        return "chiffre-affaire/list";
    }

    /**
     * Détails du CA pour un avion spécifique
     */
    @GetMapping("/par-avion/{id}")
    public String detailAvion(@PathVariable Long id, Model model) {
        ChiffreAffaireDTO ca = chiffreAffaireService.getCAParAvion(id);
        model.addAttribute("ca", ca);
        return "chiffre-affaire/detail";
    }

    /**
     * Liste des CA par trajet (Vol de base)
     */
    @GetMapping("/par-vol")
    public String parVol(Model model) {
        List<ChiffreAffaireDTO> caList = chiffreAffaireService.getCAParVol();
        BigDecimal total = caList.stream()
            .map(ChiffreAffaireDTO::getMontantTotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        model.addAttribute("caList", caList);
        model.addAttribute("total", total);
        model.addAttribute("type", "VOL");
        model.addAttribute("titre", "Chiffre d'affaires par trajet");
        return "chiffre-affaire/list";
    }

    /**
     * Détails du CA pour un trajet (Vol) spécifique
     */
    @GetMapping("/par-vol/{id}")
    public String detailVol(@PathVariable Long id, Model model) {
        ChiffreAffaireDTO ca = chiffreAffaireService.getCAParVol(id);
        model.addAttribute("ca", ca);
        return "chiffre-affaire/detail";
    }
    
    /**
     * Liste des CA par catégorie de passagers
     */
    @GetMapping("/par-categorie")
    public String parCategorie(Model model) {
        List<ChiffreAffaireDTO> caList = chiffreAffaireService.getCAParCategorie();
        BigDecimal total = caList.stream()
            .map(ChiffreAffaireDTO::getMontantTotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        model.addAttribute("caList", caList);
        model.addAttribute("total", total);
        model.addAttribute("type", "CATEGORIE");
        model.addAttribute("titre", "Chiffre d'affaires par catégorie de passagers");
        return "chiffre-affaire/list";
    }
    
    /**
     * Détails du CA par catégorie pour un vol programmé
     */
    @GetMapping("/par-vol-programme/{id}/categories")
    public String detailCategoriesVolProgramme(@PathVariable Long id, Model model) {
        ChiffreAffaireDTO ca = chiffreAffaireService.getCAParVolProgramme(id);
        List<ChiffreAffaireDTO> categories = chiffreAffaireService.getCAParCategorieForVolProgramme(id);
        model.addAttribute("ca", ca);
        model.addAttribute("categories", categories);
        return "chiffre-affaire/detail-categories";
    }
    
    /**
     * Page de simulation du chiffre d'affaires
     */
    @GetMapping("/simulation")
    public String simulation(Model model) {
        model.addAttribute("categories", categoriePassagerRepository.findAll());
        model.addAttribute("classes", classeSiegeService.findAll());
        model.addAttribute("volsProgrammes", volProgrammeService.findAll());
        return "chiffre-affaire/simulation";
    }
    
    /**
     * Chiffre d'affaires des diffusions pour un mois et une année donnés
     */
    @GetMapping("/diffusion/{mois}/{annee}")
    public String chiffreAffaireDiffusion(@PathVariable int mois, @PathVariable int annee, Model model) {
        ChiffreAffaireDTO caDiffusion = chiffreAffaireService.getChiffreAffaireDiffusion(mois, annee);
        model.addAttribute("caDiffusion", caDiffusion);
        model.addAttribute("mois", mois);
        model.addAttribute("annee", annee);
        return "chiffre-affaire/diffusion";
    }
    
    /**
     * Formulaire de sélection pour le CA des diffusions par mois
     */
    @GetMapping("/diffusion")
    public String chiffreAffaireDiffusionForm(Model model) {
        // Par défaut, afficher le mois et l'année actuelle
        java.time.LocalDate now = java.time.LocalDate.now();
        model.addAttribute("moisActuel", now.getMonthValue());
        model.addAttribute("anneeActuelle", now.getYear());
        return "chiffre-affaire/diffusion-form";
    }
    
    /**
     * Page de suivi des paiements diffusion
     */
    @GetMapping("/suivi-diffusion")
    public String suiviDiffusion(Model model) {
        List<DiffusionSociete> diffusions = diffusionSocieteRepository.findAll();
        
        // Calculer les totaux
        BigDecimal totalAPayer = diffusions.stream()
            .map(d -> d.getMontantTotalAPayer() != null ? d.getMontantTotalAPayer() : BigDecimal.ZERO)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
            
        BigDecimal totalPaye = diffusions.stream()
            .map(d -> d.getMontantTotalPaye() != null ? d.getMontantTotalPaye() : BigDecimal.ZERO)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
            
        BigDecimal resteAPayer = totalAPayer.subtract(totalPaye);
        
        BigDecimal pourcentagePaye = BigDecimal.ZERO;
        if (totalAPayer.compareTo(BigDecimal.ZERO) > 0) {
            pourcentagePaye = totalPaye.multiply(BigDecimal.valueOf(100))
                .divide(totalAPayer, 2, java.math.RoundingMode.HALF_UP);
        }
        
        model.addAttribute("diffusions", diffusions);
        model.addAttribute("totalAPayer", totalAPayer);
        model.addAttribute("totalPaye", totalPaye);
        model.addAttribute("resteAPayer", resteAPayer);
        model.addAttribute("pourcentagePaye", pourcentagePaye);
        
        return "chiffre-affaire/suivi-diffusion";
    }
    
    /**
     * Détail du suivi d'une diffusion société
     */
    @GetMapping("/suivi-diffusion/detail/{id}")
    public String suiviDiffusionDetail(@PathVariable Long id, Model model) {
        DiffusionSociete diffusion = diffusionSocieteRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Diffusion non trouvée"));
            
        List<DiffusionVolProgramme> volsProgrammes = 
            diffusionVolProgrammeRepository.findByDiffusionSocieteIdDiffusionSociete(id);
        
        // Récupérer les paiements diffusion
        List<PaiementDiffusion> paiementsDiffusion = 
            paiementDiffusionRepository.findByDiffusionSocieteIdDiffusionSociete(id);
        
        // Calculer le total payé via paiements diffusion (table paiement_diffusion)
        BigDecimal totalPayeDiffusion = paiementsDiffusion.stream()
            .map(p -> p.getMontantPaye() != null ? p.getMontantPaye() : BigDecimal.ZERO)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // Pour chaque vol programmé, récupérer le montant payé calculé par le trigger
        // (basé sur le pourcentage de paiement de la diffusion société)
        Map<Long, BigDecimal> montantPayeParVol = new HashMap<>();
        BigDecimal totalPayeParPourcentage = BigDecimal.ZERO;
        
        for (DiffusionVolProgramme dvp : volsProgrammes) {
            if (dvp.getVolProgramme() != null) {
                // Utiliser le montant_paye calculé automatiquement par le trigger
                BigDecimal montantPayeVol = dvp.getMontantPaye() != null ? dvp.getMontantPaye() : BigDecimal.ZERO;
                montantPayeParVol.put(dvp.getVolProgramme().getId(), montantPayeVol);
                totalPayeParPourcentage = totalPayeParPourcentage.add(montantPayeVol);
            }
        }
        
        // Calculer les totaux globaux
        BigDecimal totalMontant = volsProgrammes.stream()
            .map(d -> d.getMontantTotal() != null ? d.getMontantTotal() : BigDecimal.ZERO)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // Le total payé global est simplement le montant payé de la diffusion société
        // (qui est calculé par le trigger et correspond au total des paiements diffusion)
        BigDecimal totalPayeGlobal = diffusion.getMontantTotalPaye() != null ? 
            diffusion.getMontantTotalPaye() : BigDecimal.ZERO;
            
        BigDecimal totalReste = totalMontant.subtract(totalPayeGlobal);
        
        // Calculer le pourcentage payé
        BigDecimal pourcentagePaye = diffusion.getPourcentagePaye() != null ? 
            diffusion.getPourcentagePaye() : BigDecimal.ZERO;
        
        model.addAttribute("diffusion", diffusion);
        model.addAttribute("volsProgrammes", volsProgrammes);
        model.addAttribute("paiementsDiffusion", paiementsDiffusion);
        model.addAttribute("montantPayeParVol", montantPayeParVol);
        model.addAttribute("totalMontant", totalMontant);
        model.addAttribute("totalPayeDiffusion", totalPayeDiffusion);
        model.addAttribute("totalPayeGlobal", totalPayeGlobal);
        model.addAttribute("totalReste", totalReste);
        model.addAttribute("pourcentagePaye", pourcentagePaye);
        
        return "chiffre-affaire/suivi-diffusion-detail";
    }
}
