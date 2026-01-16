package com.airline.airline_app.controller;

import com.airline.airline_app.entity.RemiseCategoriePlace;
import com.airline.airline_app.repository.RemiseCategoriePlaceRepository;
import com.airline.airline_app.repository.CategoriePassagerRepository;
import com.airline.airline_app.repository.ClasseSiegeRepository;
import com.airline.airline_app.repository.VolProgrammeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/remises-categorie-place")
public class RemiseCategoriePlaceController {
    
    @Autowired
    private RemiseCategoriePlaceRepository remiseRepository;
    
    @Autowired
    private CategoriePassagerRepository categoriePassagerRepository;
    
    @Autowired
    private ClasseSiegeRepository classeSiegeRepository;
    
    @Autowired
    private VolProgrammeRepository volProgrammeRepository;
    
    @GetMapping
    public String list(Model model) {
        model.addAttribute("remises", remiseRepository.findAll());
        return "remise-categorie-place/list";
    }
    
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("remise", new RemiseCategoriePlace());
        model.addAttribute("categories", categoriePassagerRepository.findAll());
        model.addAttribute("classes", classeSiegeRepository.findAll());
        model.addAttribute("volsProgrammes", volProgrammeRepository.findAll());
        return "remise-categorie-place/form";
    }
    
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        return remiseRepository.findById(id)
            .map(remise -> {
                model.addAttribute("remise", remise);
                model.addAttribute("categories", categoriePassagerRepository.findAll());
                model.addAttribute("classes", classeSiegeRepository.findAll());
                model.addAttribute("volsProgrammes", volProgrammeRepository.findAll());
                return "remise-categorie-place/form";
            })
            .orElseGet(() -> {
                redirectAttributes.addFlashAttribute("error", "Remise non trouvée");
                return "redirect:/remises-categorie-place";
            });
    }
    
    @PostMapping
    public String save(@ModelAttribute RemiseCategoriePlace remise,
                      @RequestParam Long volProgrammeId,
                      @RequestParam Long categoriePassagerId,
                      @RequestParam Long classeSiegeId,
                      RedirectAttributes redirectAttributes) {
        try {
            var volProgramme = volProgrammeRepository.findById(volProgrammeId)
                .orElseThrow(() -> new RuntimeException("Vol programmé non trouvé"));
            var categorie = categoriePassagerRepository.findById(categoriePassagerId)
                .orElseThrow(() -> new RuntimeException("Catégorie de passager non trouvée"));
            var classe = classeSiegeRepository.findById(classeSiegeId)
                .orElseThrow(() -> new RuntimeException("Classe de siège non trouvée"));
            
            remise.setVolProgramme(volProgramme);
            remise.setCategoriePassager(categorie);
            remise.setClasseSiege(classe);
            remiseRepository.save(remise);
            
            redirectAttributes.addFlashAttribute("success", "Remise enregistrée avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de l'enregistrement: " + e.getMessage());
        }
        return "redirect:/remises-categorie-place";
    }
    
    @GetMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            remiseRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Remise supprimée avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la suppression: " + e.getMessage());
        }
        return "redirect:/remises-categorie-place";
    }
}
