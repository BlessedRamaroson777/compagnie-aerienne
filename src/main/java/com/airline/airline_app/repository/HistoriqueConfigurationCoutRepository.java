package com.airline.airline_app.repository;

import com.airline.airline_app.entity.HistoriqueConfigurationCout;
import com.airline.airline_app.entity.enums.StatusHistorique;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistoriqueConfigurationCoutRepository extends JpaRepository<HistoriqueConfigurationCout, Long>, JpaSpecificationExecutor<HistoriqueConfigurationCout> {
    List<HistoriqueConfigurationCout> findByStatus(StatusHistorique status);
    List<HistoriqueConfigurationCout> findByConfigurationCoutDiffusionIdConfigurationCoutDiffusion(Long idConfigurationCoutDiffusion);
}
