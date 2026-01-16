package com.airline.airline_app.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Association entre une configuration de remise et les pourcentages par catégorie/classe
 * Définit le pourcentage de remise pour chaque combinaison catégorie-classe
 */
@Entity
@Table(name = "assoc_configuration_remise_personnalisee")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssocConfigurationRemisePersonnalisee {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "id_configuration_remise_personnalisee", nullable = false)
    private ConfigurationRemisePersonnalisee configurationRemisePersonnalisee;
    
    @ManyToOne
    @JoinColumn(name = "id_categorie_passager", nullable = false)
    private CategoriePassager categoriePassager;
    
    @ManyToOne
    @JoinColumn(name = "id_classe_siege", nullable = false)
    private ClasseSiege classeSiege;
    
    /**
     * Pourcentage de remise (0-100)
     * Exemple: 30 signifie 30% de remise
     * Si prix_vol = 100,000 Ar et pourcentage_remise = 30%
     * Alors prix_final = 70,000 Ar et remise_prix = 30,000 Ar
     */
    @Column(name = "pourcentage_remise", nullable = false, precision = 5, scale = 2)
    private BigDecimal pourcentageRemise;
    
    @Column(name = "date_creation")
    private LocalDateTime dateCreation = LocalDateTime.now();
    
    @Column(name = "date_modification")
    private LocalDateTime dateModification = LocalDateTime.now();
    
    @PreUpdate
    public void preUpdate() {
        this.dateModification = LocalDateTime.now();
    }
    
    /**
     * Calcule le prix final après remise
     * @param prixReference Prix de référence depuis prix_vol
     * @return Prix après application de la remise
     */
    public BigDecimal calculerPrixFinal(BigDecimal prixReference) {
        if (prixReference == null || pourcentageRemise == null) {
            return prixReference;
        }
        
        // Prix final = prix_reference * (1 - pourcentage_remise/100)
        BigDecimal facteurRemise = BigDecimal.ONE.subtract(
            pourcentageRemise.divide(new BigDecimal("100"), 4, java.math.RoundingMode.HALF_UP)
        );
        
        return prixReference.multiply(facteurRemise).setScale(2, java.math.RoundingMode.HALF_UP);
    }
    
    /**
     * Calcule le montant de la remise à stocker dans remise_categorie_place.remise_prix
     * @param prixReference Prix de référence depuis prix_vol
     * @return Montant de la remise (ce qui est déduit du prix de référence)
     */
    public BigDecimal calculerMontantRemise(BigDecimal prixReference) {
        if (prixReference == null || pourcentageRemise == null) {
            return BigDecimal.ZERO;
        }
        
        // Montant remise = prix_reference * (pourcentage_remise/100)
        return prixReference.multiply(pourcentageRemise)
            .divide(new BigDecimal("100"), 2, java.math.RoundingMode.HALF_UP);
    }
}
