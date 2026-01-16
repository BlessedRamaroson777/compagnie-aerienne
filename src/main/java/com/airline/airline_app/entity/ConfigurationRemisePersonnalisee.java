package com.airline.airline_app.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Configuration de remise personnalisée liée à un vol programmé
 * Les pourcentages de remise sont définis dans la table d'association par catégorie et classe
 * Le prix de référence provient de la table prix_vol
 */
@Entity
@Table(name = "configuration_remise_personnalisee")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConfigurationRemisePersonnalisee {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 200)
    private String nom;
    
    @Column(length = 500)
    private String description;
    
    @ManyToOne
    @JoinColumn(name = "vol_programme_id", nullable = false)
    private VolProgramme volProgramme;
    
    @OneToMany(mappedBy = "configurationRemisePersonnalisee", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AssocConfigurationRemisePersonnalisee> associations = new ArrayList<>();
    
    @Column(name = "actif", nullable = false)
    private Boolean actif = true;
    
    @Column(name = "date_creation")
    private LocalDateTime dateCreation = LocalDateTime.now();
    
    @Column(name = "date_modification")
    private LocalDateTime dateModification = LocalDateTime.now();
    
    @PreUpdate
    public void preUpdate() {
        this.dateModification = LocalDateTime.now();
    }
    
    public void addAssociation(AssocConfigurationRemisePersonnalisee association) {
        associations.add(association);
        association.setConfigurationRemisePersonnalisee(this);
    }
    
    public void removeAssociation(AssocConfigurationRemisePersonnalisee association) {
        associations.remove(association);
        association.setConfigurationRemisePersonnalisee(null);
    }
}
