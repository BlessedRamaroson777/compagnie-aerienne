package com.airline.airline_app.repository;

import com.airline.airline_app.entity.DiffusionVolProgramme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface DiffusionVolProgrammeRepository extends JpaRepository<DiffusionVolProgramme, Long>, JpaSpecificationExecutor<DiffusionVolProgramme> {
    List<DiffusionVolProgramme> findByDiffusionSocieteIdDiffusionSociete(Long idDiffusionSociete);
    List<DiffusionVolProgramme> findByVolProgramme_Id(Long idVolProgramme);
    
    /**
     * Calculer le montant total généré par la diffusion de publicité pour un vol programmé
     * Montant = Somme(nombre * prix unitaire de diffusion)
     */
    @Query("SELECT COALESCE(SUM(dvp.nombre * ccd.prix), 0) " +
           "FROM DiffusionVolProgramme dvp " +
           "JOIN ConfigurationCoutDiffusion ccd ON ccd.diffusionSociete.idDiffusionSociete = dvp.diffusionSociete.idDiffusionSociete " +
           "WHERE dvp.volProgramme.id = :idVolProgramme")
    BigDecimal calculateMontantDiffusionByVolProgramme(@Param("idVolProgramme") Long idVolProgramme);
    
    /**
     * Calculer le montant total à payer pour la diffusion (utilise la colonne montant_total)
     */
    @Query("SELECT COALESCE(SUM(dvp.montantTotal), 0) " +
           "FROM DiffusionVolProgramme dvp " +
           "WHERE dvp.volProgramme.id = :idVolProgramme")
    BigDecimal calculateMontantDiffusionTotalByVolProgramme(@Param("idVolProgramme") Long idVolProgramme);
    
    /**
     * Calculer le montant déjà payé pour la diffusion (utilise la colonne montant_paye)
     */
    @Query("SELECT COALESCE(SUM(dvp.montantPaye), 0) " +
           "FROM DiffusionVolProgramme dvp " +
           "WHERE dvp.volProgramme.id = :idVolProgramme")
    BigDecimal calculateMontantDiffusionPayeByVolProgramme(@Param("idVolProgramme") Long idVolProgramme);
    
    /**
     * Calculer le reste à payer pour la diffusion (utilise la colonne reste_a_payer)
     */
    @Query("SELECT COALESCE(SUM(dvp.resteAPayer), 0) " +
           "FROM DiffusionVolProgramme dvp " +
           "WHERE dvp.volProgramme.id = :idVolProgramme")
    BigDecimal calculateMontantDiffusionResteByVolProgramme(@Param("idVolProgramme") Long idVolProgramme);
}
