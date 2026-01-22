package com.airline.airline_app.repository;

import com.airline.airline_app.entity.DiffusionVolProgramme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiffusionVolProgrammeRepository extends JpaRepository<DiffusionVolProgramme, Long>, JpaSpecificationExecutor<DiffusionVolProgramme> {
    List<DiffusionVolProgramme> findByDiffusionSocieteIdDiffusionSociete(Long idDiffusionSociete);
    List<DiffusionVolProgramme> findByVolProgrammeId(Long idVolProgramme);
}
