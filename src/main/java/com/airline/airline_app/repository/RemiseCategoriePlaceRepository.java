package com.airline.airline_app.repository;

import com.airline.airline_app.entity.RemiseCategoriePlace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RemiseCategoriePlaceRepository extends JpaRepository<RemiseCategoriePlace, Long> {
    List<RemiseCategoriePlace> findByVolProgramme_Id(Long volProgrammeId);
    List<RemiseCategoriePlace> findByCategoriePassager_IdCategoriePassager(Long idCategoriePassager);
    List<RemiseCategoriePlace> findByClasseSiege_Id(Long idClasseSiege);
    List<RemiseCategoriePlace> findByVolProgramme_IdAndClasseSiege_Id(Long volProgrammeId, Long classeSiegeId);
    
    /**
     * Trouve une remise spécifique par vol, catégorie et classe
     */
    Optional<RemiseCategoriePlace> findByVolProgramme_IdAndCategoriePassager_IdCategoriePassagerAndClasseSiege_Id(
        Long volProgrammeId, Long categoriePassagerId, Long classeSiegeId);
}
