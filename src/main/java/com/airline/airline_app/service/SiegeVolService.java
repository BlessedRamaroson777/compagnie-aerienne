package com.airline.airline_app.service;

import com.airline.airline_app.entity.SiegeVol;
import com.airline.airline_app.repository.SiegeVolRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SiegeVolService extends GenericServiceImpl<SiegeVol, Long> {
    
    private final SiegeVolRepository siegeVolRepository;
    
    @Override
    protected JpaRepository<SiegeVol, Long> getRepository() {
        return siegeVolRepository;
    }
    
    @Override
    protected JpaSpecificationExecutor<SiegeVol> getSpecificationExecutor() {
        return siegeVolRepository;
    }
    
    public List<SiegeVol> findByVolProgrammeId(Long volProgrammeId) {
        return siegeVolRepository.findByVolProgramme_Id(volProgrammeId);
    }
    
    public List<SiegeVol> findSiegesLibres(Long volProgrammeId) {
        return siegeVolRepository.findByVolProgramme_IdAndOccupe(volProgrammeId, false);
    }
    
    public List<SiegeVol> findDisponiblesByVolProgrammeAndClasse(Long volProgrammeId, Long classeSiegeId) {
        return siegeVolRepository.findByVolProgramme_IdAndOccupe(volProgrammeId, false)
            .stream()
            .filter(sv -> sv.getSiege() != null && 
                         sv.getSiege().getClasseSiege() != null && 
                         sv.getSiege().getClasseSiege().getId().equals(classeSiegeId))
            .toList();
    }
}
