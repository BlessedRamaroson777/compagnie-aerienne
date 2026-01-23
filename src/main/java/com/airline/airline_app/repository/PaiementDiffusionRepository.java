package com.airline.airline_app.repository;

import com.airline.airline_app.entity.PaiementDiffusion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface PaiementDiffusionRepository extends JpaRepository<PaiementDiffusion, Long> {
    
    List<PaiementDiffusion> findByDiffusionSocieteIdDiffusionSociete(Long idDiffusionSociete);
    
    @Query("SELECT COALESCE(SUM(p.montantPaye), 0) FROM PaiementDiffusion p WHERE p.diffusionSociete.idDiffusionSociete = :idDiffusionSociete")
    BigDecimal sumMontantPayeByDiffusionSociete(@Param("idDiffusionSociete") Long idDiffusionSociete);
}
