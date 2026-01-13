package com.airline.airline_app.service;

import com.airline.airline_app.entity.PrixVol;
import com.airline.airline_app.repository.PrixVolRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PrixVolService extends GenericServiceImpl<PrixVol, Long> {

    private final PrixVolRepository prixVolRepository;

    @Override
    protected JpaRepository<PrixVol, Long> getRepository() {
        return prixVolRepository;
    }

    @Override
    protected JpaSpecificationExecutor<PrixVol> getSpecificationExecutor() {
        return prixVolRepository;
    }

    public List<PrixVol> findAll(Specification<PrixVol> specification) {
        return prixVolRepository.findAll(specification);
    }

    public List<PrixVol> findByVolProgrammeId(Long volProgrammeId) {
        return prixVolRepository.findByVolProgrammeId(volProgrammeId);
    }
    
    public List<PrixVol> findByVolProgrammeIdAndModelAvion(Long volProgrammeId) {
        List<PrixVol> allPrixVols = prixVolRepository.findByVolProgrammeId(volProgrammeId);
        // Filtrer pour ne garder que les classes disponibles dans le modèle d'avion
        return allPrixVols.stream()
                .filter(pv -> {
                    if (pv.getVolProgramme().getAvion() != null 
                            && pv.getVolProgramme().getAvion().getModelAvion() != null
                            && pv.getVolProgramme().getAvion().getModelAvion().getClassesDisponibles() != null) {
                        return pv.getVolProgramme().getAvion().getModelAvion().getClassesDisponibles()
                                .stream()
                                .anyMatch(c -> c.getId().equals(pv.getClasseSiege().getId()));
                    }
                    return false; // Si pas de modèle ou classes définies, ne pas afficher
                })
                .toList();
    }

    public Optional<PrixVol> findByVolProgrammeIdAndClasseSiegeId(Long volProgrammeId, Long classeSiegeId) {
        return prixVolRepository.findByVolProgrammeIdAndClasseSiegeId(volProgrammeId, classeSiegeId);
    }
}
