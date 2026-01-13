package com.airline.airline_app.controller;

import com.airline.airline_app.entity.Billet;
import com.airline.airline_app.entity.Reservation;
import com.airline.airline_app.entity.SiegeVol;
import com.airline.airline_app.entity.enums.StatutBillet;
import com.airline.airline_app.service.BilletService;
import com.airline.airline_app.service.ReservationService;
import com.airline.airline_app.service.SiegeVolService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/billets")
@RequiredArgsConstructor
public class BilletController {

    private final BilletService billetService;
    private final ReservationService reservationService;
    private final SiegeVolService siegeVolService;

    @GetMapping
    public String list(Model model) {
        List<Billet> billets = billetService.findAll();
        model.addAttribute("billets", billets);
        return "billet/list";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("billet", new Billet());
        model.addAttribute("reservations", reservationService.findAll());
        model.addAttribute("siegesVol", siegeVolService.findAll());
        model.addAttribute("statuts", StatutBillet.values());
        return "billet/form";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Billet billet = billetService.findById(id)
                .orElseThrow(() -> new RuntimeException("Billet non trouvé"));
        model.addAttribute("billet", billet);
        return "billet/detail";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Billet billet = billetService.findById(id)
                .orElseThrow(() -> new RuntimeException("Billet non trouvé"));
        model.addAttribute("billet", billet);
        model.addAttribute("reservations", reservationService.findAll());
        model.addAttribute("siegesVol", siegeVolService.findAll());
        model.addAttribute("statuts", StatutBillet.values());
        return "billet/form";
    }

    @PostMapping
    public String save(@ModelAttribute Billet billet,
                       RedirectAttributes redirectAttributes) {
        try {
            if (billet.getReservation() == null || billet.getReservation().getId() == null) {
                throw new RuntimeException("Réservation non spécifiée");
            }
            if (billet.getSiegeVol() == null || billet.getSiegeVol().getId() == null) {
                throw new RuntimeException("Siège non spécifié");
            }

            Reservation reservation = reservationService.findById(billet.getReservation().getId())
                    .orElseThrow(() -> new RuntimeException("Réservation non trouvée"));
            SiegeVol siegeVol = siegeVolService.findById(billet.getSiegeVol().getId())
                    .orElseThrow(() -> new RuntimeException("Siège vol non trouvé"));

            // Vérifier que le siège est disponible
            if (siegeVol.getOccupe()) {
                throw new RuntimeException("Ce siège est déjà occupé");
            }

            billet.setReservation(reservation);
            billet.setSiegeVol(siegeVol);
            
            // Si le statut n'est pas défini, mettre EMIS par défaut
            if (billet.getStatut() == null) {
                billet.setStatut(StatutBillet.EMIS);
            }

            billetService.save(billet);
            
            // Marquer le siège comme occupé
            siegeVol.setOccupe(true);
            siegeVolService.update(siegeVol.getId(), siegeVol);
            
            redirectAttributes.addFlashAttribute("success", "Billet créé avec succès");
            return "redirect:/billets";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur: " + e.getMessage());
            return "redirect:/billets/new";
        }
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @ModelAttribute Billet billet,
                         RedirectAttributes redirectAttributes) {
        try {
            if (billet.getReservation() == null || billet.getReservation().getId() == null) {
                throw new RuntimeException("Réservation non spécifiée");
            }
            if (billet.getSiegeVol() == null || billet.getSiegeVol().getId() == null) {
                throw new RuntimeException("Siège non spécifié");
            }

            Reservation reservation = reservationService.findById(billet.getReservation().getId())
                    .orElseThrow(() -> new RuntimeException("Réservation non trouvée"));
            SiegeVol siegeVol = siegeVolService.findById(billet.getSiegeVol().getId())
                    .orElseThrow(() -> new RuntimeException("Siège vol non trouvé"));

            billet.setId(id);
            billet.setReservation(reservation);
            billet.setSiegeVol(siegeVol);

            billetService.update(id, billet);
            redirectAttributes.addFlashAttribute("success", "Billet modifié avec succès");
            return "redirect:/billets/" + id;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur: " + e.getMessage());
            return "redirect:/billets/" + id + "/edit";
        }
    }

    @GetMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            billetService.delete(id);
            redirectAttributes.addFlashAttribute("success", "Billet supprimé avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur: " + e.getMessage());
        }
        return "redirect:/billets";
    }

    @GetMapping("/{id}/annuler")
    public String annuler(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Billet billet = billetService.findById(id)
                    .orElseThrow(() -> new RuntimeException("Billet non trouvé"));
            billet.setStatut(StatutBillet.ANNULE);
            billetService.update(id, billet);
            redirectAttributes.addFlashAttribute("success", "Billet annulé avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur: " + e.getMessage());
        }
        return "redirect:/billets/" + id;
    }
}
