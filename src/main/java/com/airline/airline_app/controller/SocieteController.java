package com.airline.airline_app.controller;

import com.airline.airline_app.entity.Societe;
import com.airline.airline_app.service.SocieteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/societe")
@RequiredArgsConstructor
public class SocieteController {

    private final SocieteService societeService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("societes", societeService.findAll());
        return "societe/list";
    }

    @GetMapping("/detail/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Societe societe = societeService.findById(id)
            .orElseThrow(() -> new RuntimeException("Société non trouvée"));
        model.addAttribute("societe", societe);
        return "societe/detail";
    }

    @GetMapping("/form")
    public String showCreateForm(Model model) {
        model.addAttribute("societe", new Societe());
        return "societe/form";
    }

    @GetMapping("/form/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Societe societe = societeService.findById(id)
            .orElseThrow(() -> new RuntimeException("Société non trouvée"));
        model.addAttribute("societe", societe);
        return "societe/form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Societe societe, RedirectAttributes redirectAttributes) {
        societeService.save(societe);
        redirectAttributes.addFlashAttribute("success", "Société enregistrée avec succès");
        return "redirect:/societe";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        societeService.deleteById(id);
        redirectAttributes.addFlashAttribute("success", "Société supprimée avec succès");
        return "redirect:/societe";
    }
}
