package com.airline.airline_app.controller;

import com.airline.airline_app.entity.Avion;
import com.airline.airline_app.entity.Vol;
import com.airline.airline_app.service.AvionService;
import com.airline.airline_app.service.VolService;
import com.airline.airline_app.service.AeroportService;
import com.airline.airline_app.service.PrixVolService;
import com.airline.airline_app.service.ClasseSiegeService;
import com.airline.airline_app.specification.VolSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/vols")
@RequiredArgsConstructor
public class VolController {
    
    private final VolService volService;
    private final AvionService avionService;
    private final AeroportService aeroportService;
    private final PrixVolService prixVolService;
    private final ClasseSiegeService classeSiegeService;
    
    @GetMapping
    public String list(@RequestParam(required = false) String numeroVol,
                      @RequestParam(required = false) String depart,
                      @RequestParam(required = false) String arrivee,
                      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateMin,
                      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateMax,
                      @RequestParam(required = false) BigDecimal prixMin,
                      @RequestParam(required = false) BigDecimal prixMax,
                      @RequestParam(required = false) Long avionId,
                      Model model) {
        
        Specification<Vol> spec = VolSpecification.filterBy(numeroVol, depart, arrivee, dateMin, dateMax, prixMin, prixMax, avionId);
        List<Vol> vols = volService.findAll(spec);
        List<Avion> avions = avionService.findAll();
        
        model.addAttribute("vols", vols);
        model.addAttribute("avions", avions);
        model.addAttribute("numeroVol", numeroVol);
        model.addAttribute("depart", depart);
        model.addAttribute("arrivee", arrivee);
        model.addAttribute("dateMin", dateMin);
        model.addAttribute("dateMax", dateMax);
        model.addAttribute("prixMin", prixMin);
        model.addAttribute("prixMax", prixMax);
        model.addAttribute("avionId", avionId);
        
        return "vol/list";
    }
    
    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("vol", new Vol());
        model.addAttribute("avions", avionService.findAll());
        model.addAttribute("aeroports", aeroportService.findAll());
        return "vol/form";
    }
    
    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Vol vol = volService.findById(id)
            .orElseThrow(() -> new RuntimeException("Vol non trouvé"));
        model.addAttribute("vol", vol);
        return "vol/detail";
    }
    
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Vol vol = volService.findById(id)
            .orElseThrow(() -> new RuntimeException("Vol non trouvé"));
        model.addAttribute("vol", vol);
        model.addAttribute("avions", avionService.findAll());
        model.addAttribute("aeroports", aeroportService.findAll());
        return "vol/form";
    }
    
    @PostMapping
    public String save(@ModelAttribute Vol vol, @RequestParam Long avionId, RedirectAttributes redirectAttributes) {
        try {
            Avion avion = avionService.findById(avionId)
                .orElseThrow(() -> new RuntimeException("Avion non trouvé"));
            vol.setAvion(avion);
            
            if (vol.getId() == null) {
                volService.save(vol);
                redirectAttributes.addFlashAttribute("success", "Vol créé avec succès");
            } else {
                volService.update(vol.getId(), vol);
                redirectAttributes.addFlashAttribute("success", "Vol modifié avec succès");
            }
            return "redirect:/vols";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur: " + e.getMessage());
            return "redirect:/vols/new";
        }
    }
    
    @GetMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            volService.delete(id);
            redirectAttributes.addFlashAttribute("success", "Vol supprimé avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur: " + e.getMessage());
        }
        return "redirect:/vols";
    }
}
