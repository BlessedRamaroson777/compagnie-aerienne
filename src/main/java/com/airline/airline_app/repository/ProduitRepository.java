package com.airline.airline_app.repository;

import com.airline.airline_app.entity.Produit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProduitRepository extends JpaRepository<Produit, Long> {
    
    List<Produit> findByLibelleContainingIgnoreCase(String libelle);
    
    @Query("SELECT p FROM Produit p WHERE p.stock < :seuil")
    List<Produit> findProduitsFaibleStock(@Param("seuil") Integer seuil);
}
