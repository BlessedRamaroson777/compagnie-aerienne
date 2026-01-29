package com.airline.airline_app.repository;

import com.airline.airline_app.entity.ProduitCategorieSiegePassager;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProduitCategorieSiegePassagerRepository extends JpaRepository<ProduitCategorieSiegePassager, Long> {
    
    List<ProduitCategorieSiegePassager> findByProduitIdProduit(Long idProduit);
    
    List<ProduitCategorieSiegePassager> findByCategoriePassagerIdCategoriePassager(Long idCategoriePassager);
    
    List<ProduitCategorieSiegePassager> findByClasseSiegeId(Long idClasseSiege);
}
