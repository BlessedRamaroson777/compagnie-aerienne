package com.airline.airline_app.controller;

import com.airline.airline_app.entity.CategoriePassager;
import com.airline.airline_app.entity.ClasseSiege;
import com.airline.airline_app.entity.Produit;
import com.airline.airline_app.entity.ProduitCategorieSiegePassager;
import com.airline.airline_app.repository.CategoriePassagerRepository;
import com.airline.airline_app.repository.ClasseSiegeRepository;
import com.airline.airline_app.service.ProduitCategorieSiegePassagerService;
import com.airline.airline_app.service.ProduitService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/produit")
@RequiredArgsConstructor
public class ProduitController {

    private final ProduitService produitService;
    private final ProduitCategorieSiegePassagerService associationService;
    private final CategoriePassagerRepository categoriePassagerRepository;
    private final ClasseSiegeRepository classeSiegeRepository;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("produits", produitService.findAll());
        return "produit/list";
    }

    @GetMapping("/form")
    public String showForm(Model model) {
        model.addAttribute("produit", new Produit());
        return "produit/form";
    }

    @GetMapping("/form/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Produit produit = produitService.findById(id);
        model.addAttribute("produit", produit);
        model.addAttribute("historiquePrix", produitService.getHistoriquePrix(id));
        return "produit/form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Produit produit, RedirectAttributes redirectAttributes) {
        try {
            if (produit.getIdProduit() != null) {
                produitService.update(produit.getIdProduit(), produit);
                redirectAttributes.addFlashAttribute("successMessage", "Produit modifié avec succès");
            } else {
                produitService.save(produit);
                redirectAttributes.addFlashAttribute("successMessage", "Produit créé avec succès");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erreur: " + e.getMessage());
        }
        return "redirect:/produit";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            produitService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "Produit supprimé avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erreur: " + e.getMessage());
        }
        return "redirect:/produit";
    }

    // Associations Produit-Catégorie-Siège
    @GetMapping("/{id}/associations")
    public String showAssociations(@PathVariable Long id, Model model) {
        Produit produit = produitService.findById(id);
        List<ProduitCategorieSiegePassager> associations = associationService.findByProduit(id);
        List<CategoriePassager> categories = categoriePassagerRepository.findAll();
        List<ClasseSiege> classes = classeSiegeRepository.findAll();
        
        model.addAttribute("produit", produit);
        model.addAttribute("associations", associations);
        model.addAttribute("categories", categories);
        model.addAttribute("classes", classes);
        model.addAttribute("nouvelleAssociation", new ProduitCategorieSiegePassager());
        
        return "produit/associations";
    }

    @PostMapping("/{id}/associations/save")
    public String saveAssociation(@PathVariable Long id, 
                                   @RequestParam Long categorieId,
                                   @RequestParam Long classeId,
                                   RedirectAttributes redirectAttributes) {
        try {
            Produit produit = produitService.findById(id);
            CategoriePassager categorie = categoriePassagerRepository.findById(categorieId)
                    .orElseThrow(() -> new RuntimeException("Catégorie non trouvée"));
            ClasseSiege classe = classeSiegeRepository.findById(classeId)
                    .orElseThrow(() -> new RuntimeException("Classe non trouvée"));
            
            ProduitCategorieSiegePassager association = new ProduitCategorieSiegePassager();
            association.setProduit(produit);
            association.setCategoriePassager(categorie);
            association.setClasseSiege(classe);
            
            associationService.save(association);
            redirectAttributes.addFlashAttribute("successMessage", "Association créée avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erreur: " + e.getMessage());
        }
        return "redirect:/produit/" + id + "/associations";
    }

    @GetMapping("/associations/delete/{id}")
    public String deleteAssociation(@PathVariable Long id,
                                     @RequestParam Long produitId,
                                     RedirectAttributes redirectAttributes) {
        try {
            associationService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "Association supprimée avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erreur: " + e.getMessage());
        }
        return "redirect:/produit/" + produitId + "/associations";
    }
}
