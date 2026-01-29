package com.airline.airline_app.repository;

import com.airline.airline_app.entity.VenteProduit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface VenteProduitRepository extends JpaRepository<VenteProduit, Long> {
    
    List<VenteProduit> findByVolProgrammeId(Long idVolProgramme);
    
    List<VenteProduit> findByProduitIdProduit(Long idProduit);
    
    @Query("SELECT COALESCE(SUM(vp.quantite * vp.produit.prix), 0) " +
           "FROM VenteProduit vp " +
           "WHERE vp.volProgramme.id = :idVolProgramme")
    BigDecimal calculateCAVenteByVolProgramme(@Param("idVolProgramme") Long idVolProgramme);
    
    @Query("SELECT COALESCE(SUM(vp.quantite * vp.produit.prix), 0) " +
           "FROM VenteProduit vp " +
           "WHERE EXTRACT(MONTH FROM vp.dateVente) = :mois " +
           "AND EXTRACT(YEAR FROM vp.dateVente) = :annee")
    BigDecimal calculateCAVenteByMois(@Param("mois") Integer mois, @Param("annee") Integer annee);
}
