package com.airline.airline_app.service;

import com.airline.airline_app.entity.MethodePaiement;
import com.airline.airline_app.entity.Paiement;
import com.airline.airline_app.entity.PaiementMethode;
import com.airline.airline_app.entity.Reservation;
import com.airline.airline_app.entity.enums.StatutPaiement;
import com.airline.airline_app.repository.MethodePaiementRepository;
import com.airline.airline_app.repository.PaiementMethodeRepository;
import com.airline.airline_app.repository.PaiementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PaiementService extends GenericServiceImpl<Paiement, Long> {
    
    private final PaiementRepository paiementRepository;
    private final PaiementMethodeRepository paiementMethodeRepository;
    private final MethodePaiementRepository methodePaiementRepository;
    
    @Override
    protected JpaRepository<Paiement, Long> getRepository() {
        return paiementRepository;
    }
    
    @Override
    protected JpaSpecificationExecutor<Paiement> getSpecificationExecutor() {
        return paiementRepository;
    }
    
    /**
     * Calcule le montant total déjà payé (paiements VALIDE) pour une réservation
     */
    public BigDecimal getMontantDejaPaye(Reservation reservation) {
        return paiementRepository.sumMontantByReservationAndStatut(reservation, StatutPaiement.VALIDE);
    }
    
    /**
     * Calcule le montant restant à payer pour une réservation
     */
    public BigDecimal getMontantRestant(Reservation reservation) {
        BigDecimal montantTotal = reservation.getPrixTotal();
        BigDecimal montantPaye = getMontantDejaPaye(reservation);
        return montantTotal.subtract(montantPaye);
    }
    
    /**
     * Vérifie si la réservation est entièrement payée
     */
    public boolean estEntierementPayee(Reservation reservation) {
        return getMontantRestant(reservation).compareTo(BigDecimal.ZERO) <= 0;
    }
    
    /**
     * Valide un paiement: vérifie qu'il ne dépasse pas le montant restant
     */
    public String validerPaiement(Reservation reservation, BigDecimal montantPaiement) {
        if (estEntierementPayee(reservation)) {
            return "Cette réservation est déjà entièrement payée. Montant total: " + 
                   reservation.getPrixTotal() + " Ar, Déjà payé: " + getMontantDejaPaye(reservation) + " Ar";
        }
        
        BigDecimal montantRestant = getMontantRestant(reservation);
        if (montantPaiement.compareTo(montantRestant) > 0) {
            return "Le montant du paiement (" + montantPaiement + " Ar) dépasse le montant restant à payer (" + 
                   montantRestant + " Ar). Montant maximum accepté: " + montantRestant + " Ar";
        }
        
        return null; // Validation OK
    }
    
    @Override
    @Transactional
    public Paiement save(Paiement paiement) {
        if (paiement.getDatePaiement() == null) {
            paiement.setDatePaiement(LocalDateTime.now());
        }
        // Par défaut, le statut est VALIDE (confirmé)
        if (paiement.getStatut() == null) {
            paiement.setStatut(StatutPaiement.VALIDE);
        }
        return super.save(paiement);
    }
    
    /**
     * Effectue un paiement multi-méthodes
     * @param paiement Le paiement à effectuer
     * @param repartition Map<MethodePaiementId, Montant>
     */
    @Transactional
    public Paiement effectuerPaiementMultiMethodes(Paiement paiement, Map<Long, BigDecimal> repartition) {
        Paiement savedPaiement = save(paiement);
        
        BigDecimal totalRepartition = BigDecimal.ZERO;
        
        for (Map.Entry<Long, BigDecimal> entry : repartition.entrySet()) {
            MethodePaiement methodePaiement = methodePaiementRepository.findById(entry.getKey())
                .orElseThrow(() -> new RuntimeException("Méthode de paiement non trouvée"));
            
            PaiementMethode paiementMethode = new PaiementMethode();
            paiementMethode.setPaiement(savedPaiement);
            paiementMethode.setMethodePaiement(methodePaiement);
            paiementMethode.setMontant(entry.getValue());
            
            paiementMethodeRepository.save(paiementMethode);
            totalRepartition = totalRepartition.add(entry.getValue());
        }
        
        if (totalRepartition.compareTo(paiement.getMontant()) != 0) {
            throw new RuntimeException("La somme des montants ne correspond pas au montant total du paiement");
        }
        
        savedPaiement.setStatut(StatutPaiement.VALIDE);
        return update(savedPaiement.getId(), savedPaiement);
    }
    
    public List<Paiement> findAll(Specification<Paiement> specification) {
        return paiementRepository.findAll(specification);
    }
}
