package com.airline.airline_app.repository;

import com.airline.airline_app.entity.DiffusionSociete;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiffusionSocieteRepository extends JpaRepository<DiffusionSociete, Long>, JpaSpecificationExecutor<DiffusionSociete> {
    List<DiffusionSociete> findBySocieteIdSociete(Long idSociete);
}
