package com.airline.airline_app.service;

import com.airline.airline_app.entity.*;
import com.airline.airline_app.repository.*;
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
    private final VolProgrammeClassePrixRepository volProgrammeClassePrixRepository;
    private final RemiseCategoriePlaceRepository remiseCategoriePlaceRepository;
    private final CategoriePassagerRepository categoriePassagerRepository;
    private final ClasseSiegeRepository classeSiegeRepository;
    
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
     * Supprime une configuration par ID
     */
    @Transactional
    public void deleteById(Long id) {
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
        
        // Récupérer le prix de référence depuis vol_programme_classe_prix
        Optional<VolProgrammeClassePrix> prixClasseOpt = volProgrammeClassePrixRepository.findByVolProgrammeIdAndClasseSiegeId(volProgrammeId, classeSiegeId);
        if (prixClasseOpt.isEmpty()) {
            return null;
        }
        
        // Calculer le prix final
        return assocOpt.get().calculerPrixFinal(prixClasseOpt.get().getPrix());
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
        
        // Récupérer le prix de référence depuis vol_programme_classe_prix
        Optional<VolProgrammeClassePrix> prixClasseOpt = volProgrammeClassePrixRepository.findByVolProgrammeIdAndClasseSiegeId(volProgrammeId, classeSiegeId);
        if (prixClasseOpt.isEmpty()) {
            return BigDecimal.ZERO;
        }
        
        // Calculer le montant de la remise
        return assocOpt.get().calculerMontantRemise(prixClasseOpt.get().getPrix());
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
            // Calculer le PRIX FINAL après remise (pas le montant de la remise)
            BigDecimal prixFinal = assoc.calculerPrixFinal(nouveauPrix);
            
            // Chercher ou créer la remise dans remise_categorie_place
            Optional<RemiseCategoriePlace> remiseOpt = remiseCategoriePlaceRepository
                .findByVolProgramme_IdAndCategoriePassager_IdCategoriePassagerAndClasseSiege_Id(
                    volProgrammeId, 
                    assoc.getCategoriePassager().getIdCategoriePassager(), 
                    classeSiegeId);
            
            if (remiseOpt.isPresent()) {
                // Mettre à jour la remise existante avec le prix final
                RemiseCategoriePlace remise = remiseOpt.get();
                remise.setRemisePrix(prixFinal.doubleValue());
                remiseCategoriePlaceRepository.save(remise);
            } else {
                // Créer une nouvelle remise si elle n'existe pas
                RemiseCategoriePlace nouvelleRemise = new RemiseCategoriePlace();
                nouvelleRemise.setVolProgramme(configOpt.get().getVolProgramme());
                nouvelleRemise.setCategoriePassager(assoc.getCategoriePassager());
                nouvelleRemise.setClasseSiege(assoc.getClasseSiege());
                nouvelleRemise.setRemisePrix(prixFinal.doubleValue());
                remiseCategoriePlaceRepository.save(nouvelleRemise);
            }
        }
    }
    
    /**
     * Crée les associations et les remises pour une configuration
     * @param config La configuration
     * @param classeSiegeIds Liste des IDs de classes de siège (null si toutes)
     * @param pourcentagesParCategorieEtClasse Map: "categorieId_classeId" -> pourcentage
     * @return Le nombre de remises créées dans remise_categorie_place
     */
    @Transactional
    public int creerAssociationsEtRemises(
            ConfigurationRemisePersonnalisee config,
            List<Long> classeSiegeIds,
            java.util.Map<String, BigDecimal> pourcentagesParCategorieEtClasse) {
        
        int nbRemisesCreees = 0;
        
        System.out.println("=== SERVICE: creerAssociationsEtRemises ===");
        System.out.println("Vol programmé ID: " + config.getVolProgramme().getId());
        System.out.println("Nombre de pourcentages à traiter: " + pourcentagesParCategorieEtClasse.size());
        
        // Supprimer les anciennes associations
        if (config.getAssociations() != null && !config.getAssociations().isEmpty()) {
            assocRepository.deleteAll(config.getAssociations());
            config.getAssociations().clear();
        }
        
        // Récupérer les prix depuis vol_programme_classe_prix pour ce vol programmé
        List<VolProgrammeClassePrix> prixParClasse = volProgrammeClassePrixRepository.findByVolProgrammeId(config.getVolProgramme().getId());
        System.out.println("Nombre de prix (vol_programme_classe_prix) trouvés: " + prixParClasse.size());
        
        if (prixParClasse.isEmpty()) {
            System.out.println("ERREUR CRITIQUE: Aucun prix trouvé pour ce vol programmé dans vol_programme_classe_prix!");
            System.out.println("  -> Impossible de créer des remises sans prix de référence");
            System.out.println("  -> Veuillez d'abord créer des prix pour ce vol programmé");
            return 0; // Retourner 0 car aucune remise ne peut être créée
        }
        
        // Pour chaque entrée dans la map des pourcentages
        for (java.util.Map.Entry<String, BigDecimal> entry : pourcentagesParCategorieEtClasse.entrySet()) {
            String[] parts = entry.getKey().split("_");
            Long categorieId = Long.parseLong(parts[0]);
            Long classeId = Long.parseLong(parts[1]);
            BigDecimal pourcentage = entry.getValue();
            
            if (pourcentage == null || pourcentage.compareTo(BigDecimal.ZERO) <= 0) {
                continue; // Ignorer les pourcentages nuls ou négatifs
            }
            
            // Récupérer les entités
            Optional<CategoriePassager> categorieOpt = categoriePassagerRepository.findById(categorieId);
            Optional<ClasseSiege> classeOpt = classeSiegeRepository.findById(classeId);
            
            if (categorieOpt.isEmpty() || classeOpt.isEmpty()) {
                continue;
            }
            
            // Créer l'association
            AssocConfigurationRemisePersonnalisee assoc = new AssocConfigurationRemisePersonnalisee();
            assoc.setConfigurationRemisePersonnalisee(config);
            assoc.setCategoriePassager(categorieOpt.get());
            assoc.setClasseSiege(classeOpt.get());
            assoc.setPourcentageRemise(pourcentage);
            assocRepository.save(assoc);
            
            // Trouver le prix de référence pour cette classe
            Optional<VolProgrammeClassePrix> prixClasseOpt = prixParClasse.stream()
                .filter(vpc -> vpc.getClasseSiege().getId().equals(classeId))
                .findFirst();
            
            System.out.println("Traitement: catégorie=" + categorieId + ", classe=" + classeId + 
                ", pourcentage=" + pourcentage + ", prix trouvé=" + prixClasseOpt.isPresent());
            
            if (prixClasseOpt.isPresent()) {
                BigDecimal prixReference = prixClasseOpt.get().getPrix();
                BigDecimal prixFinalCalc = assoc.calculerPrixFinal(prixReference);
                
                System.out.println("  -> Prix référence: " + prixReference + ", Prix final: " + prixFinalCalc);
                
                // Créer ou mettre à jour la remise dans remise_categorie_place
                Optional<RemiseCategoriePlace> remiseOpt = remiseCategoriePlaceRepository
                    .findByVolProgramme_IdAndCategoriePassager_IdCategoriePassagerAndClasseSiege_Id(
                        config.getVolProgramme().getId(), categorieId, classeId);
                
                RemiseCategoriePlace remise;
                if (remiseOpt.isPresent()) {
                    remise = remiseOpt.get();
                    System.out.println("  -> Mise à jour remise existante ID: " + remise.getId());
                } else {
                    remise = new RemiseCategoriePlace();
                    remise.setVolProgramme(config.getVolProgramme());
                    remise.setCategoriePassager(categorieOpt.get());
                    remise.setClasseSiege(classeOpt.get());
                    System.out.println("  -> Création nouvelle remise");
                }
                remise.setRemisePrix(prixFinalCalc.doubleValue());
                remiseCategoriePlaceRepository.save(remise);
                nbRemisesCreees++;
                System.out.println("  -> Remise sauvegardée avec succès!");
            } else {
                System.out.println("  -> ERREUR: Aucun prix trouvé pour cette classe dans vol_programme_classe_prix!");
            }
        }
        
        System.out.println("=== FIN: Total remises créées: " + nbRemisesCreees + " ===");
        return nbRemisesCreees;
    }
}
