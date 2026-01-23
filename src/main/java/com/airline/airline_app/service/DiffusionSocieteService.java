package com.airline.airline_app.service;

import com.airline.airline_app.entity.ConfigurationCoutDiffusion;
import com.airline.airline_app.entity.DiffusionSociete;
import com.airline.airline_app.entity.DiffusionVolProgramme;
import com.airline.airline_app.repository.ConfigurationCoutDiffusionRepository;
import com.airline.airline_app.repository.DiffusionSocieteRepository;
import com.airline.airline_app.repository.DiffusionVolProgrammeRepository;
import com.airline.airline_app.repository.PaiementDiffusionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class DiffusionSocieteService {

    @Autowired
    private DiffusionSocieteRepository diffusionSocieteRepository;

    @Autowired
    private DiffusionVolProgrammeRepository diffusionVolProgrammeRepository;

    @Autowired
    private ConfigurationCoutDiffusionRepository configurationCoutDiffusionRepository;

    @Autowired
    private PaiementDiffusionRepository paiementDiffusionRepository;

    public List<DiffusionSociete> findAll() {
        return diffusionSocieteRepository.findAll();
    }

    public Page<DiffusionSociete> findAll(Specification<DiffusionSociete> spec, Pageable pageable) {
        return diffusionSocieteRepository.findAll(spec, pageable);
    }

    public Optional<DiffusionSociete> findById(Long id) {
        return diffusionSocieteRepository.findById(id);
    }

    public DiffusionSociete save(DiffusionSociete diffusionSociete) {
        return diffusionSocieteRepository.save(diffusionSociete);
    }

    public void deleteById(Long id) {
        diffusionSocieteRepository.deleteById(id);
    }

    public List<DiffusionSociete> findBySocieteId(Long idSociete) {
        return diffusionSocieteRepository.findBySocieteIdSociete(idSociete);
    }

    /**
     * Calcule le montant total à payer pour une diffusion société
     * Montant = Somme(nombre * prix) pour chaque DiffusionVolProgramme associé
     */
    public BigDecimal getMontantTotal(Long idDiffusionSociete) {
        List<DiffusionVolProgramme> diffusionsVol = diffusionVolProgrammeRepository
            .findByDiffusionSocieteIdDiffusionSociete(idDiffusionSociete);
        
        BigDecimal montantTotal = BigDecimal.ZERO;
        
        for (DiffusionVolProgramme dvp : diffusionsVol) {
            // Récupérer la configuration de coût pour cette diffusion société
            Optional<ConfigurationCoutDiffusion> configOpt = configurationCoutDiffusionRepository
                .findByDiffusionSocieteIdDiffusionSociete(idDiffusionSociete)
                .stream()
                .findFirst();
            
            if (configOpt.isPresent()) {
                BigDecimal prix = BigDecimal.valueOf(configOpt.get().getPrix());
                BigDecimal nombre = BigDecimal.valueOf(dvp.getNombre());
                montantTotal = montantTotal.add(prix.multiply(nombre));
            }
        }
        
        return montantTotal;
    }

    /**
     * Calcule le montant total payé pour une diffusion société
     */
    public BigDecimal getMontantPaye(Long idDiffusionSociete) {
        return paiementDiffusionRepository.sumMontantPayeByDiffusionSociete(idDiffusionSociete);
    }

    /**
     * Calcule le reste à payer pour une diffusion société
     */
    public BigDecimal getResteAPayer(Long idDiffusionSociete) {
        BigDecimal montantTotal = getMontantTotal(idDiffusionSociete);
        BigDecimal montantPaye = getMontantPaye(idDiffusionSociete);
        return montantTotal.subtract(montantPaye);
    }
}
