package com.airline.airline_app.service;

import com.airline.airline_app.entity.Siege;
import com.airline.airline_app.entity.SiegeVol;
import com.airline.airline_app.entity.VolProgramme;
import com.airline.airline_app.repository.SiegeRepository;
import com.airline.airline_app.repository.SiegeVolRepository;
import com.airline.airline_app.repository.VolProgrammeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VolProgrammeService extends GenericServiceImpl<VolProgramme, Long> {

    private final VolProgrammeRepository volProgrammeRepository;
    private final SiegeRepository siegeRepository;
    private final SiegeVolRepository siegeVolRepository;

    @Override
    protected JpaRepository<VolProgramme, Long> getRepository() {
        return volProgrammeRepository;
    }

    @Override
    protected JpaSpecificationExecutor<VolProgramme> getSpecificationExecutor() {
        return volProgrammeRepository;
    }
    
    @Override
    @Transactional
    public VolProgramme save(VolProgramme volProgramme) {
        VolProgramme savedVolProgramme = super.save(volProgramme);
        creerSiegesVolProgramme(savedVolProgramme);
        return savedVolProgramme;
    }
    
    /**
     * Crée automatiquement les sièges pour un vol programmé basé sur l'avion
     */
    private void creerSiegesVolProgramme(VolProgramme volProgramme) {
        if (volProgramme.getAvion() == null) {
            return;
        }
        
        List<Siege> sieges = siegeRepository.findAll((root, query, cb) -> 
            cb.equal(root.get("avion").get("id"), volProgramme.getAvion().getId())
        );
        
        for (Siege siege : sieges) {
            SiegeVol siegeVol = new SiegeVol();
            siegeVol.setVolProgramme(volProgramme);
            siegeVol.setSiege(siege);
            siegeVol.setOccupe(false);
            siegeVolRepository.save(siegeVol);
        }
    }

    public List<VolProgramme> findByVolId(Long volId) {
        return volProgrammeRepository.findByVol_Id(volId);
    }

    public List<VolProgramme> findByAeroportsAndDate(Long aeroportDepartId, Long aeroportArriveeId, LocalDate date) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atTime(LocalTime.MAX);
        return volProgrammeRepository.findByVol_AeroportDepart_IdAndVol_AeroportArrivee_IdAndDateHeureDepartBetween(
                aeroportDepartId, aeroportArriveeId, start, end);
    }
    
    public List<VolProgramme> findByAeroportsAllDates(Long aeroportDepartId, Long aeroportArriveeId) {
        return volProgrammeRepository.findByVol_AeroportDepart_IdAndVol_AeroportArrivee_Id(
                aeroportDepartId, aeroportArriveeId);
    }
}
