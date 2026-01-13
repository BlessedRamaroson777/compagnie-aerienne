package com.airline.airline_app.controller;

import com.airline.airline_app.entity.Paiement;
import com.airline.airline_app.entity.enums.StatutPaiement;
import com.airline.airline_app.service.PaiementService;
import com.airline.airline_app.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;

@Controller
@RequestMapping("/paiements")
@RequiredArgsConstructor
public class PaiementController {
    
    private final PaiementService paiementService;
    private final ReservationService reservationService;
    
    @GetMapping
    public String list(@RequestParam(required = false) Long reservationId,
                      @RequestParam(required = false) StatutPaiement statut,
                      Model model) {
        try {
            var paiements = paiementService.findAll();
            model.addAttribute("paiements", paiements);
            model.addAttribute("reservations", reservationService.findAll());
            model.addAttribute("statuts", Arrays.asList(StatutPaiement.values()));
            model.addAttribute("reservationId", reservationId);
            model.addAttribute("statut", statut);
            return "paiement/list";
        } catch (Exception e) {
            model.addAttribute("error", "Erreur lors du chargement des paiements: " + e.getMessage());
            return "paiement/list";
        }
    }
    
    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("paiement", new Paiement());
        model.addAttribute("reservations", reservationService.findAll());
        model.addAttribute("statuts", Arrays.asList(StatutPaiement.values()));
        return "paiement/form";
    }
    
    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        paiementService.findById(id).ifPresentOrElse(
            paiement -> model.addAttribute("paiement", paiement),
            () -> model.addAttribute("error", "Paiement non trouvé")
        );
        return "paiement/detail";
    }
    
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        paiementService.findById(id).ifPresentOrElse(
            paiement -> {
                model.addAttribute("paiement", paiement);
                model.addAttribute("reservations", reservationService.findAll());
                model.addAttribute("statuts", Arrays.asList(StatutPaiement.values()));
            },
            () -> model.addAttribute("error", "Paiement non trouvé")
        );
        return "paiement/form";
    }
    
    @PostMapping
    public String save(@ModelAttribute Paiement paiement,
                      @RequestParam Long reservationId,
                      RedirectAttributes redirectAttributes) {
        try {
            var reservation = reservationService.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Réservation non trouvée"));
            paiement.setReservation(reservation);
            
            if (paiement.getId() == null) {
                paiementService.save(paiement);
                redirectAttributes.addFlashAttribute("success", "Paiement créé avec succès");
            } else {
                paiementService.update(paiement.getId(), paiement);
                redirectAttributes.addFlashAttribute("success", "Paiement modifié avec succès");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur: " + e.getMessage());
        }
        return "redirect:/paiements";
    }
}
