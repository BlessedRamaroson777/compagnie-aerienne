-- Script de test pour le module de diffusion

-- 1. Test d'insertion dans configuration_cout_diffusion
-- Le trigger devrait automatiquement insérer dans historique_configuration_cout
INSERT INTO configuration_cout_diffusion (id_diffusion, prix) 
VALUES (4, 20.00);

-- Vérifier l'insertion dans l'historique
SELECT * FROM historique_configuration_cout 
WHERE status = 'CREATION' 
ORDER BY date DESC 
LIMIT 1;

-- 2. Test de modification de configuration_cout_diffusion
UPDATE configuration_cout_diffusion 
SET prix = 25.00 
WHERE id_configuration_cout_diffusion = 1;

-- Vérifier la modification dans l'historique
SELECT * FROM historique_configuration_cout 
WHERE status = 'MODIFICATION' 
ORDER BY date DESC 
LIMIT 1;

-- 3. Créer des données de test pour le calcul du chiffre d'affaires
-- Supposons qu'il existe un vol programmé avec id=1
INSERT INTO diffusion_societe (id_societe, nombre, date) VALUES 
    (1, 200, '2026-01-15 10:00:00'),
    (2, 150, '2026-01-20 14:30:00'),
    (3, 100, '2026-02-05 09:00:00');

-- Récupérer les IDs des diffusions créées
-- Supposons que les IDs sont 4, 5, 6

INSERT INTO configuration_cout_diffusion (id_diffusion, prix) VALUES 
    (4, 12.50),
    (5, 15.00),
    (6, 18.00);

-- Lier les diffusions aux vols programmés (supposons que le vol programmé id=1 existe)
INSERT INTO diffusion_vol_programme (id_diffusion_societe, nombre, id_vol_programme) VALUES 
    (4, 50, 1),
    (5, 75, 1),
    (6, 30, 1);

-- 4. Calcul du chiffre d'affaires pour janvier 2026
-- Pour diffusion_societe id=4: 200 * 12.50 = 2500
-- Pour diffusion_societe id=5: 150 * 15.00 = 2250
-- Total pour janvier 2026: 4750

SELECT 
    ds.id_diffusion_societe,
    s.libelle as societe,
    ds.nombre,
    ds.date,
    ccd.prix,
    (ds.nombre * ccd.prix) as montant_total
FROM diffusion_societe ds
JOIN societe s ON ds.id_societe = s.id_societe
LEFT JOIN configuration_cout_diffusion ccd ON ccd.id_diffusion = ds.id_diffusion_societe
WHERE EXTRACT(MONTH FROM ds.date) = 1 
  AND EXTRACT(YEAR FROM ds.date) = 2026
ORDER BY ds.date;

-- 5. Vue pour consulter l'historique complet
SELECT 
    hcc.id_historique_configuration_cout,
    hcc.prix,
    hcc.status,
    hcc.date,
    CASE 
        WHEN hcc.status = 'CREATION' THEN 'Nouvelle configuration créée'
        WHEN hcc.status = 'MODIFICATION' THEN 'Configuration modifiée'
        ELSE hcc.status
    END as description
FROM historique_configuration_cout hcc
ORDER BY hcc.date DESC;

-- 6. Rapport des diffusions par société
SELECT 
    s.libelle as societe,
    COUNT(ds.id_diffusion_societe) as nombre_diffusions,
    SUM(ds.nombre) as total_elements_diffuses,
    AVG(ccd.prix) as prix_moyen,
    SUM(ds.nombre * COALESCE(ccd.prix, 0)) as chiffre_affaire_total
FROM societe s
LEFT JOIN diffusion_societe ds ON ds.id_societe = s.id_societe
LEFT JOIN configuration_cout_diffusion ccd ON ccd.id_diffusion = ds.id_diffusion_societe
GROUP BY s.id_societe, s.libelle
ORDER BY chiffre_affaire_total DESC;
