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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/configurations-remise-personnalisee")
@RequiredArgsConstructor
public class ConfigurationRemisePersonnaliseeController {
    
    private final ConfigurationRemisePersonnaliseeService service;
    private final VolProgrammeRepository volProgrammeRepository;
    private final CategoriePassagerRepository categoriePassagerRepository;
    private final ClasseSiegeRepository classeSiegeRepository;
    private final AssocConfigurationRemisePersonnaliseeRepository assocRepository;
    private final VolProgrammeClassePrixRepository volProgrammeClassePrixRepository;
    private final RemiseCategoriePlaceRepository remiseCategoriePlaceRepository;
    
    /**
     * Liste toutes les configurations
     */
    @GetMapping
    public String list(Model model) {
        List<ConfigurationRemisePersonnalisee> configurations = service.findAll();
        model.addAttribute("configurations", configurations);
        return "configurations-remise-personnalisee/list";
    }
    
    /**
     * Affiche le formulaire de création
     */
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("configuration", new ConfigurationRemisePersonnalisee());
        model.addAttribute("volsProgrammes", volProgrammeRepository.findAll());
        model.addAttribute("categoriesPassager", categoriePassagerRepository.findAll());
        model.addAttribute("classesSiege", classeSiegeRepository.findAll());
        
        // Vérifier si au moins un prix existe dans vol_programme_classe_prix
        long nbPrix = volProgrammeClassePrixRepository.count();
        if (nbPrix == 0) {
            model.addAttribute("warning", 
                "⚠️ ATTENTION: Aucun prix n'est configuré ! Vous devez d'abord créer des prix pour les vols via " +
                "<a href='/prix-vol/new' class='alert-link'>Gestion des Prix</a> avant de créer des remises.");
        }
        
        return "configurations-remise-personnalisee/form";
    }
    
    /**
     * Crée une nouvelle configuration avec ses associations
     */
    @PostMapping("/save")
    public String save(
            @RequestParam String nom,
            @RequestParam(required = false) String description,
            @RequestParam Long volProgrammeId,
            @RequestParam(required = false) Boolean actif,
            @RequestParam(required = false) Boolean toutesLesClasses,
            @RequestParam(required = false) List<Long> classeSiegeIds,
            @RequestParam Map<String, String> allParams,
            RedirectAttributes redirectAttributes) {
        
        try {
            Optional<VolProgramme> volProgrammeOpt = volProgrammeRepository.findById(volProgrammeId);
            if (volProgrammeOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Vol programmé non trouvé");
                return "redirect:/configurations-remise-personnalisee/new";
            }
            
            ConfigurationRemisePersonnalisee config = new ConfigurationRemisePersonnalisee();
            config.setNom(nom);
            config.setDescription(description);
            config.setVolProgramme(volProgrammeOpt.get());
            config.setActif(actif != null ? actif : true);
            config.setAppliquerToutesClasses(toutesLesClasses != null ? toutesLesClasses : false);
            
            // Sauvegarder d'abord la configuration
            config = service.save(config);
            
            // Récupérer toutes les classes si "toutes les classes" est coché
            List<Long> classesAUtiliser = classeSiegeIds;
            if (toutesLesClasses != null && toutesLesClasses) {
                classesAUtiliser = classeSiegeRepository.findAll().stream()
                    .map(ClasseSiege::getId)
                    .toList();
            }
            
            // Traiter les pourcentages (format: pourcentage_categorieId_classeId)
            Map<String, BigDecimal> pourcentagesMap = new HashMap<>();
            for (Map.Entry<String, String> entry : allParams.entrySet()) {
                if (entry.getKey().startsWith("pourcentage_") && entry.getValue() != null && !entry.getValue().isEmpty()) {
                    String[] parts = entry.getKey().split("_");
                    if (parts.length == 3) {
                        String key = parts[1] + "_" + parts[2]; // categorieId_classeId
                        try {
                            BigDecimal pourcentage = new BigDecimal(entry.getValue());
                            if (pourcentage.compareTo(BigDecimal.ZERO) > 0) {
                                pourcentagesMap.put(key, pourcentage);
                            }
                        } catch (NumberFormatException ignored) {}
                    }
                }
            }
            
            // Debug logs
            System.out.println("=== DEBUG Configuration ===");
            System.out.println("Pourcentages trouvés: " + pourcentagesMap.size());
            System.out.println("Détails: " + pourcentagesMap);
            System.out.println("Classes à utiliser: " + classesAUtiliser);
            System.out.println("Vol programmé ID: " + config.getVolProgramme().getId());
            
            // Créer les associations et les remises
            int nbRemisesCreees = 0;
            if (!pourcentagesMap.isEmpty()) {
                nbRemisesCreees = service.creerAssociationsEtRemises(config, classesAUtiliser, pourcentagesMap);
                System.out.println("Nombre de remises créées: " + nbRemisesCreees);
                
                if (nbRemisesCreees == 0) {
                    redirectAttributes.addFlashAttribute("warning", 
                        "Configuration créée mais AUCUNE remise n'a pu être insérée ! " +
                        "Raison: Aucun prix n'existe pour ce vol programmé. " +
                        "<a href='/prix-vol/new' class='alert-link'>Créer des prix d'abord</a>");
                } else {
                    redirectAttributes.addFlashAttribute("success", 
                        "Configuration créée avec succès ! " + 
                        pourcentagesMap.size() + " associations créées et " + 
                        nbRemisesCreees + " remises insérées dans remise_categorie_place. " +
                        "<a href='/remises-categorie-place' class='alert-link'>Voir toutes les remises</a>");
                }
            } else {
                System.out.println("ATTENTION: Aucun pourcentage trouvé dans le formulaire!");
                redirectAttributes.addFlashAttribute("warning", "Configuration créée mais aucun pourcentage n'a été défini.");
            }
            return "redirect:/configurations-remise-personnalisee";
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Erreur: " + e.getMessage());
            return "redirect:/configurations-remise-personnalisee/new";
        }
    }
    
    /**
     * Affiche le formulaire d'édition
     */
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Optional<ConfigurationRemisePersonnalisee> configOpt = service.findById(id);
        if (configOpt.isEmpty()) {
            return "redirect:/configurations-remise-personnalisee";
        }
        
        ConfigurationRemisePersonnalisee config = configOpt.get();
        model.addAttribute("configuration", config);
        model.addAttribute("volsProgrammes", volProgrammeRepository.findAll());
        model.addAttribute("categoriesPassager", categoriePassagerRepository.findAll());
        model.addAttribute("classesSiege", classeSiegeRepository.findAll());
        
        // Récupérer les prix des classes pour ce vol depuis vol_programme_classe_prix
        if (config.getVolProgramme() != null) {
            List<VolProgrammeClassePrix> prixParClasse = volProgrammeClassePrixRepository.findByVolProgrammeId(config.getVolProgramme().getId());
            model.addAttribute("prixParClasse", prixParClasse);
        }
        
        return "configurations-remise-personnalisee/form";
    }
    
    /**
     * Met à jour une configuration
     */
    @PostMapping("/update/{id}")
    public String update(
            @PathVariable Long id,
            @RequestParam String nom,
            @RequestParam(required = false) String description,
            @RequestParam Long volProgrammeId,
            @RequestParam(required = false) Boolean actif,
            RedirectAttributes redirectAttributes) {
        
        try {
            Optional<ConfigurationRemisePersonnalisee> configOpt = service.findById(id);
            if (configOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Configuration non trouvée");
                return "redirect:/configurations-remise-personnalisee";
            }
            
            Optional<VolProgramme> volProgrammeOpt = volProgrammeRepository.findById(volProgrammeId);
            if (volProgrammeOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Vol programmé non trouvé");
                return "redirect:/configurations-remise-personnalisee/edit/" + id;
            }
            
            ConfigurationRemisePersonnalisee config = configOpt.get();
            config.setNom(nom);
            config.setDescription(description);
            config.setVolProgramme(volProgrammeOpt.get());
            config.setActif(actif != null ? actif : false);
            
            service.save(config);
            redirectAttributes.addFlashAttribute("success", "Configuration mise à jour avec succès");
            return "redirect:/configurations-remise-personnalisee";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur: " + e.getMessage());
            return "redirect:/configurations-remise-personnalisee/edit/" + id;
        }
    }
    
    /**
     * Supprime une configuration
     */
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            service.findById(id).ifPresent(config -> {
                // Supprimer d'abord les associations
                assocRepository.deleteAll(config.getAssociations());
            });
            service.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Configuration supprimée avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la suppression: " + e.getMessage());
        }
        return "redirect:/configurations-remise-personnalisee";
    }
    
    /**
     * Affiche les détails d'une configuration avec ses associations
     */
    @GetMapping("/details/{id}")
    public String details(@PathVariable Long id, Model model) {
        Optional<ConfigurationRemisePersonnalisee> configOpt = service.findById(id);
        if (configOpt.isEmpty()) {
            return "redirect:/configurations-remise-personnalisee";
        }
        
        ConfigurationRemisePersonnalisee config = configOpt.get();
        model.addAttribute("configuration", config);
        
        // Récupérer les prix des classes pour ce vol depuis vol_programme_classe_prix
        if (config.getVolProgramme() != null) {
            List<VolProgrammeClassePrix> prixParClasse = volProgrammeClassePrixRepository.findByVolProgrammeId(config.getVolProgramme().getId());
            model.addAttribute("prixParClasse", prixParClasse);
            
            // Récupérer les remises dénormalisées depuis remise_categorie_place
            List<RemiseCategoriePlace> remisesDenormalisees = remiseCategoriePlaceRepository
                .findByVolProgramme_Id(config.getVolProgramme().getId());
            model.addAttribute("remisesDenormalisees", remisesDenormalisees);
        }
        
        model.addAttribute("categoriesPassager", categoriePassagerRepository.findAll());
        model.addAttribute("classesSiege", classeSiegeRepository.findAll());
        
        return "configurations-remise-personnalisee/details";
    }
    
    /**
     * Ajoute une association (pourcentage par catégorie/classe)
     */
    @PostMapping("/{configId}/associations/add")
    public String addAssociation(
            @PathVariable Long configId,
            @RequestParam Long categoriePassagerId,
            @RequestParam Long classeSiegeId,
            @RequestParam BigDecimal pourcentageRemise,
            RedirectAttributes redirectAttributes) {
        
        try {
            Optional<ConfigurationRemisePersonnalisee> configOpt = service.findById(configId);
            if (configOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Configuration non trouvée");
                return "redirect:/configurations-remise-personnalisee";
            }
            
            Optional<CategoriePassager> categorieOpt = categoriePassagerRepository.findById(categoriePassagerId);
            Optional<ClasseSiege> classeOpt = classeSiegeRepository.findById(classeSiegeId);
            
            if (categorieOpt.isEmpty() || classeOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Catégorie ou classe non trouvée");
                return "redirect:/configurations-remise-personnalisee/details/" + configId;
            }
            
            // Vérifier si l'association existe déjà
            Optional<AssocConfigurationRemisePersonnalisee> existingAssoc = 
                assocRepository.findByConfigurationRemisePersonnalisee_IdAndCategoriePassager_IdCategoriePassagerAndClasseSiege_Id(
                    configId, categoriePassagerId, classeSiegeId);
            
            if (existingAssoc.isPresent()) {
                redirectAttributes.addFlashAttribute("error", "Cette combinaison catégorie/classe existe déjà");
                return "redirect:/configurations-remise-personnalisee/details/" + configId;
            }
            
            AssocConfigurationRemisePersonnalisee assoc = new AssocConfigurationRemisePersonnalisee();
            assoc.setConfigurationRemisePersonnalisee(configOpt.get());
            assoc.setCategoriePassager(categorieOpt.get());
            assoc.setClasseSiege(classeOpt.get());
            assoc.setPourcentageRemise(pourcentageRemise);
            
            assocRepository.save(assoc);
            redirectAttributes.addFlashAttribute("success", "Association ajoutée avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur: " + e.getMessage());
        }
        
        return "redirect:/configurations-remise-personnalisee/details/" + configId;
    }
    
    /**
     * Supprime une association
     */
    @GetMapping("/{configId}/associations/delete/{assocId}")
    public String deleteAssociation(
            @PathVariable Long configId,
            @PathVariable Long assocId,
            RedirectAttributes redirectAttributes) {
        
        try {
            assocRepository.deleteById(assocId);
            redirectAttributes.addFlashAttribute("success", "Association supprimée avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la suppression: " + e.getMessage());
        }
        
        return "redirect:/configurations-remise-personnalisee/details/" + configId;
    }
    
    /**
     * Active/désactive une configuration
     */
    @GetMapping("/toggle/{id}")
    public String toggle(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Optional<ConfigurationRemisePersonnalisee> configOpt = service.findById(id);
            if (configOpt.isPresent()) {
                ConfigurationRemisePersonnalisee config = configOpt.get();
                if (config.getActif()) {
                    service.deactivate(id);
                    redirectAttributes.addFlashAttribute("success", "Configuration désactivée");
                } else {
                    service.activate(id);
                    redirectAttributes.addFlashAttribute("success", "Configuration activée");
                }
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur: " + e.getMessage());
        }
        return "redirect:/configurations-remise-personnalisee";
    }
}
