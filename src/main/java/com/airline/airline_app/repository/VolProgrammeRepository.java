package com.airline.airline_app.repository;

import com.airline.airline_app.entity.VolProgramme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VolProgrammeRepository
        extends JpaRepository<VolProgramme, Long>, JpaSpecificationExecutor<VolProgramme> {

    List<VolProgramme> findByVol_Id(Long volId);

    List<VolProgramme> findByVol_IdAndDateHeureDepartBetween(Long volId, LocalDateTime start, LocalDateTime end);

    List<VolProgramme> findByVol_AeroportDepart_IdAndVol_AeroportArrivee_IdAndDateHeureDepartBetween(
            Long aeroportDepartId,
            Long aeroportArriveeId,
            LocalDateTime start,
            LocalDateTime end);
    
    List<VolProgramme> findByVol_AeroportDepart_IdAndVol_AeroportArrivee_Id(
            Long aeroportDepartId,
            Long aeroportArriveeId);
}
