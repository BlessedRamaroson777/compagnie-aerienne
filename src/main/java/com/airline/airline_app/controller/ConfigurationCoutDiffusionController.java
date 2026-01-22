package com.airline.airline_app.controller;

import com.airline.airline_app.entity.ConfigurationCoutDiffusion;
import com.airline.airline_app.service.ConfigurationCoutDiffusionService;
import com.airline.airline_app.service.DiffusionSocieteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/configuration-cout-diffusion")
@RequiredArgsConstructor
public class ConfigurationCoutDiffusionController {

    private final ConfigurationCoutDiffusionService configurationCoutDiffusionService;
    private final DiffusionSocieteService diffusionSocieteService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("configurations", configurationCoutDiffusionService.findAll());
        return "configuration-cout-diffusion/list";
    }

    @GetMapping("/detail/{id}")
    public String detail(@PathVariable Long id, Model model) {
        ConfigurationCoutDiffusion config = configurationCoutDiffusionService.findById(id)
            .orElseThrow(() -> new RuntimeException("Configuration non trouvée"));
        model.addAttribute("configuration", config);
        return "configuration-cout-diffusion/detail";
    }

    @GetMapping("/form")
    public String showCreateForm(Model model) {
        model.addAttribute("configuration", new ConfigurationCoutDiffusion());
        model.addAttribute("diffusions", diffusionSocieteService.findAll());
        return "configuration-cout-diffusion/form";
    }

    @GetMapping("/form/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        ConfigurationCoutDiffusion config = configurationCoutDiffusionService.findById(id)
            .orElseThrow(() -> new RuntimeException("Configuration non trouvée"));
        model.addAttribute("configuration", config);
        model.addAttribute("diffusions", diffusionSocieteService.findAll());
        return "configuration-cout-diffusion/form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute ConfigurationCoutDiffusion configuration,
                      @RequestParam Long diffusionSocieteId,
                      RedirectAttributes redirectAttributes) {
        configuration.setDiffusionSociete(diffusionSocieteService.findById(diffusionSocieteId).orElse(null));
        configurationCoutDiffusionService.save(configuration);
        redirectAttributes.addFlashAttribute("success", "Configuration enregistrée avec succès");
        return "redirect:/configuration-cout-diffusion";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        configurationCoutDiffusionService.deleteById(id);
        redirectAttributes.addFlashAttribute("success", "Configuration supprimée avec succès");
        return "redirect:/configuration-cout-diffusion";
    }
}
