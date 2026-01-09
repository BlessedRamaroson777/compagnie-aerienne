package com.airline.airline_app.controller;

import com.airline.airline_app.entity.Equipage;
import com.airline.airline_app.entity.RoleEquipage;
import com.airline.airline_app.service.EquipageService;
import com.airline.airline_app.service.RoleEquipageService;
import com.airline.airline_app.specification.EquipageSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/equipages")
@RequiredArgsConstructor
public class EquipageController {
    
    private final EquipageService equipageService;
    private final RoleEquipageService roleEquipageService;
    
    @GetMapping
    public String list(@RequestParam(required = false) String nom,
                      @RequestParam(required = false) String prenom,
                      @RequestParam(required = false) Long roleId,
                      Model model) {
        
        RoleEquipage role = null;
        if (roleId != null) {
            role = roleEquipageService.findById(roleId).orElse(null);
        }
        
        Specification<Equipage> spec = EquipageSpecification.filterBy(nom, prenom, role);
        List<Equipage> equipages = equipageService.findAll(spec);
        
        model.addAttribute("equipages", equipages);
        model.addAttribute("roles", roleEquipageService.findAll());
        model.addAttribute("nom", nom);
        model.addAttribute("prenom", prenom);
        model.addAttribute("roleId", roleId);
        
        return "equipage/list";
    }
    
    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("equipage", new Equipage());
        model.addAttribute("roles", roleEquipageService.findAll());
        return "equipage/form";
    }
    
    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Equipage equipage = equipageService.findById(id)
            .orElseThrow(() -> new RuntimeException("Équipage non trouvé"));
        model.addAttribute("equipage", equipage);
        return "equipage/detail";
    }
    
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Equipage equipage = equipageService.findById(id)
            .orElseThrow(() -> new RuntimeException("Équipage non trouvé"));
        model.addAttribute("equipage", equipage);
        model.addAttribute("roles", roleEquipageService.findAll());
        return "equipage/form";
    }
    
    @PostMapping
    public String save(@ModelAttribute Equipage equipage, RedirectAttributes redirectAttributes) {
        try {
            if (equipage.getId() == null) {
                equipageService.save(equipage);
                redirectAttributes.addFlashAttribute("success", "Membre d'équipage créé avec succès");
            } else {
                equipageService.update(equipage.getId(), equipage);
                redirectAttributes.addFlashAttribute("success", "Membre d'équipage modifié avec succès");
            }
            return "redirect:/equipages";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur: " + e.getMessage());
            return "redirect:/equipages/new";
        }
    }
    
    @GetMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            equipageService.delete(id);
            redirectAttributes.addFlashAttribute("success", "Membre d'équipage supprimé avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur: " + e.getMessage());
        }
        return "redirect:/equipages";
    }
}
