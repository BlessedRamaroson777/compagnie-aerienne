package com.airline.airline_app.repository;

import com.airline.airline_app.entity.ConfigurationRemisePersonnalisee;
import com.airline.airline_app.entity.VolProgramme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConfigurationRemisePersonnaliseeRepository extends JpaRepository<ConfigurationRemisePersonnalisee, Long> {
    
    // Trouver toutes les configurations actives
    List<ConfigurationRemisePersonnalisee> findByActifTrue();
    
    // Trouver les configurations pour un vol programmé donné
    List<ConfigurationRemisePersonnalisee> findByVolProgramme_Id(Long volProgrammeId);
    
    // Trouver la configuration active pour un vol programmé
    Optional<ConfigurationRemisePersonnalisee> findByVolProgramme_IdAndActifTrue(Long volProgrammeId);
    
    // Trouver toutes les configurations pour un vol programmé
    List<ConfigurationRemisePersonnalisee> findByVolProgramme(VolProgramme volProgramme);
}
