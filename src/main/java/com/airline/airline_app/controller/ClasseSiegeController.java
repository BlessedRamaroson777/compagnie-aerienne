package com.airline.airline_app.controller;

import com.airline.airline_app.entity.ClasseSiege;
import com.airline.airline_app.service.ClasseSiegeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/classes-siege")
@RequiredArgsConstructor
public class ClasseSiegeController {
    
    private final ClasseSiegeService classeSiegeService;
    
    @GetMapping
    public String list(Model model) {
        List<ClasseSiege> classes = classeSiegeService.findAll();
        model.addAttribute("classes", classes);
        return "classe-siege/list";
    }
    
    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("classeSiege", new ClasseSiege());
        return "classe-siege/form";
    }
    
    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        ClasseSiege classeSiege = classeSiegeService.findById(id)
            .orElseThrow(() -> new RuntimeException("Classe de siège non trouvée"));
        model.addAttribute("classeSiege", classeSiege);
        return "classe-siege/detail";
    }
    
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        ClasseSiege classeSiege = classeSiegeService.findById(id)
            .orElseThrow(() -> new RuntimeException("Classe de siège non trouvée"));
        model.addAttribute("classeSiege", classeSiege);
        return "classe-siege/form";
    }
    
    @PostMapping
    public String save(@ModelAttribute ClasseSiege classeSiege, RedirectAttributes redirectAttributes) {
        try {
            if (classeSiege.getId() == null) {
                classeSiegeService.save(classeSiege);
                redirectAttributes.addFlashAttribute("success", "Classe de siège créée avec succès");
            } else {
                classeSiegeService.update(classeSiege.getId(), classeSiege);
                redirectAttributes.addFlashAttribute("success", "Classe de siège modifiée avec succès");
            }
            return "redirect:/classes-siege";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur: " + e.getMessage());
            return "redirect:/classes-siege/new";
        }
    }
    
    @GetMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            classeSiegeService.delete(id);
            redirectAttributes.addFlashAttribute("success", "Classe de siège supprimée avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Impossible de supprimer: " + e.getMessage());
        }
        return "redirect:/classes-siege";
    }
}
