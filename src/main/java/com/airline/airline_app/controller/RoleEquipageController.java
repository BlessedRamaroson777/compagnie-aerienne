package com.airline.airline_app.controller;

import com.airline.airline_app.entity.RoleEquipage;
import com.airline.airline_app.service.RoleEquipageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/roles-equipage")
@RequiredArgsConstructor
public class RoleEquipageController {
    
    private final RoleEquipageService roleEquipageService;
    
    @GetMapping
    public String list(Model model) {
        List<RoleEquipage> roles = roleEquipageService.findAll();
        model.addAttribute("roleEquipages", roles);
        return "role-equipage/list";
    }
    
    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("role", new RoleEquipage());
        return "role-equipage/form";
    }
    
    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        RoleEquipage role = roleEquipageService.findById(id)
            .orElseThrow(() -> new RuntimeException("Rôle d'équipage non trouvé"));
        model.addAttribute("role", role);
        return "role-equipage/detail";
    }
    
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        RoleEquipage role = roleEquipageService.findById(id)
            .orElseThrow(() -> new RuntimeException("Rôle d'équipage non trouvé"));
        model.addAttribute("role", role);
        return "role-equipage/form";
    }
    
    @PostMapping
    public String save(@ModelAttribute RoleEquipage role, RedirectAttributes redirectAttributes) {
        try {
            if (role.getId() == null) {
                roleEquipageService.save(role);
                redirectAttributes.addFlashAttribute("success", "Rôle d'équipage créé avec succès");
            } else {
                roleEquipageService.update(role.getId(), role);
                redirectAttributes.addFlashAttribute("success", "Rôle d'équipage modifié avec succès");
            }
            return "redirect:/roles-equipage";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur: " + e.getMessage());
            return "redirect:/roles-equipage/new";
        }
    }
    
    @GetMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            roleEquipageService.delete(id);
            redirectAttributes.addFlashAttribute("success", "Rôle d'équipage supprimé avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Impossible de supprimer: " + e.getMessage());
        }
        return "redirect:/roles-equipage";
    }
}
