package com.airline.airline_app.repository;

import com.airline.airline_app.entity.ConfigurationCoutDiffusion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConfigurationCoutDiffusionRepository extends JpaRepository<ConfigurationCoutDiffusion, Long>, JpaSpecificationExecutor<ConfigurationCoutDiffusion> {
    Optional<ConfigurationCoutDiffusion> findByDiffusionSocieteIdDiffusionSociete(Long idDiffusionSociete);
    List<ConfigurationCoutDiffusion> findAllByDiffusionSocieteIdDiffusionSociete(Long idDiffusionSociete);
}
