package com.airline.airline_app.controller;

import com.airline.airline_app.dto.ChiffreAffaireDTO;
import com.airline.airline_app.service.ChiffreAffaireService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/chiffre-affaire")
@RequiredArgsConstructor
public class ChiffreAffaireController {

    private final ChiffreAffaireService chiffreAffaireService;

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
}
