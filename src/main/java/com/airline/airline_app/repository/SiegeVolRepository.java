package com.airline.airline_app.repository;

import com.airline.airline_app.entity.SiegeVol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SiegeVolRepository extends JpaRepository<SiegeVol, Long>, JpaSpecificationExecutor<SiegeVol> {
    List<SiegeVol> findByVolProgramme_Id(Long volProgrammeId);
    List<SiegeVol> findByVolProgramme_IdAndOccupe(Long volProgrammeId, Boolean occupe);
}
