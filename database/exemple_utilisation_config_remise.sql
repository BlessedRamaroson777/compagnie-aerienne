-- =====================================================
-- EXEMPLE D'UTILISATION - Configuration Remise Personnalisée
-- =====================================================
-- Ce script montre comment utiliser le nouveau système

-- =====================================================
-- 1) COMPRENDRE LE SYSTÈME
-- =====================================================

/*
IMPORTANTE DISTINCTION:

Dans remise_categorie_place:
- remise_prix = MONTANT DE LA REMISE (ce qu'on enlève)
  Exemple: remise_prix = 30 000 Ar

Dans configuration_remise_personnalisee:
- pourcentage_enfant = POURCENTAGE DU PRIX ADULTE (ce qu'on garde)
  Exemple: pourcentage_enfant = 70% (on garde 70% du prix adulte)

CONVERSION:
- Prix adulte: 100 000 Ar
- Pourcentage enfant: 70%
- Prix enfant: 100 000 * 70% = 70 000 Ar
- Remise (pour remise_categorie_place): 100 000 - 70 000 = 30 000 Ar
*/

-- =====================================================
-- 2) CRÉER UNE CONFIGURATION MANUELLE
-- =====================================================

-- Exemple: Configuration pour classe Économique
INSERT INTO configuration_remise_personnalisee (
    nom,
    description,
    classe_siege_id,
    prix_adulte,
    pourcentage_enfant,
    pourcentage_bebe,
    pourcentage_senior,
    pourcentage_etudiant,
    actif,
    appliquer_toutes_classes
) VALUES (
    'Tarif Standard Économique',
    'Configuration standard pour la classe économique',
    (SELECT id FROM classe_siege WHERE libelle = 'Économique'),
    100000.00,  -- Prix adulte: 100 000 Ar
    70.00,      -- Enfant paie 70% = 70 000 Ar (remise de 30 000 Ar)
    30.00,      -- Bébé paie 30% = 30 000 Ar (remise de 70 000 Ar)
    80.00,      -- Senior paie 80% = 80 000 Ar (remise de 20 000 Ar)
    85.00,      -- Étudiant paie 85% = 85 000 Ar (remise de 15 000 Ar)
    TRUE,
    FALSE
);

-- =====================================================
-- 3) SYNCHRONISER AVEC remise_categorie_place
-- =====================================================

-- Fonction pour créer des entrées remise_categorie_place 
-- à partir d'une configuration personnalisée

CREATE OR REPLACE FUNCTION synchroniser_remise_depuis_config(
    p_config_id INT,
    p_vol_programme_id INT
) RETURNS void AS $$
DECLARE
    v_config RECORD;
    v_categorie_adulte_id INT;
    v_categorie_enfant_id INT;
    v_categorie_bebe_id INT;
    v_categorie_senior_id INT;
    v_categorie_etudiant_id INT;
    v_remise_enfant DECIMAL(10,2);
    v_remise_bebe DECIMAL(10,2);
    v_remise_senior DECIMAL(10,2);
    v_remise_etudiant DECIMAL(10,2);
BEGIN
    -- Récupérer la configuration
    SELECT * INTO v_config
    FROM configuration_remise_personnalisee
    WHERE id = p_config_id;
    
    IF NOT FOUND THEN
        RAISE EXCEPTION 'Configuration % non trouvée', p_config_id;
    END IF;
    
    -- Récupérer les IDs des catégories
    SELECT id_categorie_passager INTO v_categorie_adulte_id
    FROM categorie_passager WHERE LOWER(libelle) LIKE '%adulte%' LIMIT 1;
    
    SELECT id_categorie_passager INTO v_categorie_enfant_id
    FROM categorie_passager WHERE LOWER(libelle) LIKE '%enfant%' LIMIT 1;
    
    SELECT id_categorie_passager INTO v_categorie_bebe_id
    FROM categorie_passager WHERE LOWER(libelle) LIKE '%bébé%' OR LOWER(libelle) LIKE '%bebe%' LIMIT 1;
    
    SELECT id_categorie_passager INTO v_categorie_senior_id
    FROM categorie_passager WHERE LOWER(libelle) LIKE '%senior%' LIMIT 1;
    
    SELECT id_categorie_passager INTO v_categorie_etudiant_id
    FROM categorie_passager WHERE LOWER(libelle) LIKE '%étudiant%' OR LOWER(libelle) LIKE '%etudiant%' LIMIT 1;
    
    -- Calculer les montants de remise
    -- Remise = Prix_adulte - (Prix_adulte * Pourcentage / 100)
    v_remise_enfant := v_config.prix_adulte - (v_config.prix_adulte * v_config.pourcentage_enfant / 100);
    v_remise_bebe := v_config.prix_adulte - (v_config.prix_adulte * v_config.pourcentage_bebe / 100);
    v_remise_senior := v_config.prix_adulte - (v_config.prix_adulte * v_config.pourcentage_senior / 100);
    v_remise_etudiant := v_config.prix_adulte - (v_config.prix_adulte * v_config.pourcentage_etudiant / 100);
    
    -- Insérer ou mettre à jour dans remise_categorie_place
    -- Adulte (pas de remise)
    INSERT INTO remise_categorie_place (
        vol_programme_id, id_categorie_passager, id_classe_siege, remise_prix
    ) VALUES (
        p_vol_programme_id, v_categorie_adulte_id, v_config.classe_siege_id, 0
    ) ON CONFLICT (vol_programme_id, id_categorie_passager, id_classe_siege) 
    DO UPDATE SET remise_prix = 0;
    
    -- Enfant
    IF v_categorie_enfant_id IS NOT NULL THEN
        INSERT INTO remise_categorie_place (
            vol_programme_id, id_categorie_passager, id_classe_siege, remise_prix
        ) VALUES (
            p_vol_programme_id, v_categorie_enfant_id, v_config.classe_siege_id, v_remise_enfant
        ) ON CONFLICT (vol_programme_id, id_categorie_passager, id_classe_siege) 
        DO UPDATE SET remise_prix = v_remise_enfant;
    END IF;
    
    -- Bébé
    IF v_categorie_bebe_id IS NOT NULL THEN
        INSERT INTO remise_categorie_place (
            vol_programme_id, id_categorie_passager, id_classe_siege, remise_prix
        ) VALUES (
            p_vol_programme_id, v_categorie_bebe_id, v_config.classe_siege_id, v_remise_bebe
        ) ON CONFLICT (vol_programme_id, id_categorie_passager, id_classe_siege) 
        DO UPDATE SET remise_prix = v_remise_bebe;
    END IF;
    
    -- Senior
    IF v_categorie_senior_id IS NOT NULL THEN
        INSERT INTO remise_categorie_place (
            vol_programme_id, id_categorie_passager, id_classe_siege, remise_prix
        ) VALUES (
            p_vol_programme_id, v_categorie_senior_id, v_config.classe_siege_id, v_remise_senior
        ) ON CONFLICT (vol_programme_id, id_categorie_passager, id_classe_siege) 
        DO UPDATE SET remise_prix = v_remise_senior;
    END IF;
    
    -- Étudiant
    IF v_categorie_etudiant_id IS NOT NULL THEN
        INSERT INTO remise_categorie_place (
            vol_programme_id, id_categorie_passager, id_classe_siege, remise_prix
        ) VALUES (
            p_vol_programme_id, v_categorie_etudiant_id, v_config.classe_siege_id, v_remise_etudiant
        ) ON CONFLICT (vol_programme_id, id_categorie_passager, id_classe_siege) 
        DO UPDATE SET remise_prix = v_remise_etudiant;
    END IF;
    
    RAISE NOTICE 'Synchronisation terminée pour la configuration % sur le vol %', p_config_id, p_vol_programme_id;
END;
$$ LANGUAGE plpgsql;

-- =====================================================
-- 4) EXEMPLE D'UTILISATION
-- =====================================================

-- Synchroniser la configuration ID 1 pour le vol programmé ID 10
-- SELECT synchroniser_remise_depuis_config(1, 10);

-- Vérifier les résultats
-- SELECT 
--     cp.libelle as categorie,
--     cs.libelle as classe,
--     rcp.remise_prix as remise_montant,
--     pv.prix as prix_base,
--     (pv.prix - rcp.remise_prix) as prix_final
-- FROM remise_categorie_place rcp
-- JOIN categorie_passager cp ON rcp.id_categorie_passager = cp.id_categorie_passager
-- JOIN classe_siege cs ON rcp.id_classe_siege = cs.id
-- JOIN prix_vol pv ON pv.classe_siege_id = cs.id AND pv.vol_programme_id = rcp.vol_programme_id
-- WHERE rcp.vol_programme_id = 10;

-- =====================================================
-- 5) TABLEAU RÉCAPITULATIF DES CALCULS
-- =====================================================

/*
Prix Adulte: 100 000 Ar

┌──────────────┬────────────────┬──────────────┬───────────────────┐
│  Catégorie   │  Pourcentage   │  Prix Final  │  Remise (montant) │
├──────────────┼────────────────┼──────────────┼───────────────────┤
│ Adulte       │     100%       │  100 000 Ar  │        0 Ar       │
│ Enfant       │      70%       │   70 000 Ar  │   30 000 Ar       │
│ Bébé         │      30%       │   30 000 Ar  │   70 000 Ar       │
│ Senior       │      80%       │   80 000 Ar  │   20 000 Ar       │
│ Étudiant     │      85%       │   85 000 Ar  │   15 000 Ar       │
└──────────────┴────────────────┴──────────────┴───────────────────┘

FORMULES:
- Prix Final = Prix Adulte × (Pourcentage ÷ 100)
- Remise = Prix Adulte - Prix Final
- Pourcentage = (Prix Final ÷ Prix Adulte) × 100
*/
