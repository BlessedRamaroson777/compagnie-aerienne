package com.airline.airline_app.repository;

import com.airline.airline_app.entity.HistoriqueMouvementProduit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistoriqueMouvementProduitRepository extends JpaRepository<HistoriqueMouvementProduit, Long> {
    
    List<HistoriqueMouvementProduit> findByProduitIdProduitOrderByDateMouvementDesc(Long idProduit);
    
    @Query("SELECT h FROM HistoriqueMouvementProduit h WHERE h.estEntree = :estEntree ORDER BY h.dateMouvement DESC")
    List<HistoriqueMouvementProduit> findByEstEntree(@Param("estEntree") Boolean estEntree);
}
