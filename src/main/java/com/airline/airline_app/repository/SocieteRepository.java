package com.airline.airline_app.repository;

import com.airline.airline_app.entity.Societe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SocieteRepository extends JpaRepository<Societe, Long>, JpaSpecificationExecutor<Societe> {
    Optional<Societe> findByLibelle(String libelle);
}
