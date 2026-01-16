-- =====================================================
-- SCRIPT DE CONFORMISATION - Configuration Remise Personnalisée
-- =====================================================
-- Ce script crée la table pour le nouveau système de remise personnalisée
-- et génère des configurations par défaut basées sur les données existantes

-- =====================================================
-- 1) CRÉATION DE LA TABLE
-- =====================================================

CREATE TABLE IF NOT EXISTS configuration_remise_personnalisee (
    id SERIAL PRIMARY KEY,
    nom VARCHAR(200) NOT NULL,
    description VARCHAR(500),
    classe_siege_id INT,
    prix_adulte DECIMAL(10,2) NOT NULL,
    pourcentage_enfant DECIMAL(5,2) NOT NULL,
    pourcentage_bebe DECIMAL(5,2) NOT NULL,
    pourcentage_senior DECIMAL(5,2) NOT NULL,
    pourcentage_etudiant DECIMAL(5,2) NOT NULL,
    actif BOOLEAN NOT NULL DEFAULT TRUE,
    appliquer_toutes_classes BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (classe_siege_id) REFERENCES classe_siege(id) ON DELETE CASCADE
);

-- Index pour améliorer les performances
CREATE INDEX IF NOT EXISTS idx_config_remise_classe ON configuration_remise_personnalisee(classe_siege_id);
CREATE INDEX IF NOT EXISTS idx_config_remise_actif ON configuration_remise_personnalisee(actif);
CREATE INDEX IF NOT EXISTS idx_config_remise_toutes_classes ON configuration_remise_personnalisee(appliquer_toutes_classes);

-- =====================================================
-- 2) FONCTION POUR GÉNÉRER DES CONFIGURATIONS PAR DÉFAUT
-- =====================================================

-- Cette fonction analyse les données existantes et crée des configurations
-- basées sur les prix moyens par classe

DO $$
DECLARE
    classe_record RECORD;
    prix_adulte_moyen DECIMAL(10,2);
    prix_enfant_moyen DECIMAL(10,2);
    prix_bebe_moyen DECIMAL(10,2);
    prix_senior_moyen DECIMAL(10,2);
    prix_etudiant_moyen DECIMAL(10,2);
    pct_enfant DECIMAL(5,2);
    pct_bebe DECIMAL(5,2);
    pct_senior DECIMAL(5,2);
    pct_etudiant DECIMAL(5,2);
BEGIN
    -- Pour chaque classe de siège
    FOR classe_record IN SELECT id, libelle FROM classe_siege
    LOOP
        -- Calculer le prix moyen adulte pour cette classe
        SELECT AVG(pv.prix) INTO prix_adulte_moyen
        FROM prix_vol pv
        WHERE pv.classe_siege_id = classe_record.id;
        
        -- Si pas de prix trouvé, utiliser une valeur par défaut
        IF prix_adulte_moyen IS NULL THEN
            CASE classe_record.libelle
                WHEN 'Économique' THEN prix_adulte_moyen := 100000;
                WHEN 'Business' THEN prix_adulte_moyen := 200000;
                WHEN 'Première' THEN prix_adulte_moyen := 350000;
                ELSE prix_adulte_moyen := 150000;
            END CASE;
        END IF;
        
        -- Analyser les remises existantes pour calculer les pourcentages moyens
        -- IMPORTANT: Dans remise_categorie_place, remise_prix = MONTANT de la remise (pas le prix final)
        -- Donc: prix_final = prix_adulte - remise_prix
        -- Et: pourcentage = (prix_final / prix_adulte) * 100 = ((prix_adulte - remise_prix) / prix_adulte) * 100
        
        -- Enfant
        SELECT AVG(
            CASE 
                WHEN prix_adulte_moyen > 0 THEN 
                    (((prix_adulte_moyen - remise_prix) / prix_adulte_moyen) * 100)
                ELSE 70
            END
        ) INTO pct_enfant
        FROM remise_categorie_place rcp
        JOIN categorie_passager cp ON rcp.id_categorie_passager = cp.id_categorie_passager
        WHERE rcp.id_classe_siege = classe_record.id
        AND LOWER(cp.libelle) LIKE '%enfant%';
        
        -- Bébé
        SELECT AVG(
            CASE 
                WHEN prix_adulte_moyen > 0 THEN 
                    (((prix_adulte_moyen - remise_prix) / prix_adulte_moyen) * 100)
                ELSE 30
            END
        ) INTO pct_bebe
        FROM remise_categorie_place rcp
        JOIN categorie_passager cp ON rcp.id_categorie_passager = cp.id_categorie_passager
        WHERE rcp.id_classe_siege = classe_record.id
        AND (LOWER(cp.libelle) LIKE '%bébé%' OR LOWER(cp.libelle) LIKE '%bebe%');
        
        -- Senior
        SELECT AVG(
            CASE 
                WHEN prix_adulte_moyen > 0 THEN 
                    (((prix_adulte_moyen - remise_prix) / prix_adulte_moyen) * 100)
                ELSE 80
            END
        ) INTO pct_senior
        FROM remise_categorie_place rcp
        JOIN categorie_passager cp ON rcp.id_categorie_passager = cp.id_categorie_passager
        WHERE rcp.id_classe_siege = classe_record.id
        AND LOWER(cp.libelle) LIKE '%senior%';
        
        -- Étudiant
        SELECT AVG(
            CASE 
                WHEN prix_adulte_moyen > 0 THEN 
                    (((prix_adulte_moyen - remise_prix) / prix_adulte_moyen) * 100)
                ELSE 85
            END
        ) INTO pct_etudiant
        FROM remise_categorie_place rcp
        JOIN categorie_passager cp ON rcp.id_categorie_passager = cp.id_categorie_passager
        WHERE rcp.id_classe_siege = classe_record.id
        AND (LOWER(cp.libelle) LIKE '%étudiant%' OR LOWER(cp.libelle) LIKE '%etudiant%');
        
        -- Valeurs par défaut si aucune donnée n'est trouvée
        pct_enfant := COALESCE(pct_enfant, 70.00);
        pct_bebe := COALESCE(pct_bebe, 30.00);
        pct_senior := COALESCE(pct_senior, 80.00);
        pct_etudiant := COALESCE(pct_etudiant, 85.00);
        
        -- Arrondir à 2 décimales et s'assurer que les valeurs sont entre 0 et 100
        pct_enfant := LEAST(100, GREATEST(0, ROUND(pct_enfant, 2)));
        pct_bebe := LEAST(100, GREATEST(0, ROUND(pct_bebe, 2)));
        pct_senior := LEAST(100, GREATEST(0, ROUND(pct_senior, 2)));
        pct_etudiant := LEAST(100, GREATEST(0, ROUND(pct_etudiant, 2)));
        
        -- Insérer la configuration
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
            'Configuration Standard - ' || classe_record.libelle,
            'Configuration générée automatiquement à partir des données existantes',
            classe_record.id,
            prix_adulte_moyen,
            pct_enfant,
            pct_bebe,
            pct_senior,
            pct_etudiant,
            TRUE,
            FALSE
        );
        
        RAISE NOTICE 'Configuration créée pour %: Prix adulte = %, Enfant = %%, Bébé = %%, Senior = %%, Étudiant = %%',
            classe_record.libelle, prix_adulte_moyen, pct_enfant, pct_bebe, pct_senior, pct_etudiant;
    END LOOP;
END $$;

-- =====================================================
-- 3) CRÉATION D'UNE CONFIGURATION GLOBALE PAR DÉFAUT
-- =====================================================

-- Cette configuration s'applique à toutes les classes qui n'ont pas de configuration spécifique
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
    'Configuration Globale Standard',
    'Configuration par défaut applicable à toutes les classes',
    NULL,
    150000.00,
    70.00,
    30.00,
    80.00,
    85.00,
    FALSE, -- Inactive par défaut, l'utilisateur peut l'activer si nécessaire
    TRUE
) ON CONFLICT DO NOTHING;

-- =====================================================
-- 4) VÉRIFICATION ET RAPPORT
-- =====================================================

-- Afficher les configurations créées
SELECT 
    c.id,
    c.nom,
    COALESCE(cs.libelle, 'Toutes les classes') as classe,
    c.prix_adulte,
    c.pourcentage_enfant || '%' as enfant,
    c.pourcentage_bebe || '%' as bebe,
    c.pourcentage_senior || '%' as senior,
    c.pourcentage_etudiant || '%' as etudiant,
    CASE WHEN c.actif THEN 'Active' ELSE 'Inactive' END as statut
FROM configuration_remise_personnalisee c
LEFT JOIN classe_siege cs ON c.classe_siege_id = cs.id
ORDER BY c.appliquer_toutes_classes DESC, cs.libelle;

-- =====================================================
-- 5) NOTES D'UTILISATION
-- =====================================================

/*
COMMENT ON TABLE configuration_remise_personnalisee IS 
'Stocke les configurations de remise personnalisée. 
Le prix adulte est fixe, les autres catégories sont calculées en pourcentage.
Exemple: Si prix_adulte = 100000 et pourcentage_enfant = 70, alors prix_enfant = 70000 Ar.';

COMMENT ON COLUMN configuration_remise_personnalisee.appliquer_toutes_classes IS
'Si TRUE, cette configuration s''applique à toutes les classes de siège qui n''ont pas de configuration spécifique.
Une seule configuration avec appliquer_toutes_classes=TRUE peut être active à la fois.';

COMMENT ON COLUMN configuration_remise_personnalisee.actif IS
'Indique si cette configuration est actuellement active et utilisée pour les calculs de prix.';

COMMENT ON COLUMN configuration_remise_personnalisee.pourcentage_enfant IS
'Pourcentage du prix adulte à appliquer pour la catégorie Enfant (0-100).';

COMMENT ON COLUMN configuration_remise_personnalisee.pourcentage_bebe IS
'Pourcentage du prix adulte à appliquer pour la catégorie Bébé (0-100).';

COMMENT ON COLUMN configuration_remise_personnalisee.pourcentage_senior IS
'Pourcentage du prix adulte à appliquer pour la catégorie Senior (0-100).';

COMMENT ON COLUMN configuration_remise_personnalisee.pourcentage_etudiant IS
'Pourcentage du prix adulte à appliquer pour la catégorie Étudiant (0-100).';
*/

-- =====================================================
-- 6) INFORMATIONS IMPORTANTES
-- =====================================================

-- Ce script:
-- 1. Crée la table configuration_remise_personnalisee
-- 2. Analyse les données existantes (prix_vol et remise_categorie_place)
-- 3. Génère automatiquement une configuration par classe de siège
-- 4. Crée une configuration globale inactive (à activer manuellement si besoin)
--
-- Après l'exécution de ce script:
-- - Vérifiez les configurations créées
-- - Ajustez les pourcentages si nécessaire via l'interface web
-- - Les anciennes données (remise_categorie_place) restent intactes
-- - Le nouveau système coexiste avec l'ancien

-- =====================================================
-- FIN DU SCRIPT
-- =====================================================
