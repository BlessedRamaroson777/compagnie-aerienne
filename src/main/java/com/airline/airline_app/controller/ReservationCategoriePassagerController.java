package com.airline.airline_app.controller;

import com.airline.airline_app.entity.ReservationCategoriePassager;
import com.airline.airline_app.repository.ReservationCategoriePassagerRepository;
import com.airline.airline_app.repository.CategoriePassagerRepository;
import com.airline.airline_app.repository.ClasseSiegeRepository;
import com.airline.airline_app.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/reservation-categories")
@RequiredArgsConstructor
public class ReservationCategoriePassagerController {

    private final ReservationCategoriePassagerRepository reservationCategoriePassagerRepository;
    private final CategoriePassagerRepository categoriePassagerRepository;
    private final ClasseSiegeRepository classeSiegeRepository;
    private final ReservationRepository reservationRepository;

    @GetMapping
    public String list(
            @RequestParam(required = false) Long reservationId,
            @RequestParam(required = false) Long categorieId,
            @RequestParam(required = false) Long classeId,
            Model model) {
        
        List<ReservationCategoriePassager> categories;
        
        // Appliquer les filtres
        if (reservationId != null) {
            categories = reservationCategoriePassagerRepository.findByReservation_Id(reservationId);
        } else if (categorieId != null) {
            categories = reservationCategoriePassagerRepository.findByCategoriePassager_IdCategoriePassager(categorieId);
        } else if (classeId != null) {
            categories = reservationCategoriePassagerRepository.findByClasseSiege_Id(classeId);
        } else {
            categories = reservationCategoriePassagerRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
        }
        
        model.addAttribute("categories", categories);
        model.addAttribute("categoriesPassagers", categoriePassagerRepository.findAll());
        model.addAttribute("classessieges", classeSiegeRepository.findAll());
        model.addAttribute("reservations", reservationRepository.findAll());
        model.addAttribute("selectedReservationId", reservationId);
        model.addAttribute("selectedCategorieId", categorieId);
        model.addAttribute("selectedClasseId", classeId);
        
        return "reservation-categorie/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        ReservationCategoriePassager categorie = reservationCategoriePassagerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Catégorie de réservation non trouvée"));
        
        model.addAttribute("categorie", categorie);
        return "reservation-categorie/detail";
    }
}
