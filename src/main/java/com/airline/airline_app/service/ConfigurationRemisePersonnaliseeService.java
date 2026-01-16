package com.airline.airline_app.service;

import com.airline.airline_app.entity.*;
import com.airline.airline_app.repository.AssocConfigurationRemisePersonnaliseeRepository;
import com.airline.airline_app.repository.ConfigurationRemisePersonnaliseeRepository;
import com.airline.airline_app.repository.PrixVolRepository;
import com.airline.airline_app.repository.RemiseCategoriePlaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ConfigurationRemisePersonnaliseeService {
    
    private final ConfigurationRemisePersonnaliseeRepository repository;
    private final AssocConfigurationRemisePersonnaliseeRepository assocRepository;
    private final PrixVolRepository prixVolRepository;
    private final RemiseCategoriePlaceRepository remiseCategoriePlaceRepository;
    
    /**
     * Récupère toutes les configurations
     */
    public List<ConfigurationRemisePersonnalisee> findAll() {
        return repository.findAll();
    }
    
    /**
     * Récupère une configuration par ID
     */
    public Optional<ConfigurationRemisePersonnalisee> findById(Long id) {
        return repository.findById(id);
    }
    
    /**
     * Récupère toutes les configurations actives
     */
    public List<ConfigurationRemisePersonnalisee> findAllActive() {
        return repository.findByActifTrue();
    }
    
    /**
     * Récupère la configuration active pour un vol programmé
     */
    public Optional<ConfigurationRemisePersonnalisee> findActiveConfigForVolProgramme(Long volProgrammeId) {
        return repository.findByVolProgramme_IdAndActifTrue(volProgrammeId);
    }
    
    /**
     * Récupère toutes les configurations pour un vol programmé
     */
    public List<ConfigurationRemisePersonnalisee> findConfigsForVolProgramme(Long volProgrammeId) {
        return repository.findByVolProgramme_Id(volProgrammeId);
    }
    
    /**
     * Sauvegarde ou met à jour une configuration avec ses associations
     */
    @Transactional
    public ConfigurationRemisePersonnalisee save(ConfigurationRemisePersonnalisee config) {
        // Si on active cette config, désactiver les autres configs du même vol
        if (config.getActif() && config.getVolProgramme() != null) {
            List<ConfigurationRemisePersonnalisee> autresConfigs = 
                repository.findByVolProgramme_Id(config.getVolProgramme().getId());
            
            for (ConfigurationRemisePersonnalisee autre : autresConfigs) {
                if (autre.getActif() && !autre.getId().equals(config.getId())) {
                    autre.setActif(false);
                    repository.save(autre);
                }
            }
        }
        
        return repository.save(config);
    }
    
    /**
     * Désactive une configuration
     */
    @Transactional
    public void deactivate(Long id) {
        findById(id).ifPresent(config -> {
            config.setActif(false);
            repository.save(config);
        });
    }
    
    /**
     * Active une configuration
     */
    @Transactional
    public void activate(Long id) {
        findById(id).ifPresent(config -> {
            // Désactiver les autres configs du même vol
            if (config.getVolProgramme() != null) {
                List<ConfigurationRemisePersonnalisee> autresConfigs = 
                    repository.findByVolProgramme_Id(config.getVolProgramme().getId());
                
                for (ConfigurationRemisePersonnalisee autre : autresConfigs) {
                    if (autre.getActif()) {
                        autre.setActif(false);
                        repository.save(autre);
                    }
                }
            }
            
            config.setActif(true);
            repository.save(config);
        });
    }
    
    /**
     * Supprime une configuration
     */
    @Transactional
    public void delete(Long id) {
        repository.deleteById(id);
    }
    
    /**
     * Calcule le prix final après remise pour une catégorie et classe donnée
     * @param volProgrammeId ID du vol programmé
     * @param categoriePassagerId ID de la catégorie de passager
     * @param classeSiegeId ID de la classe de siège
     * @return Prix final après remise, ou null si pas de configuration
     */
    public BigDecimal calculerPrixFinalPourCategorie(Long volProgrammeId, Long categoriePassagerId, Long classeSiegeId) {
        // Récupérer la configuration active
        Optional<ConfigurationRemisePersonnalisee> configOpt = findActiveConfigForVolProgramme(volProgrammeId);
        if (configOpt.isEmpty()) {
            return null;
        }
        
        // Récupérer l'association pour cette catégorie et classe
        Optional<AssocConfigurationRemisePersonnalisee> assocOpt = 
            assocRepository.findByConfigurationRemisePersonnalisee_IdAndCategoriePassager_IdCategoriePassagerAndClasseSiege_Id(
                configOpt.get().getId(), categoriePassagerId, classeSiegeId);
        
        if (assocOpt.isEmpty()) {
            return null;
        }
        
        // Récupérer le prix de référence depuis prix_vol
        Optional<PrixVol> prixVolOpt = prixVolRepository.findByVolProgrammeIdAndClasseSiegeId(volProgrammeId, classeSiegeId);
        if (prixVolOpt.isEmpty()) {
            return null;
        }
        
        // Calculer le prix final
        return assocOpt.get().calculerPrixFinal(prixVolOpt.get().getPrix());
    }
    
    /**
     * Calcule le montant de la remise pour remise_categorie_place
     */
    public BigDecimal calculerMontantRemisePourCategorie(Long volProgrammeId, Long categoriePassagerId, Long classeSiegeId) {
        // Récupérer la configuration active
        Optional<ConfigurationRemisePersonnalisee> configOpt = findActiveConfigForVolProgramme(volProgrammeId);
        if (configOpt.isEmpty()) {
            return BigDecimal.ZERO;
        }
        
        // Récupérer l'association
        Optional<AssocConfigurationRemisePersonnalisee> assocOpt = 
            assocRepository.findByConfigurationRemisePersonnalisee_IdAndCategoriePassager_IdCategoriePassagerAndClasseSiege_Id(
                configOpt.get().getId(), categoriePassagerId, classeSiegeId);
        
        if (assocOpt.isEmpty()) {
            return BigDecimal.ZERO;
        }
        
        // Récupérer le prix de référence
        Optional<PrixVol> prixVolOpt = prixVolRepository.findByVolProgrammeIdAndClasseSiegeId(volProgrammeId, classeSiegeId);
        if (prixVolOpt.isEmpty()) {
            return BigDecimal.ZERO;
        }
        
        // Calculer le montant de la remise
        return assocOpt.get().calculerMontantRemise(prixVolOpt.get().getPrix());
    }
    
    /**
     * Récupère toutes les associations d'une configuration
     */
    public List<AssocConfigurationRemisePersonnalisee> findAssociationsByConfig(Long configId) {
        return assocRepository.findByConfigurationRemisePersonnalisee_Id(configId);
    }
    
    /**
     * Sauvegarde une association
     */
    @Transactional
    public AssocConfigurationRemisePersonnalisee saveAssociation(AssocConfigurationRemisePersonnalisee assoc) {
        return assocRepository.save(assoc);
    }
    
    /**
     * Supprime une association
     */
    @Transactional
    public void deleteAssociation(Long assocId) {
        assocRepository.deleteById(assocId);
    }
    
    /**
     * Synchronise les remises dans remise_categorie_place quand le prix_vol change
     * Met à jour toutes les remises pour un vol programmé et une classe donnée
     * @param volProgrammeId ID du vol programmé
     * @param classeSiegeId ID de la classe de siège
     * @param nouveauPrix Nouveau prix de référence depuis prix_vol
     */
    @Transactional
    public void synchroniserRemisesApresChangementPrix(Long volProgrammeId, Long classeSiegeId, BigDecimal nouveauPrix) {
        // Récupérer la configuration active pour ce vol
        Optional<ConfigurationRemisePersonnalisee> configOpt = findActiveConfigForVolProgramme(volProgrammeId);
        if (configOpt.isEmpty()) {
            return; // Pas de configuration active, rien à synchroniser
        }
        
        // Récupérer toutes les associations pour cette configuration et cette classe
        List<AssocConfigurationRemisePersonnalisee> associations = 
            assocRepository.findByConfigurationRemisePersonnalisee_IdAndClasseSiege_Id(
                configOpt.get().getId(), classeSiegeId);
        
        if (associations.isEmpty()) {
            return; // Pas d'associations, rien à synchroniser
        }
        
        // Pour chaque association (catégorie de passager), recalculer et mettre à jour remise_categorie_place
        for (AssocConfigurationRemisePersonnalisee assoc : associations) {
            BigDecimal montantRemise = assoc.calculerMontantRemise(nouveauPrix);
            
            // Chercher ou créer la remise dans remise_categorie_place
            Optional<RemiseCategoriePlace> remiseOpt = remiseCategoriePlaceRepository
                .findByVolProgramme_IdAndCategoriePassager_IdCategoriePassagerAndClasseSiege_Id(
                    volProgrammeId, 
                    assoc.getCategoriePassager().getIdCategoriePassager(), 
                    classeSiegeId);
            
            if (remiseOpt.isPresent()) {
                // Mettre à jour la remise existante
                RemiseCategoriePlace remise = remiseOpt.get();
                remise.setRemisePrix(montantRemise.doubleValue());
                remiseCategoriePlaceRepository.save(remise);
            } else {
                // Créer une nouvelle remise si elle n'existe pas
                RemiseCategoriePlace nouvelleRemise = new RemiseCategoriePlace();
                nouvelleRemise.setVolProgramme(configOpt.get().getVolProgramme());
                nouvelleRemise.setCategoriePassager(assoc.getCategoriePassager());
                nouvelleRemise.setClasseSiege(assoc.getClasseSiege());
                nouvelleRemise.setRemisePrix(montantRemise.doubleValue());
                remiseCategoriePlaceRepository.save(nouvelleRemise);
            }
        }
    }
}
