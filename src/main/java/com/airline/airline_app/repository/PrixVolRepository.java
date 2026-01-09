package com.airline.airline_app.repository;

import com.airline.airline_app.entity.PrixVol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PrixVolRepository extends JpaRepository<PrixVol, Long>, JpaSpecificationExecutor<PrixVol> {
    List<PrixVol> findByVolId(Long volId);
    Optional<PrixVol> findByVolIdAndClasseSiegeId(Long volId, Long classeSiegeId);
}
