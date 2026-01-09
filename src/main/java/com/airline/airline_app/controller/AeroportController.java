package com.airline.airline_app.controller;

import com.airline.airline_app.entity.Aeroport;
import com.airline.airline_app.service.AeroportService;
import com.airline.airline_app.specification.AeroportSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/aeroports")
@RequiredArgsConstructor
public class AeroportController {
    
    private final AeroportService aeroportService;
    
    @GetMapping
    public String list(@RequestParam(required = false) String codeAeroport,
                      @RequestParam(required = false) String nom,
                      @RequestParam(required = false) String ville,
                      @RequestParam(required = false) String pays,
                      Model model) {
        
        Specification<Aeroport> spec = AeroportSpecification.filterBy(codeAeroport, nom, ville, pays);
        List<Aeroport> aeroports = aeroportService.findAll(spec);
        
        model.addAttribute("aeroports", aeroports);
        model.addAttribute("codeAeroport", codeAeroport);
        model.addAttribute("nom", nom);
        model.addAttribute("ville", ville);
        model.addAttribute("pays", pays);
        
        return "aeroport/list";
    }
    
    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("aeroport", new Aeroport());
        return "aeroport/form";
    }
    
    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Aeroport aeroport = aeroportService.findById(id)
            .orElseThrow(() -> new RuntimeException("Aéroport non trouvé"));
        model.addAttribute("aeroport", aeroport);
        return "aeroport/detail";
    }
    
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Aeroport aeroport = aeroportService.findById(id)
            .orElseThrow(() -> new RuntimeException("Aéroport non trouvé"));
        model.addAttribute("aeroport", aeroport);
        return "aeroport/form";
    }
    
    @PostMapping
    public String save(@ModelAttribute Aeroport aeroport, RedirectAttributes redirectAttributes) {
        try {
            if (aeroport.getId() == null) {
                aeroportService.save(aeroport);
                redirectAttributes.addFlashAttribute("success", "Aéroport créé avec succès");
            } else {
                aeroportService.update(aeroport.getId(), aeroport);
                redirectAttributes.addFlashAttribute("success", "Aéroport modifié avec succès");
            }
            return "redirect:/aeroports";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur: " + e.getMessage());
            return "redirect:/aeroports/new";
        }
    }
    
    @GetMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            aeroportService.delete(id);
            redirectAttributes.addFlashAttribute("success", "Aéroport supprimé avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Impossible de supprimer: " + e.getMessage());
        }
        return "redirect:/aeroports";
    }
}
