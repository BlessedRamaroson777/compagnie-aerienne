package com.airline.airline_app.repository;

import com.airline.airline_app.entity.Paiement;
import com.airline.airline_app.entity.Reservation;
import com.airline.airline_app.entity.enums.StatutPaiement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface PaiementRepository extends JpaRepository<Paiement, Long>, JpaSpecificationExecutor<Paiement> {
    
    List<Paiement> findByReservation(Reservation reservation);
    
    List<Paiement> findByReservationAndStatut(Reservation reservation, StatutPaiement statut);
    
    @Query("SELECT COALESCE(SUM(p.montant), 0) FROM Paiement p WHERE p.reservation = :reservation AND p.statut = :statut")
    BigDecimal sumMontantByReservationAndStatut(@Param("reservation") Reservation reservation, @Param("statut") StatutPaiement statut);
}
