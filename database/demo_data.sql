-- Script SQL pour insérer des données de démonstration
-- À exécuter après le premier démarrage de l'application

-- ==================================================
-- AVIONS
-- ==================================================
INSERT INTO avion (code_avion, modele, capacite_totale) VALUES
('AF001', 'Boeing 737-800', 150),
('AF002', 'Airbus A320', 180),
('AF003', 'Boeing 777-300', 350);

-- Les sièges seront créés automatiquement par l'application lors de la sauvegarde

-- ==================================================
-- VOLS (à adapter selon les IDs des avions créés)
-- ==================================================
-- Note: Remplacer les avion_id par les vrais IDs après insertion des avions
INSERT INTO vol (numero_vol, depart, arrivee, date_heure_depart, date_heure_arrivee, prix_base, avion_id) VALUES
('AF100', 'Paris CDG', 'New York JFK', '2024-06-15 10:00:00', '2024-06-15 18:30:00', 850.00, 1),
('AF200', 'Lyon', 'Londres', '2024-06-20 14:00:00', '2024-06-20 15:30:00', 120.00, 2),
('AF300', 'Paris CDG', 'Tokyo Narita', '2024-07-01 23:00:00', '2024-07-02 18:00:00', 1200.00, 3),
('AF101', 'New York JFK', 'Paris CDG', '2024-06-16 20:00:00', '2024-06-17 10:30:00', 900.00, 1),
('AF201', 'Londres', 'Lyon', '2024-06-21 16:00:00', '2024-06-21 17:30:00', 130.00, 2);

-- ==================================================
-- CLIENTS
-- ==================================================
INSERT INTO client (nom, prenom, email, telephone) VALUES
('Dupont', 'Jean', 'jean.dupont@email.com', '+33 6 12 34 56 78'),
('Martin', 'Sophie', 'sophie.martin@email.com', '+33 6 98 76 54 32'),
('Bernard', 'Pierre', 'pierre.bernard@email.com', '+33 6 11 22 33 44'),
('Dubois', 'Marie', 'marie.dubois@email.com', '+33 6 55 66 77 88'),
('Moreau', 'Luc', 'luc.moreau@email.com', '+33 6 44 33 22 11'),
('Laurent', 'Isabelle', 'isabelle.laurent@email.com', '+33 6 77 88 99 00');

-- ==================================================
-- ÉQUIPAGE
-- ==================================================
INSERT INTO equipage (nom, prenom, role) VALUES
('Leclerc', 'Antoine', 'PILOTE'),
('Moreau', 'François', 'COPILOTE'),
('Laurent', 'Isabelle', 'HOTESSE'),
('Simon', 'Thomas', 'STEWARD'),
('Petit', 'Charlotte', 'PILOTE'),
('Robert', 'Nicolas', 'COPILOTE'),
('Richard', 'Julie', 'HOTESSE'),
('Durand', 'Alexandre', 'STEWARD');

-- ==================================================
-- MÉTHODES DE PAIEMENT
-- ==================================================
-- Ces données sont déjà insérées automatiquement par l'application via DataInitializer
-- Mais si besoin, voici le script :
-- INSERT INTO methode_paiement (libelle) VALUES
-- ('Carte bancaire'),
-- ('Espèces'),
-- ('Mobile Money'),
-- ('Virement bancaire');

-- ==================================================
-- RÉSERVATIONS (Exemples)
-- ==================================================
-- Note: À adapter selon les IDs réels des clients et vols

-- Réservation 1: Jean Dupont sur vol AF100
INSERT INTO reservation (client_id, vol_id, date_reservation, statut) VALUES
(1, 1, '2024-05-20', 'CONFIRMEE');

-- Réservation 2: Sophie Martin sur vol AF200
INSERT INTO reservation (client_id, vol_id, date_reservation, statut) VALUES
(2, 2, '2024-05-21', 'CONFIRMEE');

-- Réservation 3: Pierre Bernard sur vol AF300
INSERT INTO reservation (client_id, vol_id, date_reservation, statut) VALUES
(3, 3, '2024-05-22', 'CONFIRMEE');

-- ==================================================
-- AFFECTATIONS ÉQUIPAGE AUX VOLS
-- ==================================================
-- Vol AF100: Équipe complète
INSERT INTO vol_equipage (vol_id, equipage_id) VALUES
(1, 1),  -- Pilote
(1, 2),  -- Copilote
(1, 3),  -- Hôtesse
(1, 4);  -- Steward

-- Vol AF200: Équipe complète
INSERT INTO vol_equipage (vol_id, equipage_id) VALUES
(2, 5),  -- Pilote
(2, 6),  -- Copilote
(2, 7),  -- Hôtesse
(2, 8);  -- Steward

-- Vol AF300: Équipe complète
INSERT INTO vol_equipage (vol_id, equipage_id) VALUES
(3, 1),  -- Pilote
(3, 2),  -- Copilote
(3, 7),  -- Hôtesse
(3, 8);  -- Steward

-- ==================================================
-- NOTES IMPORTANTES
-- ==================================================
-- 1. Les sièges (table siege) sont créés automatiquement par l'application
--    lors de la création d'un avion
--
-- 2. Les sièges-vol (table siege_vol) sont créés automatiquement 
--    lors de la création d'un vol
--
-- 3. Les billets doivent être créés via l'interface web pour garantir
--    l'intégrité des données et la mise à jour de l'état des sièges
--
-- 4. Les paiements sont créés via l'interface ou peuvent être ajoutés ici
--    mais nécessitent la création des paiement_methode associés

-- ==================================================
-- VÉRIFICATIONS
-- ==================================================
-- Compter les avions
SELECT COUNT(*) as nb_avions FROM avion;

-- Compter les vols
SELECT COUNT(*) as nb_vols FROM vol;

-- Compter les clients
SELECT COUNT(*) as nb_clients FROM client;

-- Compter l'équipage
SELECT COUNT(*) as nb_equipage FROM equipage;

-- Voir les réservations avec détails
SELECT 
    r.id,
    c.nom || ' ' || c.prenom as client,
    v.numero_vol,
    r.statut
FROM reservation r
JOIN client c ON r.client_id = c.id
JOIN vol v ON r.vol_id = v.id;

-- Voir les vols avec leur équipage
SELECT 
    v.numero_vol,
    e.nom || ' ' || e.prenom as membre,
    e.role
FROM vol_equipage ve
JOIN vol v ON ve.vol_id = v.id
JOIN equipage e ON ve.equipage_id = e.id
ORDER BY v.numero_vol, e.role;
