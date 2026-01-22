package com.airline.airline_app.controller;

import com.airline.airline_app.entity.DiffusionSociete;
import com.airline.airline_app.service.DiffusionSocieteService;
import com.airline.airline_app.service.SocieteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/diffusion-societe")
@RequiredArgsConstructor
public class DiffusionSocieteController {

    private final DiffusionSocieteService diffusionSocieteService;
    private final SocieteService societeService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("diffusions", diffusionSocieteService.findAll());
        return "diffusion-societe/list";
    }

    @GetMapping("/detail/{id}")
    public String detail(@PathVariable Long id, Model model) {
        DiffusionSociete diffusion = diffusionSocieteService.findById(id)
            .orElseThrow(() -> new RuntimeException("Diffusion non trouvée"));
        model.addAttribute("diffusion", diffusion);
        return "diffusion-societe/detail";
    }

    @GetMapping("/form")
    public String showCreateForm(Model model) {
        model.addAttribute("diffusion", new DiffusionSociete());
        model.addAttribute("societes", societeService.findAll());
        return "diffusion-societe/form";
    }

    @GetMapping("/form/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        DiffusionSociete diffusion = diffusionSocieteService.findById(id)
            .orElseThrow(() -> new RuntimeException("Diffusion non trouvée"));
        model.addAttribute("diffusion", diffusion);
        model.addAttribute("societes", societeService.findAll());
        return "diffusion-societe/form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute DiffusionSociete diffusion, 
                      @RequestParam Long societeId,
                      RedirectAttributes redirectAttributes) {
        if (diffusion.getDate() == null) {
            diffusion.setDate(LocalDateTime.now());
        }
        diffusion.setSociete(societeService.findById(societeId).orElse(null));
        diffusionSocieteService.save(diffusion);
        redirectAttributes.addFlashAttribute("success", "Diffusion enregistrée avec succès");
        return "redirect:/diffusion-societe";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        diffusionSocieteService.deleteById(id);
        redirectAttributes.addFlashAttribute("success", "Diffusion supprimée avec succès");
        return "redirect:/diffusion-societe";
    }
}
