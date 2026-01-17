package com.airline.airline_app.controller;

import com.airline.airline_app.entity.*;
import com.airline.airline_app.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/remises-rapide")
@RequiredArgsConstructor
public class RemiseRapideController {
    
    private final RemiseCategoriePlaceRepository remiseRepository;
    private final VolProgrammeRepository volProgrammeRepository;
    private final CategoriePassagerRepository categoriePassagerRepository;
    private final ClasseSiegeRepository classeSiegeRepository;
    private final VolProgrammeClassePrixRepository volProgrammeClassePrixRepository;
    
    /**
     * Affiche le formulaire de remise rapide en masse
     */
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("volsProgrammes", volProgrammeRepository.findAll());
        model.addAttribute("categoriesPassager", categoriePassagerRepository.findAll());
        model.addAttribute("classesSiege", classeSiegeRepository.findAll());
        
        // Vérifier si des prix existent
        long nbPrix = volProgrammeClassePrixRepository.count();
        if (nbPrix == 0) {
            model.addAttribute("warning", 
                "⚠️ ATTENTION : Aucun prix n'est configuré ! " +
                "Les prix de référence ne seront pas affichés.");
        }
        
        return "remise-rapide/form";
    }
    
    /**
     * Crée plusieurs remises en une seule fois
     * Le prix final est saisi, le système calcule et stocke le MONTANT de remise (prix_ref - prix_final)
     */
    @PostMapping("/save")
    public String save(
            @RequestParam Long volProgrammeId,
            @RequestParam(required = false) Boolean toutesLesClasses,
            @RequestParam Map<String, String> allParams,
            RedirectAttributes redirectAttributes) {
        
        try {
            Optional<VolProgramme> volProgrammeOpt = volProgrammeRepository.findById(volProgrammeId);
            if (volProgrammeOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Vol programmé non trouvé");
                return "redirect:/remises-rapide/new";
            }
            
            VolProgramme volProgramme = volProgrammeOpt.get();
            
            // Récupérer tous les prix de référence pour ce vol
            List<VolProgrammeClassePrix> prixReferenceList = 
                volProgrammeClassePrixRepository.findByVolProgrammeId(volProgrammeId);
            
            // Map pour accès rapide: classeId -> prix de référence
            Map<Long, Double> prixReferenceMap = prixReferenceList.stream()
                .collect(java.util.stream.Collectors.toMap(
                    vpc -> vpc.getClasseSiege().getId(),
                    vpc -> vpc.getPrix().doubleValue()
                ));
            
            // Récupérer les prix finaux depuis le formulaire
            // Format: prixFinal_categorieId_classeId
            int nbRemisesCreees = 0;
            int nbRemisesIgnorees = 0;
            
            System.out.println("=== REMISE RAPIDE: Création ===");
            System.out.println("Vol programmé ID: " + volProgrammeId);
            System.out.println("Prix de référence disponibles: " + prixReferenceMap.size());
            
            for (Map.Entry<String, String> entry : allParams.entrySet()) {
                if (entry.getKey().startsWith("prixFinal_")) {
                    String[] parts = entry.getKey().substring("prixFinal_".length()).split("_");
                    if (parts.length != 2) continue;
                    
                    try {
                        Long categorieId = Long.parseLong(parts[0]);
                        Long classeId = Long.parseLong(parts[1]);
                        
                        String prixFinalStr = entry.getValue();
                        if (prixFinalStr == null || prixFinalStr.trim().isEmpty()) {
                            continue;
                        }
                        
                        Double prixFinal = Double.parseDouble(prixFinalStr.trim());
                        
                        // Ignorer si prix final <= 0 (pas de remise souhaitée)
                        if (prixFinal <= 0) {
                            System.out.println("Prix final = 0 pour catégorie=" + categorieId + 
                                ", classe=" + classeId + " -> ignoré");
                            nbRemisesIgnorees++;
                            continue;
                        }
                        
                        // Récupérer le prix de référence
                        Double prixReference = prixReferenceMap.get(classeId);
                        if (prixReference == null || prixReference <= 0) {
                            System.out.println("Prix de référence non trouvé pour classe=" + classeId);
                            continue;
                        }
                        
                        // Calculer le MONTANT de remise
                        double montantRemise = prixReference - prixFinal;
                        
                        // Si le prix final est >= au prix de référence, pas de remise
                        if (montantRemise <= 0) {
                            System.out.println("Prix final >= prix référence pour catégorie=" + categorieId + 
                                ", classe=" + classeId + " (final=" + prixFinal + ", ref=" + prixReference + ") -> ignoré");
                            nbRemisesIgnorees++;
                            continue;
                        }
                        
                        // Récupérer les entités
                        Optional<CategoriePassager> categorieOpt = categoriePassagerRepository.findById(categorieId);
                        Optional<ClasseSiege> classeOpt = classeSiegeRepository.findById(classeId);
                        
                        if (categorieOpt.isEmpty() || classeOpt.isEmpty()) {
                            continue;
                        }
                        
                        System.out.println("Traitement: catégorie=" + categorieId + ", classe=" + classeId + 
                            ", prixRef=" + prixReference + ", prixFinal=" + prixFinal + 
                            ", montantRemise=" + montantRemise);
                        
                        // Créer ou mettre à jour la remise
                        Optional<RemiseCategoriePlace> remiseOpt = remiseRepository
                            .findByVolProgramme_IdAndCategoriePassager_IdCategoriePassagerAndClasseSiege_Id(
                                volProgrammeId, categorieId, classeId);
                        
                        RemiseCategoriePlace remise;
                        if (remiseOpt.isPresent()) {
                            remise = remiseOpt.get();
                            System.out.println("  -> Mise à jour remise existante ID: " + remise.getId());
                        } else {
                            remise = new RemiseCategoriePlace();
                            remise.setVolProgramme(volProgramme);
                            remise.setCategoriePassager(categorieOpt.get());
                            remise.setClasseSiege(classeOpt.get());
                            System.out.println("  -> Création nouvelle remise");
                        }
                        
                        // Stocker le MONTANT de remise (pas le prix final)
                        remise.setRemisePrix(montantRemise);
                        remiseRepository.save(remise);
                        nbRemisesCreees++;
                        System.out.println("  -> Montant remise sauvegardé: " + montantRemise + " Ar");
                        
                    } catch (NumberFormatException e) {
                        System.out.println("Erreur de format pour: " + entry.getKey() + " = " + entry.getValue());
                    }
                }
            }
            
            System.out.println("=== FIN: Remises créées: " + nbRemisesCreees + ", ignorées: " + nbRemisesIgnorees + " ===");
            
            if (nbRemisesCreees > 0) {
                String message = nbRemisesCreees + " remise(s) créée(s) avec succès !";
                if (nbRemisesIgnorees > 0) {
                    message += " (" + nbRemisesIgnorees + " entrée(s) ignorée(s) car prix = 0 ou >= prix référence)";
                }
                message += " <a href='/remises-categorie-place' class='alert-link'>Voir toutes les remises</a>";
                redirectAttributes.addFlashAttribute("success", message);
            } else {
                String message = "Aucune remise n'a été créée.";
                if (nbRemisesIgnorees > 0) {
                    message += " Toutes les entrées (" + nbRemisesIgnorees + ") ont été ignorées (prix = 0 ou >= prix référence).";
                } else {
                    message += " Veuillez saisir au moins un prix final valide.";
                }
                redirectAttributes.addFlashAttribute("warning", message);
            }
            
            return "redirect:/remises-rapide/new";
            
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Erreur : " + e.getMessage());
            return "redirect:/remises-rapide/new";
        }
    }
    
    /**
     * API pour récupérer les prix de référence d'un vol programmé (pour AJAX)
     */
    @GetMapping("/prix/{volProgrammeId}")
    @ResponseBody
    public Map<Long, Double> getPrixReference(@PathVariable Long volProgrammeId) {
        List<VolProgrammeClassePrix> prixList = volProgrammeClassePrixRepository.findByVolProgrammeId(volProgrammeId);
        
        // Retourner un Map: classeSiegeId -> prix
        return prixList.stream()
            .collect(java.util.stream.Collectors.toMap(
                vpc -> vpc.getClasseSiege().getId(),
                vpc -> vpc.getPrix().doubleValue()
            ));
    }
}
