package com.airline.airline_app.controller;

import com.airline.airline_app.entity.*;
import com.airline.airline_app.entity.enums.StatutPaiement;
import com.airline.airline_app.entity.enums.StatutReservation;
import com.airline.airline_app.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Controller
@RequestMapping("/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;
    private final ClientService clientService;
    private final VolService volService;
    private final VolProgrammeService volProgrammeService;
    private final ClasseSiegeService classeSiegeService;
    private final PrixVolService prixVolService;
    private final PaiementService paiementService;
    private final AeroportService aeroportService;

    @GetMapping
    public String list(Model model) {
        List<Reservation> reservations = reservationService.findAll();
        model.addAttribute("reservations", reservations);
        return "reservation/list";
    }

    // Étape 1: Recherche de vols
    @GetMapping("/new")
    public String rechercheVol(Model model) {
        model.addAttribute("aeroports", aeroportService.findAll());
        return "reservation/recherche-vol";
    }

    // Étape 2: Liste des vols programmés disponibles
    @GetMapping("/vols-disponibles")
    public String volsDisponibles(@RequestParam Long aeroportDepartId,
            @RequestParam Long aeroportArriveeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateVol,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime heureVol,
            Model model) {
        // Rechercher les vols programmés pour cette route et date
        List<VolProgramme> volsProgrammes = volProgrammeService.findByAeroportsAndDate(
                aeroportDepartId, aeroportArriveeId, dateVol);

        // Filtrer par heure si spécifiée
        if (heureVol != null) {
            volsProgrammes = volsProgrammes.stream()
                    .filter(vp -> vp.getDateHeureDepart().toLocalTime().equals(heureVol))
                    .toList();
        }

        model.addAttribute("volsProgrammes", volsProgrammes);
        model.addAttribute("aeroportDepart", aeroportService.findById(aeroportDepartId).orElse(null));
        model.addAttribute("aeroportArrivee", aeroportService.findById(aeroportArriveeId).orElse(null));
        model.addAttribute("dateVol", dateVol);
        model.addAttribute("heureVol", heureVol);
        return "reservation/vols-disponibles";
    }

    // Étape 3: Configuration de la réservation
    @GetMapping("/configurer/{volProgrammeId}")
    public String configurerReservation(@PathVariable Long volProgrammeId, Model model) {
        VolProgramme volProgramme = volProgrammeService.findById(volProgrammeId)
                .orElseThrow(() -> new RuntimeException("Vol programmé non trouvé"));

        List<PrixVol> prixVols = prixVolService.findByVolProgrammeId(volProgrammeId);

        model.addAttribute("volProgramme", volProgramme);
        model.addAttribute("prixVols", prixVols);
        model.addAttribute("clients", clientService.findAll());
        return "reservation/configurer";
    }

    // API pour obtenir le prix
    @GetMapping("/prix/{volProgrammeId}/{classeSiegeId}")
    @ResponseBody
    public BigDecimal getPrix(@PathVariable Long volProgrammeId, @PathVariable Long classeSiegeId) {
        return prixVolService.findByVolProgrammeIdAndClasseSiegeId(volProgrammeId, classeSiegeId)
                .map(PrixVol::getPrix)
                .orElse(BigDecimal.ZERO);
    }

    // Étape 4: Confirmation
    @PostMapping("/confirmer")
    public String confirmerReservation(@RequestParam Long volProgrammeId,
            @RequestParam Long clientId,
            @RequestParam Long classeSiegeId,
            @RequestParam Integer nombrePlaces,
            RedirectAttributes redirectAttributes) {
        try {
            VolProgramme volProgramme = volProgrammeService.findById(volProgrammeId)
                    .orElseThrow(() -> new RuntimeException("Vol programmé non trouvé"));
            Client client = clientService.findById(clientId)
                    .orElseThrow(() -> new RuntimeException("Client non trouvé"));
            ClasseSiege classeSiege = classeSiegeService.findById(classeSiegeId)
                    .orElseThrow(() -> new RuntimeException("Classe de siège non trouvée"));

            PrixVol prixVol = prixVolService.findByVolProgrammeIdAndClasseSiegeId(volProgrammeId, classeSiegeId)
                    .orElseThrow(() -> new RuntimeException("Prix non défini pour ce vol et cette classe"));

            BigDecimal prixTotal = prixVol.getPrix().multiply(BigDecimal.valueOf(nombrePlaces));

            Reservation reservation = new Reservation();
            reservation.setClient(client);
            reservation.setVolProgramme(volProgramme);
            reservation.setClasseSiege(classeSiege);
            reservation.setNombrePlaces(nombrePlaces);
            reservation.setPrixTotal(prixTotal);
            reservation.setStatut(StatutReservation.EN_ATTENTE);

            Reservation savedReservation = reservationService.save(reservation);

            return "redirect:/reservations/paiement/" + savedReservation.getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur: " + e.getMessage());
            return "redirect:/reservations/new";
        }
    }

    // Étape 5: Paiement
    @GetMapping("/paiement/{id}")
    public String pagePaiement(@PathVariable Long id, Model model) {
        Reservation reservation = reservationService.findById(id)
                .orElseThrow(() -> new RuntimeException("Réservation non trouvée"));
        model.addAttribute("reservation", reservation);
        return "reservation/paiement";
    }

    @PostMapping("/paiement/{id}")
    public String effectuerPaiement(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Reservation reservation = reservationService.findById(id)
                    .orElseThrow(() -> new RuntimeException("Réservation non trouvée"));

            Paiement paiement = new Paiement();
            paiement.setReservation(reservation);
            paiement.setMontant(reservation.getPrixTotal());
            paiement.setStatut(StatutPaiement.VALIDE);
            paiementService.save(paiement);

            reservation.setStatut(StatutReservation.CONFIRMEE);
            reservationService.update(reservation.getId(), reservation);

            redirectAttributes.addFlashAttribute("success", "Réservation confirmée et paiement effectué!");
            return "redirect:/reservations/" + id;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur: " + e.getMessage());
            return "redirect:/reservations/paiement/" + id;
        }
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Reservation reservation = reservationService.findById(id)
                .orElseThrow(() -> new RuntimeException("Réservation non trouvée"));
        model.addAttribute("reservation", reservation);
        return "reservation/detail";
    }

    @GetMapping("/{id}/annuler")
    public String annuler(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Reservation reservation = reservationService.findById(id)
                    .orElseThrow(() -> new RuntimeException("Réservation non trouvée"));
            reservation.setStatut(StatutReservation.ANNULEE);
            reservationService.update(id, reservation);
            redirectAttributes.addFlashAttribute("success", "Réservation annulée avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur: " + e.getMessage());
        }
        return "redirect:/reservations";
    }
}
