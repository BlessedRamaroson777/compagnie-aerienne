package com.airline.airline_app.repository;

import com.airline.airline_app.entity.AssocConfigurationRemisePersonnalisee;
import com.airline.airline_app.entity.CategoriePassager;
import com.airline.airline_app.entity.ClasseSiege;
import com.airline.airline_app.entity.ConfigurationRemisePersonnalisee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AssocConfigurationRemisePersonnaliseeRepository 
        extends JpaRepository<AssocConfigurationRemisePersonnalisee, Long> {
    
    /**
     * Trouve toutes les associations pour une configuration donnée
     */
    List<AssocConfigurationRemisePersonnalisee> findByConfigurationRemisePersonnalisee(
        ConfigurationRemisePersonnalisee configuration);
    
    /**
     * Trouve toutes les associations pour une configuration donnée (par ID)
     */
    List<AssocConfigurationRemisePersonnalisee> findByConfigurationRemisePersonnalisee_Id(Long configurationId);
    
    /**
     * Trouve une association spécifique par configuration, catégorie et classe
     */
    Optional<AssocConfigurationRemisePersonnalisee> findByConfigurationRemisePersonnalisee_IdAndCategoriePassager_IdCategoriePassagerAndClasseSiege_Id(
        Long configurationId, Long categoriePassagerId, Long classeSiegeId);
    
    /**
     * Trouve toutes les associations pour une catégorie donnée
     */
    List<AssocConfigurationRemisePersonnalisee> findByCategoriePassager(CategoriePassager categoriePassager);
    
    /**
     * Trouve toutes les associations pour une classe de siège donnée
     */
    List<AssocConfigurationRemisePersonnalisee> findByClasseSiege(ClasseSiege classeSiege);
    
    /**
     * Trouve toutes les associations pour une configuration et une classe donnée
     */
    List<AssocConfigurationRemisePersonnalisee> findByConfigurationRemisePersonnalisee_IdAndClasseSiege_Id(
        Long configurationId, Long classeSiegeId);
    
    /**
     * Supprime toutes les associations d'une configuration
     */
    void deleteByConfigurationRemisePersonnalisee_Id(Long configurationId);
}
