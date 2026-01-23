package com.airline.airline_app.controller;

import com.airline.airline_app.entity.DiffusionSociete;
import com.airline.airline_app.entity.PaiementDiffusion;
import com.airline.airline_app.service.DiffusionSocieteService;
import com.airline.airline_app.service.PaiementDiffusionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;

@Controller
@RequestMapping("/paiement-diffusion")
public class PaiementDiffusionController {

    @Autowired
    private PaiementDiffusionService paiementDiffusionService;

    @Autowired
    private DiffusionSocieteService diffusionSocieteService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("paiements", paiementDiffusionService.findAll());
        return "paiement-diffusion/list";
    }

    @GetMapping("/form")
    public String showForm(@RequestParam(required = false) Long diffusionSocieteId, Model model) {
        PaiementDiffusion paiement = new PaiementDiffusion();
        
        if (diffusionSocieteId != null) {
            DiffusionSociete diffusion = diffusionSocieteService.findById(diffusionSocieteId)
                .orElseThrow(() -> new RuntimeException("Diffusion non trouvée"));
            paiement.setDiffusionSociete(diffusion);
            
            // Calculer les informations de paiement
            BigDecimal montantTotal = diffusionSocieteService.getMontantTotal(diffusionSocieteId);
            BigDecimal montantPaye = diffusionSocieteService.getMontantPaye(diffusionSocieteId);
            BigDecimal resteAPayer = diffusionSocieteService.getResteAPayer(diffusionSocieteId);
            
            model.addAttribute("montantTotal", montantTotal);
            model.addAttribute("montantPaye", montantPaye);
            model.addAttribute("resteAPayer", resteAPayer);
        }
        
        model.addAttribute("paiement", paiement);
        model.addAttribute("diffusions", diffusionSocieteService.findAll());
        return "paiement-diffusion/form";
    }

    @GetMapping("/form/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        PaiementDiffusion paiement = paiementDiffusionService.findById(id)
            .orElseThrow(() -> new RuntimeException("Paiement non trouvé"));
        
        Long diffusionId = paiement.getDiffusionSociete().getIdDiffusionSociete();
        BigDecimal montantTotal = diffusionSocieteService.getMontantTotal(diffusionId);
        BigDecimal montantPaye = diffusionSocieteService.getMontantPaye(diffusionId);
        BigDecimal resteAPayer = diffusionSocieteService.getResteAPayer(diffusionId);
        
        model.addAttribute("paiement", paiement);
        model.addAttribute("diffusions", diffusionSocieteService.findAll());
        model.addAttribute("montantTotal", montantTotal);
        model.addAttribute("montantPaye", montantPaye);
        model.addAttribute("resteAPayer", resteAPayer);
        return "paiement-diffusion/form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute PaiementDiffusion paiement, RedirectAttributes redirectAttributes) {
        try {
            paiementDiffusionService.save(paiement);
            redirectAttributes.addFlashAttribute("successMessage", "Paiement enregistré avec succès");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/paiement-diffusion/form?diffusionSocieteId=" + 
                paiement.getDiffusionSociete().getIdDiffusionSociete();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erreur lors de l'enregistrement : " + e.getMessage());
            return "redirect:/paiement-diffusion/form";
        }
        return "redirect:/paiement-diffusion";
    }

    @GetMapping("/detail/{id}")
    public String detail(@PathVariable Long id, Model model) {
        PaiementDiffusion paiement = paiementDiffusionService.findById(id)
            .orElseThrow(() -> new RuntimeException("Paiement non trouvé"));
        model.addAttribute("paiement", paiement);
        return "paiement-diffusion/detail";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            paiementDiffusionService.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Paiement supprimé avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erreur lors de la suppression : " + e.getMessage());
        }
        return "redirect:/paiement-diffusion";
    }
}
