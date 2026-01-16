package com.airline.airline_app.controller;

import com.airline.airline_app.entity.*;
import com.airline.airline_app.entity.enums.StatutPaiement;
import com.airline.airline_app.entity.enums.StatutReservation;
import com.airline.airline_app.repository.AvionClasseConfigurationRepository;
import com.airline.airline_app.repository.CategoriePassagerRepository;
import com.airline.airline_app.repository.RemiseCategoriePlaceRepository;
import com.airline.airline_app.repository.ReservationCategoriePassagerRepository;
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
import java.util.Map;

@Controller
@RequestMapping("/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;
    private final ClientService clientService;
    private final VolService volService;
    private final VolProgrammeService volProgrammeService;
    private final ClasseSiegeService classeSiegeService;
    private final VolProgrammeClassePrixService volProgrammeClassePrixService;
    private final PaiementService paiementService;
    private final AeroportService aeroportService;
    private final BilletService billetService;
    private final SiegeVolService siegeVolService;
    private final AvionClasseConfigurationRepository avionClasseConfigurationRepository;
    private final CategoriePassagerRepository categoriePassagerRepository;
    private final RemiseCategoriePlaceRepository remiseCategoriePlaceRepository;
    private final ReservationCategoriePassagerRepository reservationCategoriePassagerRepository;

    @GetMapping
    public String list(Model model) {
        List<Reservation> reservations = reservationService.findAll();
        model.addAttribute("reservations", reservations);
        return "reservation/list";
    }

    // Étape 1: Recherche de vols (seulement aéroports)
    @GetMapping("/new")
    public String rechercheVol(Model model) {
        model.addAttribute("aeroports", aeroportService.findAll());
        return "reservation/recherche-vol";
    }

    // Étape 2: Liste des dates/heures disponibles pour la route
    @GetMapping("/vols-disponibles")
    public String volsDisponibles(@RequestParam Long aeroportDepartId,
            @RequestParam Long aeroportArriveeId,
            Model model) {
        // Rechercher TOUS les vols programmés pour cette route (toutes dates/heures)
        List<VolProgramme> volsProgrammes = volProgrammeService.findByAeroportsAllDates(
                aeroportDepartId, aeroportArriveeId);

        model.addAttribute("volsProgrammes", volsProgrammes);
        model.addAttribute("aeroportDepart", aeroportService.findById(aeroportDepartId).orElse(null));
        model.addAttribute("aeroportArrivee", aeroportService.findById(aeroportArriveeId).orElse(null));
        return "reservation/vols-disponibles";
    }

    // Étape 3: Configuration de la réservation avec choix de sièges
    @GetMapping("/configurer/{volProgrammeId}")
    public String configurerReservation(@PathVariable Long volProgrammeId, Model model) {
        VolProgramme volProgramme = volProgrammeService.findById(volProgrammeId)
                .orElseThrow(() -> new RuntimeException("Vol programmé non trouvé"));

        // Obtenir les prix par classe pour ce vol programmé
        List<VolProgrammeClassePrix> prixParClasse = volProgrammeClassePrixService.getPrixByVolProgramme(volProgrammeId);
        
        // Obtenir les configurations de sièges de l'avion
        List<AvionClasseConfiguration> configurations = avionClasseConfigurationRepository.findByAvionId(volProgramme.getAvion().getId());

        // Obtenir toutes les catégories de passagers
        List<CategoriePassager> categories = categoriePassagerRepository.findAll();
        
        // Obtenir les remises pour ce vol programmé
        List<RemiseCategoriePlace> remises = remiseCategoriePlaceRepository.findByVolProgramme_Id(volProgrammeId);

        model.addAttribute("volProgramme", volProgramme);
        model.addAttribute("prixParClasse", prixParClasse);
        model.addAttribute("configurations", configurations);
        model.addAttribute("clients", clientService.findAll());
        model.addAttribute("categories", categories);
        model.addAttribute("remises", remises);
        return "reservation/configurer";
    }

    // API pour obtenir le prix
    @GetMapping("/prix/{volProgrammeId}/{classeSiegeId}")
    @ResponseBody
    public BigDecimal getPrix(@PathVariable Long volProgrammeId, @PathVariable Long classeSiegeId) {
        return volProgrammeClassePrixService.getPrixByVolProgramme(volProgrammeId).stream()
                .filter(p -> p.getClasseSiege().getId().equals(classeSiegeId))
                .map(VolProgrammeClassePrix::getPrix)
                .findFirst()
                .orElse(BigDecimal.ZERO);
    }

    // Étape 4: Confirmation
    @PostMapping("/confirmer")
    public String confirmerReservation(@RequestParam Long volProgrammeId,
            @RequestParam Long clientId,
            @RequestParam Map<String, String> allParams,
            RedirectAttributes redirectAttributes) {
        try {
            VolProgramme volProgramme = volProgrammeService.findById(volProgrammeId)
                    .orElseThrow(() -> new RuntimeException("Vol programmé non trouvé"));
            Client client = clientService.findById(clientId)
                    .orElseThrow(() -> new RuntimeException("Client non trouvé"));

            // Créer la réservation principale
            Reservation reservation = new Reservation();
            reservation.setClient(client);
            reservation.setVolProgramme(volProgramme);
            reservation.setStatut(StatutReservation.EN_ATTENTE);
            
            BigDecimal prixTotal = BigDecimal.ZERO;
            int totalPlaces = 0;
            
            // Parser les paramètres categorie_{classeSiegeId}_{categoriePassagerId}
            for (Map.Entry<String, String> entry : allParams.entrySet()) {
                if (entry.getKey().startsWith("categorie_")) {
                    int nombrePassagers = Integer.parseInt(entry.getValue());
                    if (nombrePassagers > 0) {
                        // Parse categorie_{classeSiegeId}_{categoriePassagerId}
                        String[] parts = entry.getKey().replace("categorie_", "").split("_");
                        Long classeSiegeId = Long.parseLong(parts[0]);
                        Long categoriePassagerId = Long.parseLong(parts[1]);
                        
                        ClasseSiege classeSiege = classeSiegeService.findById(classeSiegeId)
                                .orElseThrow(() -> new RuntimeException("Classe de siège non trouvée"));
                        
                        CategoriePassager categoriePassager = categoriePassagerRepository.findById(categoriePassagerId)
                                .orElseThrow(() -> new RuntimeException("Catégorie passager non trouvée"));
                        
                        // Obtenir le prix de base pour cette classe
                        VolProgrammeClassePrix prixClasse = volProgrammeClassePrixService.getPrixByVolProgramme(volProgrammeId).stream()
                                .filter(p -> p.getClasseSiege().getId().equals(classeSiegeId))
                                .findFirst()
                                .orElseThrow(() -> new RuntimeException("Prix non défini pour cette classe"));
                        
                        // Obtenir la remise pour cette catégorie
                        List<RemiseCategoriePlace> remises = remiseCategoriePlaceRepository
                                .findByVolProgramme_IdAndClasseSiege_Id(volProgrammeId, classeSiegeId);
                        
                        BigDecimal remise = BigDecimal.ZERO;
                        for (RemiseCategoriePlace r : remises) {
                            if (r.getCategoriePassager().getIdCategoriePassager().equals(categoriePassagerId)) {
                                remise = BigDecimal.valueOf(r.getRemisePrix());
                                break;
                            }
                        }
                        
                        // Calculer le prix après remise
                        BigDecimal prixUnitaire = prixClasse.getPrix();
                        BigDecimal prixApresRemise = prixUnitaire.subtract(remise);
                        BigDecimal sousTotal = prixApresRemise.multiply(BigDecimal.valueOf(nombrePassagers));
                        
                        prixTotal = prixTotal.add(sousTotal);
                        totalPlaces += nombrePassagers;
                        
                        // Créer un enregistrement ReservationCategoriePassager
                        ReservationCategoriePassager rcp = new ReservationCategoriePassager();
                        rcp.setReservation(reservation);
                        rcp.setClasseSiege(classeSiege);
                        rcp.setCategoriePassager(categoriePassager);
                        rcp.setNombrePassagers(nombrePassagers);
                        rcp.setPrixUnitaire(prixUnitaire.doubleValue());
                        rcp.setRemiseUnitaire(remise.doubleValue());
                        rcp.calculerPrixApresRemise();
                        rcp.calculerSousTotal();
                        
                        reservation.getCategoriesPassagers().add(rcp);
                    }
                }
            }
            
            if (totalPlaces == 0) {
                throw new RuntimeException("Aucune place sélectionnée");
            }
            
            reservation.setNombrePlaces(totalPlaces);
            reservation.setPrixTotal(prixTotal);
            // Garder la classe principale pour compatibilité (première classe sélectionnée)
            if (!reservation.getCategoriesPassagers().isEmpty()) {
                reservation.setClasseSiege(reservation.getCategoriesPassagers().get(0).getClasseSiege());
            }

            Reservation savedReservation = reservationService.save(reservation);

            // Générer automatiquement les billets par catégorie
            for (ReservationCategoriePassager rcp : savedReservation.getCategoriesPassagers()) {
                // Pour chaque passager dans cette catégorie
                for (int i = 0; i < rcp.getNombrePassagers(); i++) {
                    // Trouver un siège disponible de cette classe
                    List<SiegeVol> siegesDisponibles = siegeVolService.findDisponiblesByVolProgrammeAndClasse(
                        volProgrammeId, rcp.getClasseSiege().getId());
                    
                    if (!siegesDisponibles.isEmpty()) {
                        SiegeVol siegeVol = siegesDisponibles.get(0);
                        
                        // Créer le billet avec le prix après remise
                        Billet billet = new Billet();
                        billet.setReservation(savedReservation);
                        billet.setSiegeVol(siegeVol);
                        billet.setPrix(BigDecimal.valueOf(rcp.getPrixApresRemise()));
                        billet.setStatut(com.airline.airline_app.entity.enums.StatutBillet.EMIS);
                        billetService.save(billet);
                        
                        // Marquer le siège comme occupé
                        siegeVol.setOccupe(true);
                        siegeVolService.update(siegeVol.getId(), siegeVol);
                    }
                }
            }

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
        
        // Charger les détails par catégorie
        List<ReservationCategoriePassager> categoriesPassagers = 
                reservationCategoriePassagerRepository.findByReservation_Id(id);
        
        model.addAttribute("reservation", reservation);
        model.addAttribute("categoriesPassagers", categoriesPassagers);
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
        
        // Charger les détails par catégorie
        List<ReservationCategoriePassager> categoriesPassagers = 
                reservationCategoriePassagerRepository.findByReservation_Id(id);
        
        model.addAttribute("reservation", reservation);
        model.addAttribute("categoriesPassagers", categoriesPassagers);
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
