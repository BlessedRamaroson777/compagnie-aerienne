package com.airline.airline_app.controller;

import com.airline.airline_app.entity.Produit;
import com.airline.airline_app.entity.VenteProduit;
import com.airline.airline_app.entity.VolProgramme;
import com.airline.airline_app.repository.VolProgrammeRepository;
import com.airline.airline_app.service.ProduitService;
import com.airline.airline_app.service.VenteProduitService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/vente-produit")
@RequiredArgsConstructor
public class VenteProduitController {

    private final VenteProduitService venteProduitService;
    private final ProduitService produitService;
    private final VolProgrammeRepository volProgrammeRepository;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("ventes", venteProduitService.findAll());
        return "vente-produit/list";
    }

    @GetMapping("/form")
    public String showForm(Model model) {
        model.addAttribute("vente", new VenteProduit());
        model.addAttribute("produits", produitService.findAll());
        model.addAttribute("volsProgrammes", volProgrammeRepository.findAll());
        return "vente-produit/form";
    }

    @GetMapping("/form/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        VenteProduit vente = venteProduitService.findById(id);
        model.addAttribute("vente", vente);
        model.addAttribute("produits", produitService.findAll());
        model.addAttribute("volsProgrammes", volProgrammeRepository.findAll());
        return "vente-produit/form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute VenteProduit vente,
                       @RequestParam Long produitId,
                       @RequestParam Long volProgrammeId,
                       RedirectAttributes redirectAttributes) {
        try {
            Produit produit = produitService.findById(produitId);
            VolProgramme volProgramme = volProgrammeRepository.findById(volProgrammeId)
                    .orElseThrow(() -> new RuntimeException("Vol programmé non trouvé"));
            
            vente.setProduit(produit);
            vente.setVolProgramme(volProgramme);
            
            venteProduitService.save(vente);
            redirectAttributes.addFlashAttribute("successMessage", "Vente enregistrée avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erreur: " + e.getMessage());
        }
        return "redirect:/vente-produit";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            venteProduitService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "Vente supprimée avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erreur: " + e.getMessage());
        }
        return "redirect:/vente-produit";
    }

    @GetMapping("/vol-programme/{id}")
    public String ventesParVol(@PathVariable Long id, Model model) {
        VolProgramme vol = volProgrammeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vol non trouvé"));
        model.addAttribute("vol", vol);
        model.addAttribute("ventes", venteProduitService.findByVolProgramme(id));
        model.addAttribute("caTotal", venteProduitService.calculateCAVenteByVolProgramme(id));
        return "vente-produit/par-vol";
    }
}
