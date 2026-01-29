package com.airline.airline_app.repository;

import com.airline.airline_app.entity.PrixProduit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PrixProduitRepository extends JpaRepository<PrixProduit, Long> {
    
    List<PrixProduit> findByProduitIdProduitOrderByDateModificationDesc(Long idProduit);
}
