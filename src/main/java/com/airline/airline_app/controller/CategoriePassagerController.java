package com.airline.airline_app.controller;

import com.airline.airline_app.entity.CategoriePassager;
import com.airline.airline_app.repository.CategoriePassagerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/categories-passager")
public class CategoriePassagerController {
    
    @Autowired
    private CategoriePassagerRepository categoriePassagerRepository;
    
    @GetMapping
    public String list(Model model) {
        model.addAttribute("categories", categoriePassagerRepository.findAll());
        return "categorie-passager/list";
    }
    
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("categorie", new CategoriePassager());
        return "categorie-passager/form";
    }
    
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        return categoriePassagerRepository.findById(id)
            .map(categorie -> {
                model.addAttribute("categorie", categorie);
                return "categorie-passager/form";
            })
            .orElseGet(() -> {
                redirectAttributes.addFlashAttribute("error", "Catégorie de passager non trouvée");
                return "redirect:/categories-passager";
            });
    }
    
    @PostMapping
    public String save(@ModelAttribute CategoriePassager categorie, RedirectAttributes redirectAttributes) {
        try {
            categoriePassagerRepository.save(categorie);
            redirectAttributes.addFlashAttribute("success", "Catégorie de passager enregistrée avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de l'enregistrement: " + e.getMessage());
        }
        return "redirect:/categories-passager";
    }
    
    @GetMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            categoriePassagerRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Catégorie de passager supprimée avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la suppression: " + e.getMessage());
        }
        return "redirect:/categories-passager";
    }
}
