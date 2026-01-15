package com.airline.airline_app.controller;

import com.airline.airline_app.entity.Avion;
import com.airline.airline_app.entity.AvionClasseConfiguration;
import com.airline.airline_app.service.AvionService;
import com.airline.airline_app.service.AvionClasseConfigurationService;
import com.airline.airline_app.service.ClasseSiegeService;
import com.airline.airline_app.service.ModelAvionService;
import com.airline.airline_app.service.EtatAvionService;
import com.airline.airline_app.specification.AvionSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/avions")
@RequiredArgsConstructor
public class AvionController {
    
    private final AvionService avionService;
    private final ModelAvionService modelAvionService;
    private final EtatAvionService etatAvionService;
    private final ClasseSiegeService classeSiegeService;
    private final AvionClasseConfigurationService configurationService;
    
    @GetMapping
    public String list(@RequestParam(required = false) String codeAvion,
                      @RequestParam(required = false) String modele,
                      @RequestParam(required = false) Integer capaciteMin,
                      @RequestParam(required = false) Integer capaciteMax,
                      Model model) {
        
        Specification<Avion> spec = AvionSpecification.filterBy(codeAvion, modele, capaciteMin, capaciteMax);
        List<Avion> avions = avionService.findAll(spec);
        
        model.addAttribute("avions", avions);
        model.addAttribute("codeAvion", codeAvion);
        model.addAttribute("modele", modele);
        model.addAttribute("capaciteMin", capaciteMin);
        model.addAttribute("capaciteMax", capaciteMax);
        
        return "avion/list";
    }
    
    @GetMapping("/new")
    public String newForm(Model model) {
        try {
            System.out.println("=== DEBUG: Entering /avions/new ===");
            
            Avion avion = new Avion();
            System.out.println("DEBUG: Created new Avion object");
            
            List<com.airline.airline_app.entity.ModelAvion> modeles = modelAvionService.findAll();
            System.out.println("DEBUG: Fetched modeles count: " + (modeles != null ? modeles.size() : "null"));
            
            List<com.airline.airline_app.entity.EtatAvion> etats = etatAvionService.findAll();
            System.out.println("DEBUG: Fetched etats count: " + (etats != null ? etats.size() : "null"));
            
            List<com.airline.airline_app.entity.ClasseSiege> classes = classeSiegeService.findAll();
            System.out.println("DEBUG: Fetched classes count: " + (classes != null ? classes.size() : "null"));
            
            model.addAttribute("avion", avion);
            model.addAttribute("modeles", modeles);
            model.addAttribute("etats", etats);
            model.addAttribute("classes", classes);
            
            System.out.println("DEBUG: All attributes added to model successfully");
            System.out.println("=== DEBUG: Exiting /avions/new ===");
            
            return "avion/form";
        } catch (Exception e) {
            System.err.println("ERROR in /avions/new: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
    
    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Avion avion = avionService.findById(id)
            .orElseThrow(() -> new RuntimeException("Avion non trouvé"));
        model.addAttribute("avion", avion);
        model.addAttribute("classes", classeSiegeService.findAll());
        return "avion/detail";
    }
    
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Avion avion = avionService.findById(id)
            .orElseThrow(() -> new RuntimeException("Avion non trouvé"));
        model.addAttribute("avion", avion);
        model.addAttribute("modeles", modelAvionService.findAll());
        model.addAttribute("etats", etatAvionService.findAll());
        model.addAttribute("classes", classeSiegeService.findAll());
        model.addAttribute("configurations", configurationService.findByAvionId(id));
        return "avion/form";
    }
    
    @PostMapping
    public String save(@ModelAttribute Avion avion, 
                      @RequestParam(required = false) Map<String, String> allParams,
                      RedirectAttributes redirectAttributes) {
        try {
            System.out.println("=== DEBUG: POST /avions ===");
            System.out.println("Code avion: " + (avion != null ? avion.getCodeAvion() : "null"));
            System.out.println("All params: " + allParams);
            
            // Validation préalable : vérifier qu'il y a au moins une configuration
            List<com.airline.airline_app.entity.ClasseSiege> classes = classeSiegeService.findAll();
            boolean hasConfiguration = false;
            int configCount = 0;
            
            for (com.airline.airline_app.entity.ClasseSiege classe : classes) {
                String paramName = "classe_" + classe.getId();
                String nombrePlacesStr = allParams.get(paramName);
                System.out.println("Checking param: " + paramName + " = " + nombrePlacesStr);
                
                if (nombrePlacesStr != null && !nombrePlacesStr.trim().isEmpty()) {
                    try {
                        int nombrePlaces = Integer.parseInt(nombrePlacesStr);
                        if (nombrePlaces > 0) {
                            hasConfiguration = true;
                            configCount++;
                            System.out.println("Found valid config: " + classe.getLibelle() + " = " + nombrePlaces);
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid number format for " + paramName);
                    }
                }
            }
            
            System.out.println("Total valid configs found: " + configCount);
            
            if (!hasConfiguration) {
                System.out.println("ERROR: No configuration found!");
                redirectAttributes.addFlashAttribute("error", "Vous devez configurer au moins une classe avec des places.");
                return "redirect:/avions/new";
            }
            
            // Sauvegarde de l'avion
            Avion savedAvion;
            if (avion.getId() == null) {
                savedAvion = avionService.save(avion);
                redirectAttributes.addFlashAttribute("success", "Avion créé avec succès");
            } else {
                savedAvion = avionService.update(avion.getId(), avion);
                // Supprimer les anciennes configurations
                configurationService.deleteByAvionId(avion.getId());
                redirectAttributes.addFlashAttribute("success", "Avion modifié avec succès");
            }
            
            // Traiter les configurations de classes
            int totalCapacite = 0;
            
            System.out.println("=== Starting configuration processing ===");
            for (com.airline.airline_app.entity.ClasseSiege classe : classes) {
                String paramName = "classe_" + classe.getId();
                String nombrePlacesStr = allParams.get(paramName);
                
                System.out.println("Processing: " + paramName + " = " + nombrePlacesStr);
                
                if (nombrePlacesStr != null && !nombrePlacesStr.trim().isEmpty()) {
                    try {
                        int nombrePlaces = Integer.parseInt(nombrePlacesStr);
                        if (nombrePlaces > 0) {
                            System.out.println("Creating configuration for " + classe.getLibelle());
                            AvionClasseConfiguration config = new AvionClasseConfiguration();
                            config.setAvion(savedAvion);
                            config.setClasseSiege(classe);
                            config.setNombrePlaces(nombrePlaces);
                            
                            System.out.println("Saving configuration...");
                            configurationService.save(config);
                            System.out.println("Configuration saved!");
                            
                            totalCapacite += nombrePlaces;
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("NumberFormatException for " + paramName);
                    } catch (Exception e) {
                        System.out.println("ERROR saving config: " + e.getClass().getName() + " - " + e.getMessage());
                        e.printStackTrace();
                        throw e;
                    }
                }
            }
            System.out.println("=== Configuration processing complete. Total capacity: " + totalCapacite + " ===");
            
            // Mettre à jour la capacité totale
            if (totalCapacite > 0) {
                savedAvion.setCapaciteTotale(totalCapacite);
                avionService.update(savedAvion.getId(), savedAvion);
            }
            
            System.out.println("=== SUCCESS: Redirecting to /avions ===");
            return "redirect:/avions";
        } catch (Exception e) {
            System.out.println("=== EXCEPTION CAUGHT ===");
            System.out.println("Exception type: " + e.getClass().getName());
            System.out.println("Exception message: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Erreur: " + e.getMessage());
            return "redirect:/avions/new";
        }
    }
    
    @GetMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            avionService.delete(id);
            redirectAttributes.addFlashAttribute("success", "Avion supprimé avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur: " + e.getMessage());
        }
        return "redirect:/avions";
    }
    
    @PostMapping("/{id}/configurations")
    public String saveConfigurations(@PathVariable Long id,
                                     @RequestParam Map<String, String> allParams,
                                     RedirectAttributes redirectAttributes) {
        try {
            Avion avion = avionService.findById(id)
                .orElseThrow(() -> new RuntimeException("Avion non trouvé"));
            
            // Supprimer les anciennes configurations
            configurationService.deleteByAvionId(id);
            
            // Calculer la capacité totale
            int capaciteTotale = 0;
            
            // Créer les nouvelles configurations
            for (Map.Entry<String, String> entry : allParams.entrySet()) {
                if (entry.getKey().startsWith("classe_")) {
                    Long classeSiegeId = Long.parseLong(entry.getKey().substring(7));
                    int nombrePlaces = Integer.parseInt(entry.getValue());
                    
                    if (nombrePlaces > 0) {
                        com.airline.airline_app.entity.ClasseSiege classeSiege = classeSiegeService.findById(classeSiegeId)
                            .orElseThrow(() -> new RuntimeException("Classe de siège non trouvée"));
                        
                        AvionClasseConfiguration config = new AvionClasseConfiguration();
                        config.setAvion(avion);
                        config.setClasseSiege(classeSiege);
                        config.setNombrePlaces(nombrePlaces);
                        configurationService.save(config);
                        
                        capaciteTotale += nombrePlaces;
                    }
                }
            }
            
            // Mettre à jour la capacité totale de l'avion
            avion.setCapaciteTotale(capaciteTotale);
            avionService.save(avion);
            
            redirectAttributes.addFlashAttribute("success", "Configurations enregistrées avec succès");
            return "redirect:/avions/" + id;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur: " + e.getMessage());
            return "redirect:/avions/" + id;
        }
    }
}
