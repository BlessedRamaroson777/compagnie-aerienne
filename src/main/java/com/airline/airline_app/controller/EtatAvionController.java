package com.airline.airline_app.controller;

import com.airline.airline_app.entity.EtatAvion;
import com.airline.airline_app.service.EtatAvionService;
import com.airline.airline_app.specification.EtatAvionSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/etats-avion")
@RequiredArgsConstructor
public class EtatAvionController {
    
    private final EtatAvionService etatAvionService;
    
    @GetMapping
    public String list(@RequestParam(required = false) String libelle, Model model) {
        
        Specification<EtatAvion> spec = EtatAvionSpecification.filterBy(libelle);
        List<EtatAvion> etats = etatAvionService.findAll(spec);
        
        model.addAttribute("etats", etats);
        model.addAttribute("libelle", libelle);
        
        return "etat-avion/list";
    }
    
    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("etat", new EtatAvion());
        return "etat-avion/form";
    }
    
    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        EtatAvion etat = etatAvionService.findById(id)
            .orElseThrow(() -> new RuntimeException("État d'avion non trouvé"));
        model.addAttribute("etat", etat);
        return "etat-avion/detail";
    }
    
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        EtatAvion etat = etatAvionService.findById(id)
            .orElseThrow(() -> new RuntimeException("État d'avion non trouvé"));
        model.addAttribute("etat", etat);
        return "etat-avion/form";
    }
    
    @PostMapping
    public String save(@ModelAttribute EtatAvion etat, RedirectAttributes redirectAttributes) {
        try {
            if (etat.getId() == null) {
                etatAvionService.save(etat);
                redirectAttributes.addFlashAttribute("success", "État d'avion créé avec succès");
            } else {
                etatAvionService.update(etat.getId(), etat);
                redirectAttributes.addFlashAttribute("success", "État d'avion modifié avec succès");
            }
            return "redirect:/etats-avion";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur: " + e.getMessage());
            return "redirect:/etats-avion/new";
        }
    }
    
    @GetMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            etatAvionService.delete(id);
            redirectAttributes.addFlashAttribute("success", "État d'avion supprimé avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Impossible de supprimer: " + e.getMessage());
        }
        return "redirect:/etats-avion";
    }
}
