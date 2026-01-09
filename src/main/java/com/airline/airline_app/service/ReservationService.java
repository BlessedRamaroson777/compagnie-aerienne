package com.airline.airline_app.service;

import com.airline.airline_app.entity.*;
import com.airline.airline_app.entity.enums.StatutBillet;
import com.airline.airline_app.entity.enums.StatutReservation;
import com.airline.airline_app.repository.BilletRepository;
import com.airline.airline_app.repository.PrixVolRepository;
import com.airline.airline_app.repository.ReservationRepository;
import com.airline.airline_app.repository.SiegeVolRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationService extends GenericServiceImpl<Reservation, Long> {
    
    private final ReservationRepository reservationRepository;
    private final BilletRepository billetRepository;
    private final SiegeVolRepository siegeVolRepository;
    private final PrixVolRepository prixVolRepository;
    
    @Override
    protected JpaRepository<Reservation, Long> getRepository() {
        return reservationRepository;
    }
    
    @Override
    protected JpaSpecificationExecutor<Reservation> getSpecificationExecutor() {
        return reservationRepository;
    }
    
    @Override
    @Transactional
    public Reservation save(Reservation reservation) {
        if (reservation.getDateReservation() == null) {
            reservation.setDateReservation(LocalDateTime.now());
        }
        if (reservation.getStatut() == null) {
            reservation.setStatut(StatutReservation.CONFIRMEE);
        }
        return super.save(reservation);
    }
    
    /**
     * Crée un billet pour un siège spécifique
     */
    @Transactional
    public Billet creerBillet(Reservation reservation, Long siegeVolId) {
        SiegeVol siegeVol = siegeVolRepository.findById(siegeVolId)
            .orElseThrow(() -> new RuntimeException("Siège non trouvé"));
        
        if (siegeVol.getOccupe()) {
            throw new RuntimeException("Ce siège est déjà occupé");
        }
        
        // Récupérer le prix du vol pour la classe du siège
        PrixVol prixVol = prixVolRepository.findByVolIdAndClasseSiegeId(
            reservation.getVol().getId(),
            siegeVol.getSiege().getClasseSiege().getId()
        ).orElseThrow(() -> new RuntimeException("Prix non trouvé pour ce vol et cette classe"));
        
        Billet billet = new Billet();
        billet.setReservation(reservation);
        billet.setSiegeVol(siegeVol);
        billet.setPrix(prixVol.getPrix());
        billet.setStatut(StatutBillet.EMIS);
        
        siegeVol.setOccupe(true);
        siegeVolRepository.save(siegeVol);
        
        return billetRepository.save(billet);
    }
    
    /**
     * Annule une réservation et libère tous les sièges
     */
    @Transactional
    public void annulerReservation(Long reservationId) {
        Reservation reservation = findById(reservationId)
            .orElseThrow(() -> new RuntimeException("Réservation non trouvée"));
        
        reservation.setStatut(StatutReservation.ANNULEE);
        
        // Annuler tous les billets et libérer les sièges
        for (Billet billet : reservation.getBillets()) {
            billet.setStatut(StatutBillet.ANNULE);
            SiegeVol siegeVol = billet.getSiegeVol();
            siegeVol.setOccupe(false);
            siegeVolRepository.save(siegeVol);
        }
        
        reservationRepository.save(reservation);
    }
    
    public List<Reservation> findAll(Specification<Reservation> specification) {
        return reservationRepository.findAll(specification);
    }
}
