package com.airline.airline_app.repository;

import com.airline.airline_app.entity.EtatAvion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EtatAvionRepository extends JpaRepository<EtatAvion, Long>, JpaSpecificationExecutor<EtatAvion> {
    Optional<EtatAvion> findByLibelle(String libelle);
}
