package com.airline.airline_app.controller;

import com.airline.airline_app.entity.MethodePaiement;
import com.airline.airline_app.entity.Paiement;
import com.airline.airline_app.entity.PaiementMethode;
import com.airline.airline_app.entity.enums.StatutPaiement;
import com.airline.airline_app.service.MethodePaiementService;
import com.airline.airline_app.service.PaiementService;
import com.airline.airline_app.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/paiements")
@RequiredArgsConstructor
public class PaiementController {
    
    private final PaiementService paiementService;
    private final ReservationService reservationService;
    private final MethodePaiementService methodePaiementService;
    
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
        model.addAttribute("methodesPaiement", methodePaiementService.findAll());
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
                model.addAttribute("methodesPaiement", methodePaiementService.findAll());
            },
            () -> model.addAttribute("error", "Paiement non trouvé")
        );
        return "paiement/form";
    }
    
    @PostMapping
    public String save(@ModelAttribute Paiement paiement,
                      @RequestParam Long reservationId,
                      @RequestParam List<Long> methodePaiementIds,
                      @RequestParam List<BigDecimal> montantMethodes,
                      RedirectAttributes redirectAttributes) {
        try {
            var reservation = reservationService.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Réservation non trouvée"));
            
            paiement.setReservation(reservation);
            paiement.setDatePaiement(LocalDateTime.now());
            paiement.setStatut(StatutPaiement.EN_ATTENTE); // Statut automatique
            
            // Sauvegarder le paiement d'abord
            Paiement savedPaiement = paiementService.save(paiement);
            
            // Créer les méthodes de paiement associées
            for (int i = 0; i < methodePaiementIds.size(); i++) {
                MethodePaiement methode = methodePaiementService.findById(methodePaiementIds.get(i))
                    .orElseThrow(() -> new RuntimeException("Méthode de paiement non trouvée"));
                
                PaiementMethode paiementMethode = new PaiementMethode();
                paiementMethode.setPaiement(savedPaiement);
                paiementMethode.setMethodePaiement(methode);
                paiementMethode.setMontant(montantMethodes.get(i));
                
                savedPaiement.getMethodesUtilisees().add(paiementMethode);
            }
            
            paiementService.update(savedPaiement.getId(), savedPaiement);
            redirectAttributes.addFlashAttribute("success", "Paiement créé avec succès");
            return "redirect:/paiements";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur: " + e.getMessage());
            return "redirect:/paiements/new";
        }
    }
}
