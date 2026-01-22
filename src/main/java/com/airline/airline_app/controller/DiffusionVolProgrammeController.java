package com.airline.airline_app.controller;

import com.airline.airline_app.entity.DiffusionVolProgramme;
import com.airline.airline_app.service.DiffusionVolProgrammeService;
import com.airline.airline_app.service.DiffusionSocieteService;
import com.airline.airline_app.service.VolProgrammeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/diffusion-vol-programme")
@RequiredArgsConstructor
public class DiffusionVolProgrammeController {

    private final DiffusionVolProgrammeService diffusionVolProgrammeService;
    private final DiffusionSocieteService diffusionSocieteService;
    private final VolProgrammeService volProgrammeService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("diffusionsVol", diffusionVolProgrammeService.findAll());
        return "diffusion-vol-programme/list";
    }

    @GetMapping("/detail/{id}")
    public String detail(@PathVariable Long id, Model model) {
        DiffusionVolProgramme diffusion = diffusionVolProgrammeService.findById(id)
            .orElseThrow(() -> new RuntimeException("Diffusion Vol Programme non trouvée"));
        model.addAttribute("diffusion", diffusion);
        return "diffusion-vol-programme/detail";
    }

    @GetMapping("/form")
    public String showCreateForm(Model model) {
        model.addAttribute("diffusion", new DiffusionVolProgramme());
        model.addAttribute("diffusionsSociete", diffusionSocieteService.findAll());
        model.addAttribute("volsProgrammes", volProgrammeService.findAll());
        return "diffusion-vol-programme/form";
    }

    @GetMapping("/form/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        DiffusionVolProgramme diffusion = diffusionVolProgrammeService.findById(id)
            .orElseThrow(() -> new RuntimeException("Diffusion Vol Programme non trouvée"));
        model.addAttribute("diffusion", diffusion);
        model.addAttribute("diffusionsSociete", diffusionSocieteService.findAll());
        model.addAttribute("volsProgrammes", volProgrammeService.findAll());
        return "diffusion-vol-programme/form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute DiffusionVolProgramme diffusion,
                      @RequestParam Long diffusionSocieteId,
                      @RequestParam Long volProgrammeId,
                      RedirectAttributes redirectAttributes) {
        diffusion.setDiffusionSociete(diffusionSocieteService.findById(diffusionSocieteId).orElse(null));
        diffusion.setVolProgramme(volProgrammeService.findById(volProgrammeId).orElse(null));
        diffusionVolProgrammeService.save(diffusion);
        redirectAttributes.addFlashAttribute("success", "Diffusion Vol Programme enregistrée avec succès");
        return "redirect:/diffusion-vol-programme";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        diffusionVolProgrammeService.deleteById(id);
        redirectAttributes.addFlashAttribute("success", "Diffusion Vol Programme supprimée avec succès");
        return "redirect:/diffusion-vol-programme";
    }
}
