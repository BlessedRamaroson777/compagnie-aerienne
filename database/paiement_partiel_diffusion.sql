-- Script pour gérer les paiements partiels de diffusion

-- Ajouter des colonnes à la table diffusion_societe pour gérer le total et reste à payer
ALTER TABLE diffusion_societe ADD COLUMN IF NOT EXISTS montant_total_a_payer NUMERIC(15, 2) DEFAULT 0;
ALTER TABLE diffusion_societe ADD COLUMN IF NOT EXISTS montant_total_paye NUMERIC(15, 2) DEFAULT 0;
ALTER TABLE diffusion_societe ADD COLUMN IF NOT EXISTS pourcentage_paye NUMERIC(5, 2) DEFAULT 0;

-- Ajouter des colonnes à la table diffusion_vol_programme pour gérer le reste à payer
ALTER TABLE diffusion_vol_programme ADD COLUMN IF NOT EXISTS montant_total NUMERIC(15, 2) DEFAULT 0;
ALTER TABLE diffusion_vol_programme ADD COLUMN IF NOT EXISTS montant_paye NUMERIC(15, 2) DEFAULT 0;
ALTER TABLE diffusion_vol_programme ADD COLUMN IF NOT EXISTS reste_a_payer NUMERIC(15, 2) DEFAULT 0;

-- Commentaires
COMMENT ON COLUMN diffusion_societe.montant_total_a_payer IS 'Montant total à payer pour cette diffusion société (nombre * prix de configuration)';
COMMENT ON COLUMN diffusion_societe.montant_total_paye IS 'Montant total déjà payé';
COMMENT ON COLUMN diffusion_societe.pourcentage_paye IS 'Pourcentage payé (montant_total_paye / montant_total_a_payer * 100)';

COMMENT ON COLUMN diffusion_vol_programme.montant_total IS 'Montant total pour cette diffusion vol programme (nombre * prix unitaire)';
COMMENT ON COLUMN diffusion_vol_programme.montant_paye IS 'Montant payé selon le pourcentage de paiement de la diffusion société';
COMMENT ON COLUMN diffusion_vol_programme.reste_a_payer IS 'Reste à payer pour cette diffusion vol programme';

-- Fonction pour calculer et mettre à jour les montants après un paiement
CREATE OR REPLACE FUNCTION update_montants_diffusion_apres_paiement()
RETURNS TRIGGER AS $$
DECLARE
    v_id_diffusion_societe BIGINT;
    v_montant_total NUMERIC(15, 2);
    v_montant_total_paye NUMERIC(15, 2);
    v_pourcentage NUMERIC(5, 2);
    v_diffusion_vol RECORD;
BEGIN
    -- Déterminer l'ID de la diffusion société (NEW pour INSERT/UPDATE, OLD pour DELETE)
    IF TG_OP = 'DELETE' THEN
        v_id_diffusion_societe := OLD.id_diffusion_societe;
    ELSE
        v_id_diffusion_societe := NEW.id_diffusion_societe;
    END IF;
    
    -- Calculer le montant total à payer pour la diffusion société (nombre * prix de configuration)
    SELECT ds.nombre * COALESCE(ccd.prix, 0)
    INTO v_montant_total
    FROM diffusion_societe ds
    LEFT JOIN configuration_cout_diffusion ccd ON ccd.id_diffusion_societe = ds.id_diffusion_societe
    WHERE ds.id_diffusion_societe = v_id_diffusion_societe;
    
    -- Calculer le montant total payé
    SELECT COALESCE(SUM(pd.montant_paye), 0)
    INTO v_montant_total_paye
    FROM paiement_diffusion pd
    WHERE pd.id_diffusion_societe = v_id_diffusion_societe;
    
    -- Calculer le pourcentage payé
    IF v_montant_total > 0 THEN
        v_pourcentage := (v_montant_total_paye / v_montant_total) * 100;
    ELSE
        v_pourcentage := 0;
    END IF;
    
    -- Mettre à jour la diffusion société
    UPDATE diffusion_societe
    SET montant_total_a_payer = v_montant_total,
        montant_total_paye = v_montant_total_paye,
        pourcentage_paye = v_pourcentage
    WHERE id_diffusion_societe = v_id_diffusion_societe;
    
    -- Mettre à jour chaque diffusion vol programme
    FOR v_diffusion_vol IN
        SELECT dvp.id_diffusion_vol_programme, 
               dvp.nombre,
               ccd.prix
        FROM diffusion_vol_programme dvp
        JOIN configuration_cout_diffusion ccd ON ccd.id_diffusion_societe = dvp.id_diffusion_societe
        WHERE dvp.id_diffusion_societe = v_id_diffusion_societe
    LOOP
        UPDATE diffusion_vol_programme
        SET montant_total = v_diffusion_vol.nombre * v_diffusion_vol.prix,
            montant_paye = (v_diffusion_vol.nombre * v_diffusion_vol.prix * v_pourcentage / 100),
            reste_a_payer = (v_diffusion_vol.nombre * v_diffusion_vol.prix * (100 - v_pourcentage) / 100)
        WHERE id_diffusion_vol_programme = v_diffusion_vol.id_diffusion_vol_programme;
    END LOOP;
    
    -- Retourner la bonne valeur selon l'opération
    IF TG_OP = 'DELETE' THEN
        RETURN OLD;
    ELSE
        RETURN NEW;
    END IF;
END;
$$ LANGUAGE plpgsql;

-- Créer le trigger sur la table paiement_diffusion
DROP TRIGGER IF EXISTS trigger_update_montants_diffusion ON paiement_diffusion;
CREATE TRIGGER trigger_update_montants_diffusion
AFTER INSERT OR UPDATE OR DELETE ON paiement_diffusion
FOR EACH ROW
EXECUTE FUNCTION update_montants_diffusion_apres_paiement();

-- Fonction pour initialiser les montants lors de l'ajout d'une diffusion vol programme
CREATE OR REPLACE FUNCTION init_montants_diffusion_vol_programme()
RETURNS TRIGGER AS $$
DECLARE
    v_prix NUMERIC(15, 2);
    v_pourcentage NUMERIC(5, 2);
BEGIN
    -- Récupérer le prix unitaire
    SELECT prix INTO v_prix
    FROM configuration_cout_diffusion
    WHERE id_diffusion_societe = NEW.id_diffusion_societe
    LIMIT 1;
    
    -- Récupérer le pourcentage payé
    SELECT COALESCE(pourcentage_paye, 0) INTO v_pourcentage
    FROM diffusion_societe
    WHERE id_diffusion_societe = NEW.id_diffusion_societe;
    
    -- Initialiser les montants
    NEW.montant_total := NEW.nombre * COALESCE(v_prix, 0);
    NEW.montant_paye := NEW.montant_total * v_pourcentage / 100;
    NEW.reste_a_payer := NEW.montant_total - NEW.montant_paye;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Créer le trigger sur la table diffusion_vol_programme
DROP TRIGGER IF EXISTS trigger_init_montants_dvp ON diffusion_vol_programme;
CREATE TRIGGER trigger_init_montants_dvp
BEFORE INSERT OR UPDATE ON diffusion_vol_programme
FOR EACH ROW
EXECUTE FUNCTION init_montants_diffusion_vol_programme();

-- Fonction pour mettre à jour le montant total de la diffusion société
CREATE OR REPLACE FUNCTION update_montant_total_diffusion_societe()
RETURNS TRIGGER AS $$
DECLARE
    v_prix NUMERIC(15, 2);
    v_montant_total NUMERIC(15, 2);
BEGIN
    -- Récupérer le prix de la configuration
    SELECT prix INTO v_prix
    FROM configuration_cout_diffusion
    WHERE id_diffusion_societe = NEW.id_diffusion_societe
    LIMIT 1;
    
    -- Calculer le montant total
    v_montant_total := NEW.nombre * COALESCE(v_prix, 0);
    
    -- Mettre à jour le montant total
    UPDATE diffusion_societe
    SET montant_total_a_payer = v_montant_total
    WHERE id_diffusion_societe = NEW.id_diffusion_societe;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Créer le trigger sur la table diffusion_societe pour mise à jour du montant
DROP TRIGGER IF EXISTS trigger_update_montant_diffusion_societe ON diffusion_societe;
CREATE TRIGGER trigger_update_montant_diffusion_societe
AFTER INSERT OR UPDATE OF nombre ON diffusion_societe
FOR EACH ROW
EXECUTE FUNCTION update_montant_total_diffusion_societe();

-- Fonction pour mettre à jour le montant total quand le prix change
CREATE OR REPLACE FUNCTION update_montant_total_on_prix_change()
RETURNS TRIGGER AS $$
DECLARE
    v_nombre INTEGER;
    v_montant_total NUMERIC(15, 2);
BEGIN
    -- Récupérer le nombre de la diffusion société
    SELECT nombre INTO v_nombre
    FROM diffusion_societe
    WHERE id_diffusion_societe = NEW.id_diffusion_societe;
    
    -- Calculer le montant total
    v_montant_total := v_nombre * NEW.prix;
    
    -- Mettre à jour le montant total
    UPDATE diffusion_societe
    SET montant_total_a_payer = v_montant_total
    WHERE id_diffusion_societe = NEW.id_diffusion_societe;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Créer le trigger sur la table configuration_cout_diffusion
DROP TRIGGER IF EXISTS trigger_update_montant_on_prix ON configuration_cout_diffusion;
CREATE TRIGGER trigger_update_montant_on_prix
AFTER INSERT OR UPDATE OF prix ON configuration_cout_diffusion
FOR EACH ROW
EXECUTE FUNCTION update_montant_total_on_prix_change();

-- ========================================
-- INITIALISATION DES DONNÉES EXISTANTES
-- ========================================

-- Mettre à jour les montants totaux pour toutes les diffusions société existantes
UPDATE diffusion_societe ds
SET montant_total_a_payer = ds.nombre * COALESCE(ccd.prix, 0)
FROM configuration_cout_diffusion ccd
WHERE ccd.id_diffusion_societe = ds.id_diffusion_societe;

-- Mettre à jour les montants payés pour toutes les diffusions société
UPDATE diffusion_societe ds
SET montant_total_paye = COALESCE((
    SELECT SUM(pd.montant_paye)
    FROM paiement_diffusion pd
    WHERE pd.id_diffusion_societe = ds.id_diffusion_societe
), 0);

-- Mettre à jour les pourcentages payés
UPDATE diffusion_societe
SET pourcentage_paye = CASE 
    WHEN montant_total_a_payer > 0 THEN (montant_total_paye / montant_total_a_payer) * 100
    ELSE 0
END;

-- Mettre à jour les montants pour tous les vols programmés existants
UPDATE diffusion_vol_programme dvp
SET 
    montant_total = dvp.nombre * COALESCE(ccd.prix, 0),
    montant_paye = dvp.nombre * COALESCE(ccd.prix, 0) * COALESCE(ds.pourcentage_paye, 0) / 100,
    reste_a_payer = dvp.nombre * COALESCE(ccd.prix, 0) * (100 - COALESCE(ds.pourcentage_paye, 0)) / 100
FROM configuration_cout_diffusion ccd, diffusion_societe ds
WHERE ccd.id_diffusion_societe = dvp.id_diffusion_societe
  AND ds.id_diffusion_societe = dvp.id_diffusion_societe;
