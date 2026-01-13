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

    List<VolProgramme> findByVolId(Long volId);

    List<VolProgramme> findByVolIdAndDateHeureDepartBetween(Long volId, LocalDateTime start, LocalDateTime end);

    List<VolProgramme> findByVolAeroportDepartIdAndVolAeroportArriveeIdAndDateHeureDepartBetween(
            Long aeroportDepartId,
            Long aeroportArriveeId,
            LocalDateTime start,
            LocalDateTime end);
}
