package com.airline.airline_app.controller;

import com.airline.airline_app.entity.ModelAvion;
import com.airline.airline_app.service.ModelAvionService;
import com.airline.airline_app.specification.ModelAvionSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/modeles-avion")
@RequiredArgsConstructor
public class ModelAvionController {
    
    private final ModelAvionService modelAvionService;
    
    @GetMapping
    public String list(@RequestParam(required = false) String designation,
                      @RequestParam(required = false) String fabricant,
                      @RequestParam(required = false) Integer capaciteMin,
                      @RequestParam(required = false) Integer capaciteMax,
                      Model model) {
        
        Specification<ModelAvion> spec = ModelAvionSpecification.filterBy(designation, fabricant, capaciteMin, capaciteMax);
        List<ModelAvion> modeles = modelAvionService.findAll(spec);
        
        model.addAttribute("modeles", modeles);
        model.addAttribute("designation", designation);
        model.addAttribute("fabricant", fabricant);
        model.addAttribute("capaciteMin", capaciteMin);
        model.addAttribute("capaciteMax", capaciteMax);
        
        return "model-avion/list";
    }
    
    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("modele", new ModelAvion());
        return "model-avion/form";
    }
    
    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        ModelAvion modele = modelAvionService.findById(id)
            .orElseThrow(() -> new RuntimeException("Modèle d'avion non trouvé"));
        model.addAttribute("modele", modele);
        return "model-avion/detail";
    }
    
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        ModelAvion modele = modelAvionService.findById(id)
            .orElseThrow(() -> new RuntimeException("Modèle d'avion non trouvé"));
        model.addAttribute("modele", modele);
        return "model-avion/form";
    }
    
    @PostMapping
    public String save(@ModelAttribute ModelAvion modele, RedirectAttributes redirectAttributes) {
        try {
            if (modele.getId() == null) {
                modelAvionService.save(modele);
                redirectAttributes.addFlashAttribute("success", "Modèle d'avion créé avec succès");
            } else {
                modelAvionService.update(modele.getId(), modele);
                redirectAttributes.addFlashAttribute("success", "Modèle d'avion modifié avec succès");
            }
            return "redirect:/modeles-avion";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur: " + e.getMessage());
            return "redirect:/modeles-avion/new";
        }
    }
    
    @GetMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            modelAvionService.delete(id);
            redirectAttributes.addFlashAttribute("success", "Modèle d'avion supprimé avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Impossible de supprimer: " + e.getMessage());
        }
        return "redirect:/modeles-avion";
    }
}
