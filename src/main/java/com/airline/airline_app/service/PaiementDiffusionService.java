package com.airline.airline_app.service;

import com.airline.airline_app.entity.DiffusionSociete;
import com.airline.airline_app.entity.PaiementDiffusion;
import com.airline.airline_app.repository.PaiementDiffusionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class PaiementDiffusionService {

    @Autowired
    private PaiementDiffusionRepository paiementDiffusionRepository;

    @Autowired
    private DiffusionSocieteService diffusionSocieteService;

    public List<PaiementDiffusion> findAll() {
        return paiementDiffusionRepository.findAll();
    }

    public Optional<PaiementDiffusion> findById(Long id) {
        return paiementDiffusionRepository.findById(id);
    }

    public List<PaiementDiffusion> findByDiffusionSocieteId(Long idDiffusionSociete) {
        return paiementDiffusionRepository.findByDiffusionSocieteIdDiffusionSociete(idDiffusionSociete);
    }

    public BigDecimal getTotalPaye(Long idDiffusionSociete) {
        return paiementDiffusionRepository.sumMontantPayeByDiffusionSociete(idDiffusionSociete);
    }

    @Transactional
    public PaiementDiffusion save(PaiementDiffusion paiement) {
        // Validation : vérifier que le total des paiements ne dépasse pas le montant total à payer
        DiffusionSociete diffusion = paiement.getDiffusionSociete();
        BigDecimal montantTotal = diffusionSocieteService.getMontantTotal(diffusion.getIdDiffusionSociete());
        BigDecimal totalPaye = getTotalPaye(diffusion.getIdDiffusionSociete());
        
        // Si on modifie un paiement existant, soustraire l'ancien montant
        if (paiement.getIdPaiementDiffusion() != null) {
            Optional<PaiementDiffusion> ancienPaiement = findById(paiement.getIdPaiementDiffusion());
            if (ancienPaiement.isPresent()) {
                totalPaye = totalPaye.subtract(ancienPaiement.get().getMontantPaye());
            }
        }
        
        BigDecimal nouveauTotal = totalPaye.add(paiement.getMontantPaye());
        
        if (nouveauTotal.compareTo(montantTotal) > 0) {
            throw new IllegalArgumentException(
                String.format("Le montant total des paiements (%.2f) dépasse le montant à payer (%.2f). Reste à payer : %.2f",
                    nouveauTotal.doubleValue(), 
                    montantTotal.doubleValue(),
                    montantTotal.subtract(totalPaye).doubleValue())
            );
        }
        
        return paiementDiffusionRepository.save(paiement);
    }

    public void deleteById(Long id) {
        paiementDiffusionRepository.deleteById(id);
    }
}
