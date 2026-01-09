package com.airline.airline_app.controller;

import com.airline.airline_app.entity.ClasseSiege;
import com.airline.airline_app.entity.PrixVol;
import com.airline.airline_app.entity.Vol;
import com.airline.airline_app.service.ClasseSiegeService;
import com.airline.airline_app.service.PrixVolService;
import com.airline.airline_app.service.VolService;
import com.airline.airline_app.specification.PrixVolSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/prix-vol")
@RequiredArgsConstructor
public class PrixVolController {
    
    private final PrixVolService prixVolService;
    private final VolService volService;
    private final ClasseSiegeService classeSiegeService;
    
    @GetMapping
    public String list(@RequestParam(required = false) Long volId,
                      @RequestParam(required = false) Long classeSiegeId,
                      @RequestParam(required = false) BigDecimal prixMin,
                      @RequestParam(required = false) BigDecimal prixMax,
                      Model model) {
        
        Specification<PrixVol> spec = PrixVolSpecification.filterBy(volId, classeSiegeId, prixMin, prixMax);
        List<PrixVol> prixVols = prixVolService.findAll(spec);
        
        model.addAttribute("prixVols", prixVols);
        model.addAttribute("vols", volService.findAll());
        model.addAttribute("classes", classeSiegeService.findAll());
        model.addAttribute("volId", volId);
        model.addAttribute("classeSiegeId", classeSiegeId);
        model.addAttribute("prixMin", prixMin);
        model.addAttribute("prixMax", prixMax);
        
        return "prix-vol/list";
    }
    
    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("prixVol", new PrixVol());
        model.addAttribute("vols", volService.findAll());
        model.addAttribute("classes", classeSiegeService.findAll());
        return "prix-vol/form";
    }
    
    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        PrixVol prixVol = prixVolService.findById(id)
            .orElseThrow(() -> new RuntimeException("Prix de vol non trouvé"));
        model.addAttribute("prixVol", prixVol);
        return "prix-vol/detail";
    }
    
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        PrixVol prixVol = prixVolService.findById(id)
            .orElseThrow(() -> new RuntimeException("Prix de vol non trouvé"));
        model.addAttribute("prixVol", prixVol);
        model.addAttribute("vols", volService.findAll());
        model.addAttribute("classes", classeSiegeService.findAll());
        return "prix-vol/form";
    }
    
    @PostMapping
    public String save(@ModelAttribute PrixVol prixVol,
                      @RequestParam Long volId,
                      @RequestParam Long classeSiegeId,
                      RedirectAttributes redirectAttributes) {
        try {
            Vol vol = volService.findById(volId)
                .orElseThrow(() -> new RuntimeException("Vol non trouvé"));
            ClasseSiege classeSiege = classeSiegeService.findById(classeSiegeId)
                .orElseThrow(() -> new RuntimeException("Classe de siège non trouvée"));
            
            prixVol.setVol(vol);
            prixVol.setClasseSiege(classeSiege);
            
            if (prixVol.getId() == null) {
                prixVolService.save(prixVol);
                redirectAttributes.addFlashAttribute("success", "Prix de vol créé avec succès");
            } else {
                prixVolService.update(prixVol.getId(), prixVol);
                redirectAttributes.addFlashAttribute("success", "Prix de vol modifié avec succès");
            }
            return "redirect:/prix-vol";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur: " + e.getMessage());
            return "redirect:/prix-vol/new";
        }
    }
    
    @GetMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            prixVolService.delete(id);
            redirectAttributes.addFlashAttribute("success", "Prix de vol supprimé avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Impossible de supprimer: " + e.getMessage());
        }
        return "redirect:/prix-vol";
    }
}
