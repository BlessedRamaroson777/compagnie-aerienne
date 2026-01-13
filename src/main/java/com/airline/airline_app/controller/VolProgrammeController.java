package com.airline.airline_app.controller;

import com.airline.airline_app.entity.Equipage;
import com.airline.airline_app.entity.RoleEquipage;
import com.airline.airline_app.entity.VolEquipage;
import com.airline.airline_app.entity.VolProgramme;
import com.airline.airline_app.entity.enums.StatutVol;
import com.airline.airline_app.service.AvionService;
import com.airline.airline_app.service.EquipageService;
import com.airline.airline_app.service.RoleEquipageService;
import com.airline.airline_app.service.VolEquipageService;
import com.airline.airline_app.service.VolProgrammeService;
import com.airline.airline_app.service.VolService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/vols-programmes")
@RequiredArgsConstructor
public class VolProgrammeController {
    
    private final VolProgrammeService volProgrammeService;
    private final VolService volService;
    private final AvionService avionService;
    private final EquipageService equipageService;
    private final RoleEquipageService roleEquipageService;
    private final VolEquipageService volEquipageService;
    
    @GetMapping
    public String list(@RequestParam(required = false) Long volId,
                      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateMin,
                      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateMax,
                      @RequestParam(required = false) StatutVol status,
                      Model model) {
        
        List<VolProgramme> volsProgrammes = volProgrammeService.findAll();
        
        // Filtrer si nécessaire
        if (volId != null) {
            volsProgrammes = volsProgrammes.stream()
                .filter(vp -> vp.getVol().getId().equals(volId))
                .toList();
        }
        if (dateMin != null) {
            volsProgrammes = volsProgrammes.stream()
                .filter(vp -> vp.getDateHeureDepart().isAfter(dateMin) || vp.getDateHeureDepart().isEqual(dateMin))
                .toList();
        }
        if (dateMax != null) {
            volsProgrammes = volsProgrammes.stream()
                .filter(vp -> vp.getDateHeureDepart().isBefore(dateMax) || vp.getDateHeureDepart().isEqual(dateMax))
                .toList();
        }
        if (status != null) {
            volsProgrammes = volsProgrammes.stream()
                .filter(vp -> vp.getStatus() == status)
                .toList();
        }
        
        model.addAttribute("volsProgrammes", volsProgrammes);
        model.addAttribute("vols", volService.findAll());
        model.addAttribute("volId", volId);
        model.addAttribute("dateMin", dateMin);
        model.addAttribute("dateMax", dateMax);
        model.addAttribute("status", status);
        model.addAttribute("statuts", StatutVol.values());
        
        return "vol-programme/list";
    }
    
    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("volProgramme", new VolProgramme());
        model.addAttribute("vols", volService.findAll());
        model.addAttribute("avions", avionService.findAll());
        model.addAttribute("statuts", StatutVol.values());
        return "vol-programme/form";
    }
    
    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        VolProgramme volProgramme = volProgrammeService.findById(id)
            .orElseThrow(() -> new RuntimeException("Vol programmé non trouvé"));
        model.addAttribute("volProgramme", volProgramme);
        return "vol-programme/detail";
    }
    
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        VolProgramme volProgramme = volProgrammeService.findById(id)
            .orElseThrow(() -> new RuntimeException("Vol programmé non trouvé"));
        model.addAttribute("volProgramme", volProgramme);
        model.addAttribute("vols", volService.findAll());
        model.addAttribute("avions", avionService.findAll());
        model.addAttribute("statuts", StatutVol.values());
        return "vol-programme/form";
    }
    
    @PostMapping
    public String save(@ModelAttribute VolProgramme volProgramme,
                      @RequestParam Long volId,
                      @RequestParam Long avionId,
                      RedirectAttributes redirectAttributes) {
        try {
            volProgramme.setVol(volService.findById(volId)
                .orElseThrow(() -> new RuntimeException("Vol non trouvé")));
            volProgramme.setAvion(avionService.findById(avionId)
                .orElseThrow(() -> new RuntimeException("Avion non trouvé")));
            
            volProgrammeService.save(volProgramme);
            redirectAttributes.addFlashAttribute("success", "Vol programmé enregistré avec succès");
            return "redirect:/vols-programmes";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur: " + e.getMessage());
            return "redirect:/vols-programmes/new";
        }
    }
    
    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                        @ModelAttribute VolProgramme volProgramme,
                        @RequestParam Long volId,
                        @RequestParam Long avionId,
                        RedirectAttributes redirectAttributes) {
        try {
            VolProgramme existing = volProgrammeService.findById(id)
                .orElseThrow(() -> new RuntimeException("Vol programmé non trouvé"));
            
            existing.setVol(volService.findById(volId)
                .orElseThrow(() -> new RuntimeException("Vol non trouvé")));
            existing.setAvion(avionService.findById(avionId)
                .orElseThrow(() -> new RuntimeException("Avion non trouvé")));
            existing.setDateHeureDepart(volProgramme.getDateHeureDepart());
            existing.setDateHeureArrivee(volProgramme.getDateHeureArrivee());
            existing.setStatus(volProgramme.getStatus());
            
            volProgrammeService.save(existing);
            redirectAttributes.addFlashAttribute("success", "Vol programmé modifié avec succès");
            return "redirect:/vols-programmes";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur: " + e.getMessage());
            return "redirect:/vols-programmes/" + id + "/edit";
        }
    }
    
    @GetMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            volProgrammeService.delete(id);
            redirectAttributes.addFlashAttribute("success", "Vol programmé supprimé avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur: " + e.getMessage());
        }
        return "redirect:/vols-programmes";
    }
    
    @GetMapping("/{id}/equipages")
    public String gererEquipages(@PathVariable Long id, Model model) {
        VolProgramme volProgramme = volProgrammeService.findById(id)
            .orElseThrow(() -> new RuntimeException("Vol programmé non trouvé"));
        
        List<Equipage> tousEquipages = equipageService.findAll();
        List<VolEquipage> equipagesAssignes = volEquipageService.findByVolProgrammeId(id);
        List<RoleEquipage> roles = roleEquipageService.findAll();
        
        model.addAttribute("volProgramme", volProgramme);
        model.addAttribute("tousEquipages", tousEquipages);
        model.addAttribute("equipagesAssignes", equipagesAssignes);
        model.addAttribute("roles", roles);
        return "vol-programme/equipages";
    }
    
    @PostMapping("/{id}/equipages/ajouter")
    public String ajouterEquipage(@PathVariable Long id,
                                   @RequestParam Long equipageId,
                                   RedirectAttributes redirectAttributes) {
        try {
            VolProgramme volProgramme = volProgrammeService.findById(id)
                .orElseThrow(() -> new RuntimeException("Vol programmé non trouvé"));
            Equipage equipage = equipageService.findById(equipageId)
                .orElseThrow(() -> new RuntimeException("Équipage non trouvé"));
            
            // Vérifier si déjà assigné
            boolean dejaAssigne = volEquipageService.findByVolProgrammeId(id)
                .stream()
                .anyMatch(ve -> ve.getEquipage().getId().equals(equipageId));
            
            if (dejaAssigne) {
                redirectAttributes.addFlashAttribute("error", "Cet équipage est déjà assigné à ce vol");
            } else {
                VolEquipage volEquipage = new VolEquipage();
                volEquipage.setVolProgramme(volProgramme);
                volEquipage.setEquipage(equipage);
                volEquipageService.save(volEquipage);
                redirectAttributes.addFlashAttribute("success", "Équipage assigné avec succès");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur: " + e.getMessage());
        }
        return "redirect:/vols-programmes/" + id + "/equipages";
    }
    
    @GetMapping("/{volId}/equipages/{equipageId}/retirer")
    public String retirerEquipage(@PathVariable Long volId,
                                   @PathVariable Long equipageId,
                                   RedirectAttributes redirectAttributes) {
        try {
            List<VolEquipage> volEquipages = volEquipageService.findByVolProgrammeId(volId);
            VolEquipage volEquipage = volEquipages.stream()
                .filter(ve -> ve.getEquipage().getId().equals(equipageId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Assignation non trouvée"));
            
            volEquipageService.delete(volEquipage.getId());
            redirectAttributes.addFlashAttribute("success", "Équipage retiré avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur: " + e.getMessage());
        }
        return "redirect:/vols-programmes/" + volId + "/equipages";
    }
}
