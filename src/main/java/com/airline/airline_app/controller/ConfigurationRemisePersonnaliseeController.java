package com.airline.airline_app.controller;

import com.airline.airline_app.entity.*;
import com.airline.airline_app.repository.*;
import com.airline.airline_app.service.ConfigurationRemisePersonnaliseeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.*;

@Controller
@RequestMapping("/configurations-remise-personnalisee")
@RequiredArgsConstructor
public class ConfigurationRemisePersonnaliseeController {
    
    private final ConfigurationRemisePersonnaliseeService service;
    private final VolProgrammeRepository volProgrammeRepository;
    private final CategoriePassagerRepository categoriePassagerRepository;
    private final ClasseSiegeRepository classeSiegeRepository;
    private final PrixVolRepository prixVolRepository;
    private final AssocConfigurationRemisePersonnaliseeRepository assocRepository;
    
    /**
     * Liste toutes les configurations
     */
    @GetMapping
    public String list(@RequestParam(required = false) Long volProgrammeId, Model model) {
        List<ConfigurationRemisePersonnalisee> configurations;
        
        if (volProgrammeId != null) {
            configurations = service.findConfigsForVolProgramme(volProgrammeId);
            volProgrammeRepository.findById(volProgrammeId)
                .ifPresent(vp -> model.addAttribute("selectedVolProgramme", vp));
        } else {
            configurations = service.findAll();
        }
        
        model.addAttribute("configurations", configurations);
        model.addAttribute("volsProgrammes", volProgrammeRepository.findAll());
        model.addAttribute("volProgrammeId", volProgrammeId);
        
        return "configuration-remise/list";
    }
    
    /**
     * Affiche le formulaire de création
     */
    @GetMapping("/new")
    public String showCreateForm(@RequestParam(required = false) Long volProgrammeId, Model model) {
        ConfigurationRemisePersonnalisee config = new ConfigurationRemisePersonnalisee();
        
        if (volProgrammeId != null) {
            volProgrammeRepository.findById(volProgrammeId)
                .ifPresent(config::setVolProgramme);
        }
        
        model.addAttribute("config", config);
        model.addAttribute("volsProgrammes", volProgrammeRepository.findAll());
        model.addAttribute("categories", categoriePassagerRepository.findAll());
        model.addAttribute("classes", classeSiegeRepository.findAll());
        model.addAttribute("isNew", true);
        
        return "configuration-remise/form";
    }
    
    /**
     * Affiche le formulaire d'édition
     */
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        return service.findById(id)
            .map(config -> {
                model.addAttribute("config", config);
                model.addAttribute("volsProgrammes", volProgrammeRepository.findAll());
                model.addAttribute("categories", categoriePassagerRepository.findAll());
                model.addAttribute("classes", classeSiegeRepository.findAll());
                model.addAttribute("associations", service.findAssociationsByConfig(id));
                model.addAttribute("isNew", false);
                
                // Prix par classe pour ce vol programmé
                if (config.getVolProgramme() != null) {
                    List<PrixVol> prixVols = prixVolRepository.findByVolProgrammeId(config.getVolProgramme().getId());
                    model.addAttribute("prixVols", prixVols);
                }
                
                return "configuration-remise/form";
            })
            .orElseGet(() -> {
                redirectAttributes.addFlashAttribute("error", "Configuration non trouvée");
                return "redirect:/configurations-remise-personnalisee";
            });
    }
    
    /**
     * Affiche le détail d'une configuration
     */
    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        return service.findById(id)
            .map(config -> {
                model.addAttribute("config", config);
                model.addAttribute("associations", service.findAssociationsByConfig(id));
                
                // Prix par classe pour ce vol programmé
                if (config.getVolProgramme() != null) {
                    List<PrixVol> prixVols = prixVolRepository.findByVolProgrammeId(config.getVolProgramme().getId());
                    model.addAttribute("prixVols", prixVols);
                    
                    // Calculer les prix finaux pour chaque association
                    List<AssociationAvecPrix> associationsAvecPrix = new ArrayList<>();
                    for (AssocConfigurationRemisePersonnalisee assoc : config.getAssociations()) {
                        PrixVol prixVol = prixVols.stream()
                            .filter(pv -> pv.getClasseSiege().getId().equals(assoc.getClasseSiege().getId()))
                            .findFirst()
                            .orElse(null);
                        
                        BigDecimal prixReference = prixVol != null ? prixVol.getPrix() : BigDecimal.ZERO;
                        BigDecimal prixFinal = assoc.calculerPrixFinal(prixReference);
                        BigDecimal montantRemise = assoc.calculerMontantRemise(prixReference);
                        
                        associationsAvecPrix.add(new AssociationAvecPrix(assoc, prixReference, prixFinal, montantRemise));
                    }
                    model.addAttribute("associationsAvecPrix", associationsAvecPrix);
                }
                
                return "configuration-remise/detail";
            })
            .orElseGet(() -> {
                redirectAttributes.addFlashAttribute("error", "Configuration non trouvée");
                return "redirect:/configurations-remise-personnalisee";
            });
    }
    
    /**
     * Sauvegarde une configuration (création ou mise à jour)
     */
    @PostMapping
    public String save(@RequestParam String nom,
                      @RequestParam(required = false) String description,
                      @RequestParam Long volProgrammeId,
                      @RequestParam(required = false) Boolean actif,
                      @RequestParam(required = false) Long id,
                      RedirectAttributes redirectAttributes) {
        try {
            VolProgramme volProgramme = volProgrammeRepository.findById(volProgrammeId)
                .orElseThrow(() -> new RuntimeException("Vol programmé non trouvé"));
            
            ConfigurationRemisePersonnalisee config;
            if (id != null) {
                config = service.findById(id)
                    .orElseThrow(() -> new RuntimeException("Configuration non trouvée"));
            } else {
                config = new ConfigurationRemisePersonnalisee();
            }
            
            config.setNom(nom);
            config.setDescription(description);
            config.setVolProgramme(volProgramme);
            config.setActif(actif != null && actif);
            
            ConfigurationRemisePersonnalisee savedConfig = service.save(config);
            
            redirectAttributes.addFlashAttribute("success", "Configuration enregistrée avec succès");
            return "redirect:/configurations-remise-personnalisee/" + savedConfig.getId() + "/edit";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur: " + e.getMessage());
            return "redirect:/configurations-remise-personnalisee/new";
        }
    }
    
    /**
     * Ajoute une association (catégorie + classe + pourcentage)
     */
    @PostMapping("/{configId}/associations")
    public String addAssociation(@PathVariable Long configId,
                                @RequestParam Long categoriePassagerId,
                                @RequestParam Long classeSiegeId,
                                @RequestParam BigDecimal pourcentageRemise,
                                RedirectAttributes redirectAttributes) {
        try {
            ConfigurationRemisePersonnalisee config = service.findById(configId)
                .orElseThrow(() -> new RuntimeException("Configuration non trouvée"));
            
            CategoriePassager categorie = categoriePassagerRepository.findById(categoriePassagerId)
                .orElseThrow(() -> new RuntimeException("Catégorie non trouvée"));
            
            ClasseSiege classe = classeSiegeRepository.findById(classeSiegeId)
                .orElseThrow(() -> new RuntimeException("Classe non trouvée"));
            
            // Vérifier si l'association existe déjà
            Optional<AssocConfigurationRemisePersonnalisee> existingAssoc = 
                assocRepository.findByConfigurationRemisePersonnalisee_IdAndCategoriePassager_IdCategoriePassagerAndClasseSiege_Id(
                    configId, categoriePassagerId, classeSiegeId);
            
            if (existingAssoc.isPresent()) {
                // Mettre à jour l'association existante
                AssocConfigurationRemisePersonnalisee assoc = existingAssoc.get();
                assoc.setPourcentageRemise(pourcentageRemise);
                service.saveAssociation(assoc);
                redirectAttributes.addFlashAttribute("success", "Association mise à jour");
            } else {
                // Créer une nouvelle association
                AssocConfigurationRemisePersonnalisee assoc = new AssocConfigurationRemisePersonnalisee();
                assoc.setConfigurationRemisePersonnalisee(config);
                assoc.setCategoriePassager(categorie);
                assoc.setClasseSiege(classe);
                assoc.setPourcentageRemise(pourcentageRemise);
                service.saveAssociation(assoc);
                redirectAttributes.addFlashAttribute("success", "Association ajoutée");
            }
            
            // Synchroniser les remises si la config est active
            if (config.getActif()) {
                Optional<PrixVol> prixVolOpt = prixVolRepository.findByVolProgrammeIdAndClasseSiegeId(
                    config.getVolProgramme().getId(), classeSiegeId);
                
                if (prixVolOpt.isPresent()) {
                    service.synchroniserRemisesApresChangementPrix(
                        config.getVolProgramme().getId(), 
                        classeSiegeId, 
                        prixVolOpt.get().getPrix());
                }
            }
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur: " + e.getMessage());
        }
        return "redirect:/configurations-remise-personnalisee/" + configId + "/edit";
    }
    
    /**
     * Supprime une association
     */
    @GetMapping("/associations/{assocId}/delete")
    public String deleteAssociation(@PathVariable Long assocId, 
                                   @RequestParam Long configId,
                                   RedirectAttributes redirectAttributes) {
        try {
            service.deleteAssociation(assocId);
            redirectAttributes.addFlashAttribute("success", "Association supprimée");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur: " + e.getMessage());
        }
        return "redirect:/configurations-remise-personnalisee/" + configId + "/edit";
    }
    
    /**
     * Toggle le statut actif/inactif
     */
    @PostMapping("/{id}/toggle-status")
    public String toggleStatus(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            service.findById(id).ifPresent(config -> {
                if (config.getActif()) {
                    service.deactivate(id);
                    redirectAttributes.addFlashAttribute("success", "Configuration désactivée");
                } else {
                    service.activate(id);
                    redirectAttributes.addFlashAttribute("success", "Configuration activée");
                    
                    // Synchroniser toutes les remises pour cette configuration
                    if (config.getVolProgramme() != null) {
                        List<PrixVol> prixVols = prixVolRepository.findByVolProgrammeId(config.getVolProgramme().getId());
                        for (PrixVol prixVol : prixVols) {
                            service.synchroniserRemisesApresChangementPrix(
                                config.getVolProgramme().getId(),
                                prixVol.getClasseSiege().getId(),
                                prixVol.getPrix());
                        }
                    }
                }
            });
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur: " + e.getMessage());
        }
        return "redirect:/configurations-remise-personnalisee";
    }
    
    /**
     * Supprime une configuration
     */
    @GetMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            service.delete(id);
            redirectAttributes.addFlashAttribute("success", "Configuration supprimée avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la suppression: " + e.getMessage());
        }
        return "redirect:/configurations-remise-personnalisee";
    }
    
    /**
     * Classe interne pour afficher les associations avec les prix calculés
     */
    public static class AssociationAvecPrix {
        private final AssocConfigurationRemisePersonnalisee association;
        private final BigDecimal prixReference;
        private final BigDecimal prixFinal;
        private final BigDecimal montantRemise;
        
        public AssociationAvecPrix(AssocConfigurationRemisePersonnalisee association, 
                                   BigDecimal prixReference, 
                                   BigDecimal prixFinal, 
                                   BigDecimal montantRemise) {
            this.association = association;
            this.prixReference = prixReference;
            this.prixFinal = prixFinal;
            this.montantRemise = montantRemise;
        }
        
        public AssocConfigurationRemisePersonnalisee getAssociation() { return association; }
        public BigDecimal getPrixReference() { return prixReference; }
        public BigDecimal getPrixFinal() { return prixFinal; }
        public BigDecimal getMontantRemise() { return montantRemise; }
    }
}
