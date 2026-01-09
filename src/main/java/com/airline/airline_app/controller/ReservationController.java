package com.airline.airline_app.controller;

import com.airline.airline_app.entity.Reservation;
import com.airline.airline_app.entity.enums.StatutReservation;
import com.airline.airline_app.service.ClientService;
import com.airline.airline_app.service.ReservationService;
import com.airline.airline_app.service.SiegeVolService;
import com.airline.airline_app.service.VolService;
import com.airline.airline_app.specification.ReservationSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/reservations")
@RequiredArgsConstructor
public class ReservationController {
    
    private final ReservationService reservationService;
    private final ClientService clientService;
    private final VolService volService;
    private final SiegeVolService siegeVolService;
    
    @GetMapping
    public String list(@RequestParam(required = false) Long clientId,
                      @RequestParam(required = false) Long volId,
                      @RequestParam(required = false) StatutReservation statut,
                      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateMin,
                      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateMax,
                      Model model) {
        
        Specification<Reservation> spec = ReservationSpecification.filterBy(clientId, volId, statut, dateMin, dateMax);
        List<Reservation> reservations = reservationService.findAll(spec);
        
        model.addAttribute("reservations", reservations);
        model.addAttribute("clients", clientService.findAll());
        model.addAttribute("vols", volService.findAll());
        model.addAttribute("statuts", StatutReservation.values());
        model.addAttribute("clientId", clientId);
        model.addAttribute("volId", volId);
        model.addAttribute("statut", statut);
        model.addAttribute("dateMin", dateMin);
        model.addAttribute("dateMax", dateMax);
        
        return "reservation/list";
    }
    
    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("reservation", new Reservation());
        model.addAttribute("clients", clientService.findAll());
        model.addAttribute("vols", volService.findAll());
        model.addAttribute("statuts", StatutReservation.values());
        return "reservation/form";
    }
    
    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Reservation reservation = reservationService.findById(id)
            .orElseThrow(() -> new RuntimeException("Réservation non trouvée"));
        model.addAttribute("reservation", reservation);
        model.addAttribute("siegesLibres", siegeVolService.findSiegesLibres(reservation.getVol().getId()));
        return "reservation/detail";
    }
    
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Reservation reservation = reservationService.findById(id)
            .orElseThrow(() -> new RuntimeException("Réservation non trouvée"));
        model.addAttribute("reservation", reservation);
        model.addAttribute("clients", clientService.findAll());
        model.addAttribute("vols", volService.findAll());
        model.addAttribute("statuts", StatutReservation.values());
        return "reservation/form";
    }
    
    @PostMapping
    public String save(@ModelAttribute Reservation reservation, 
                      @RequestParam Long clientId,
                      @RequestParam Long volId,
                      RedirectAttributes redirectAttributes) {
        try {
            reservation.setClient(clientService.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Client non trouvé")));
            reservation.setVol(volService.findById(volId)
                .orElseThrow(() -> new RuntimeException("Vol non trouvé")));
            
            if (reservation.getId() == null) {
                reservationService.save(reservation);
                redirectAttributes.addFlashAttribute("success", "Réservation créée avec succès");
            } else {
                reservationService.update(reservation.getId(), reservation);
                redirectAttributes.addFlashAttribute("success", "Réservation modifiée avec succès");
            }
            return "redirect:/reservations";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur: " + e.getMessage());
            return "redirect:/reservations/new";
        }
    }
    
    @PostMapping("/{id}/billet")
    public String creerBillet(@PathVariable Long id, @RequestParam Long siegeVolId, RedirectAttributes redirectAttributes) {
        try {
            Reservation reservation = reservationService.findById(id)
                .orElseThrow(() -> new RuntimeException("Réservation non trouvée"));
            reservationService.creerBillet(reservation, siegeVolId);
            redirectAttributes.addFlashAttribute("success", "Billet créé avec succès");
            return "redirect:/reservations/" + id;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur: " + e.getMessage());
            return "redirect:/reservations/" + id;
        }
    }
    
    @GetMapping("/{id}/annuler")
    public String annuler(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            reservationService.annulerReservation(id);
            redirectAttributes.addFlashAttribute("success", "Réservation annulée avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur: " + e.getMessage());
        }
        return "redirect:/reservations";
    }
    
    @GetMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            reservationService.delete(id);
            redirectAttributes.addFlashAttribute("success", "Réservation supprimée avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur: " + e.getMessage());
        }
        return "redirect:/reservations";
    }
}
