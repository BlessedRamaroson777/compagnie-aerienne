package com.airline.airline_app.controller;

import com.airline.airline_app.entity.Avion;
import com.airline.airline_app.service.AvionService;
import com.airline.airline_app.service.ModelAvionService;
import com.airline.airline_app.service.EtatAvionService;
import com.airline.airline_app.specification.AvionSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/avions")
@RequiredArgsConstructor
public class AvionController {
    
    private final AvionService avionService;
    private final ModelAvionService modelAvionService;
    private final EtatAvionService etatAvionService;
    
    @GetMapping
    public String list(@RequestParam(required = false) String codeAvion,
                      @RequestParam(required = false) String modele,
                      @RequestParam(required = false) Integer capaciteMin,
                      @RequestParam(required = false) Integer capaciteMax,
                      Model model) {
        
        Specification<Avion> spec = AvionSpecification.filterBy(codeAvion, modele, capaciteMin, capaciteMax);
        List<Avion> avions = avionService.findAll(spec);
        
        model.addAttribute("avions", avions);
        model.addAttribute("codeAvion", codeAvion);
        model.addAttribute("modele", modele);
        model.addAttribute("capaciteMin", capaciteMin);
        model.addAttribute("capaciteMax", capaciteMax);
        
        return "avion/list";
    }
    
    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("avion", new Avion());
        model.addAttribute("modeles", modelAvionService.findAll());
        model.addAttribute("etats", etatAvionService.findAll());
        return "avion/form";
    }
    
    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Avion avion = avionService.findById(id)
            .orElseThrow(() -> new RuntimeException("Avion non trouvé"));
        model.addAttribute("avion", avion);
        return "avion/detail";
    }
    
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Avion avion = avionService.findById(id)
            .orElseThrow(() -> new RuntimeException("Avion non trouvé"));
        model.addAttribute("avion", avion);
        model.addAttribute("modeles", modelAvionService.findAll());
        model.addAttribute("etats", etatAvionService.findAll());
        return "avion/form";
    }
    
    @PostMapping
    public String save(@ModelAttribute Avion avion, RedirectAttributes redirectAttributes) {
        try {
            if (avion.getId() == null) {
                avionService.save(avion);
                redirectAttributes.addFlashAttribute("success", "Avion créé avec succès");
            } else {
                avionService.update(avion.getId(), avion);
                redirectAttributes.addFlashAttribute("success", "Avion modifié avec succès");
            }
            return "redirect:/avions";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur: " + e.getMessage());
            return "redirect:/avions/new";
        }
    }
    
    @GetMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            avionService.delete(id);
            redirectAttributes.addFlashAttribute("success", "Avion supprimé avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur: " + e.getMessage());
        }
        return "redirect:/avions";
    }
}
