package com.airline.airline_app.service;

import com.airline.airline_app.entity.MethodePaiement;
import com.airline.airline_app.entity.Paiement;
import com.airline.airline_app.entity.PaiementMethode;
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
    
    @Override
    @Transactional
    public Paiement save(Paiement paiement) {
        if (paiement.getDatePaiement() == null) {
            paiement.setDatePaiement(LocalDateTime.now());
        }
        if (paiement.getStatut() == null) {
            paiement.setStatut(StatutPaiement.EN_ATTENTE);
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
