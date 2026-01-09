-- Script de données de test pour l'application Airline
-- Utiliser ce script après la création des tables par Hibernate

-- Nettoyage des données existantes
DELETE FROM billets;
DELETE FROM paiements;
DELETE FROM reservations;
DELETE FROM sieges;
DELETE FROM prix_vols;
DELETE FROM equipage_vol;
DELETE FROM equipages;
DELETE FROM roles_equipage;
DELETE FROM vols;
DELETE FROM clients;
DELETE FROM etats_avion;
DELETE FROM avions;
DELETE FROM modeles_avion;
DELETE FROM classes_siege;
DELETE FROM aeroports;

-- 1. Classes de siège
INSERT INTO classes_siege (nom, description, created_at) VALUES
('Economique', 'Classe standard avec services de base', NOW()),
('Affaires', 'Classe premium avec services améliorés', NOW()),
('Première', 'Classe luxe avec services exclusifs', NOW());

-- 2. Modèles d''avion
INSERT INTO modeles_avion (nom, fabricant, capacite_totale, vitesse_croisiere, autonomie, annee_fabrication, created_at) VALUES
('Boeing 737-800', 'Boeing', 189, 850, 5765, 2015, NOW()),
('Airbus A320', 'Airbus', 180, 840, 6100, 2018, NOW()),
('Boeing 777-300ER', 'Boeing', 396, 905, 13649, 2016, NOW()),
('Airbus A350-900', 'Airbus', 325, 903, 15000, 2019, NOW());

-- 3. États d''avion
INSERT INTO etats_avion (libelle, description, actif, created_at) VALUES
('En Service', 'Avion opérationnel et disponible', true, NOW()),
('En Maintenance', 'Avion en révision technique', true, NOW()),
('Au Sol', 'Avion temporairement immobilisé', true, NOW()),
('Retiré', 'Avion hors service', false, NOW());

-- 4. Avions
INSERT INTO avions (code_avion, id_modele, capacite_totale, id_etat, date_acquisition, heures_vol, created_at) VALUES
('AF-001', 1, 189, 1, '2015-06-15', 15230, NOW()),
('AF-002', 2, 180, 1, '2018-03-22', 8640, NOW()),
('AF-003', 3, 396, 2, '2016-11-08', 12850, NOW()),
('AF-004', 4, 325, 1, '2019-05-14', 5420, NOW()),
('AF-005', 1, 189, 1, '2017-09-30', 10230, NOW());

-- 5. Sièges pour chaque avion
-- AF-001 (Boeing 737): 42 Economique, 12 Affaires
INSERT INTO sieges (numero_siege, id_avion, id_classe, disponible, created_at)
SELECT 
    CONCAT(
        CHAR(65 + (ROW_NUMBER() OVER (ORDER BY n) - 1) / 6), 
        ((ROW_NUMBER() OVER (ORDER BY n) - 1) % 6 + 1)
    ),
    1,
    CASE WHEN (ROW_NUMBER() OVER (ORDER BY n)) <= 12 THEN 2 ELSE 1 END,
    true,
    NOW()
FROM (
    SELECT 1 as n UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL 
    SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9
) AS numbers
LIMIT 54;

-- 6. Aéroports
INSERT INTO aeroports (code_iata, nom, ville, pays, fuseau_horaire, created_at) VALUES
('CDG', 'Aéroport Charles de Gaulle', 'Paris', 'France', 'Europe/Paris', NOW()),
('JFK', 'John F. Kennedy International Airport', 'New York', 'États-Unis', 'America/New_York', NOW()),
('DXB', 'Dubai International Airport', 'Dubaï', 'Émirats Arabes Unis', 'Asia/Dubai', NOW()),
('NRT', 'Narita International Airport', 'Tokyo', 'Japon', 'Asia/Tokyo', NOW()),
('LHR', 'London Heathrow', 'Londres', 'Royaume-Uni', 'Europe/London', NOW()),
('LAX', 'Los Angeles International Airport', 'Los Angeles', 'États-Unis', 'America/Los_Angeles', NOW());

-- 7. Vols
INSERT INTO vols (numero_vol, depart, arrivee, date_heure_depart, date_heure_arrivee, prix_base, id_avion, statut, created_at) VALUES
('AF1234', 'CDG', 'JFK', '2024-03-15 10:00:00', '2024-03-15 13:00:00', 450.00, 1, 'PLANIFIE', NOW()),
('AF5678', 'JFK', 'CDG', '2024-03-16 15:00:00', '2024-03-17 05:00:00', 480.00, 1, 'PLANIFIE', NOW()),
('AF2468', 'CDG', 'DXB', '2024-03-18 22:00:00', '2024-03-19 07:00:00', 580.00, 2, 'PLANIFIE', NOW()),
('AF1357', 'DXB', 'NRT', '2024-03-20 09:00:00', '2024-03-20 21:00:00', 750.00, 3, 'PLANIFIE', NOW()),
('AF9876', 'LHR', 'LAX', '2024-03-22 11:00:00', '2024-03-22 14:00:00', 620.00, 4, 'PLANIFIE', NOW());

-- 8. Prix par classe pour chaque vol
INSERT INTO prix_vols (id_vol, id_classe, prix_classe, disponibilite, created_at) VALUES
-- Vol AF1234
(1, 1, 450.00, 42, NOW()),
(1, 2, 1200.00, 12, NOW()),
-- Vol AF5678
(2, 1, 480.00, 42, NOW()),
(2, 2, 1280.00, 12, NOW()),
-- Vol AF2468
(3, 1, 580.00, 45, NOW()),
(3, 2, 1550.00, 15, NOW()),
-- Vol AF1357
(4, 1, 750.00, 250, NOW()),
(4, 2, 2100.00, 80, NOW()),
(4, 3, 4500.00, 30, NOW()),
-- Vol AF9876
(5, 1, 620.00, 180, NOW()),
(5, 2, 1850.00, 60, NOW()),
(5, 3, 3900.00, 25, NOW());

-- 9. Clients
INSERT INTO clients (nom, prenom, email, telephone, date_naissance, passeport, created_at) VALUES
('Dupont', 'Jean', 'jean.dupont@email.fr', '+33612345678', '1985-04-12', 'FR123456789', NOW()),
('Martin', 'Sophie', 'sophie.martin@email.fr', '+33623456789', '1990-08-25', 'FR234567890', NOW()),
('Bernard', 'Pierre', 'pierre.bernard@email.fr', '+33634567890', '1978-02-15', 'FR345678901', NOW()),
('Dubois', 'Marie', 'marie.dubois@email.fr', '+33645678901', '1995-11-30', 'FR456789012', NOW()),
('Thomas', 'Luc', 'luc.thomas@email.fr', '+33656789012', '1982-06-18', 'FR567890123', NOW());

-- 10. Réservations
INSERT INTO reservations (numero_reservation, id_client, id_vol, date_reservation, statut, nombre_passagers, created_at) VALUES
('RES-2024-001', 1, 1, '2024-01-10 14:30:00', 'CONFIRMEE', 2, NOW()),
('RES-2024-002', 2, 1, '2024-01-12 09:15:00', 'CONFIRMEE', 1, NOW()),
('RES-2024-003', 3, 3, '2024-01-15 16:45:00', 'CONFIRMEE', 3, NOW()),
('RES-2024-004', 4, 4, '2024-01-20 11:20:00', 'EN_ATTENTE', 2, NOW()),
('RES-2024-005', 5, 5, '2024-01-25 10:00:00', 'CONFIRMEE', 1, NOW());

-- 11. Billets
INSERT INTO billets (numero_billet, id_reservation, id_siege, nom_passager, prenom_passager, date_naissance_passager, statut, created_at) VALUES
-- Réservation 1 (2 passagers)
('TKT-2024-001-1', 1, 1, 'Dupont', 'Jean', '1985-04-12', 'EMIS', NOW()),
('TKT-2024-001-2', 1, 2, 'Dupont', 'Claire', '1987-09-08', 'EMIS', NOW()),
-- Réservation 2 (1 passager)
('TKT-2024-002-1', 2, 3, 'Martin', 'Sophie', '1990-08-25', 'EMIS', NOW()),
-- Réservation 3 (3 passagers)
('TKT-2024-003-1', 3, 4, 'Bernard', 'Pierre', '1978-02-15', 'EMIS', NOW()),
('TKT-2024-003-2', 3, 5, 'Bernard', 'Anne', '1980-05-20', 'EMIS', NOW()),
('TKT-2024-003-3', 3, 6, 'Bernard', 'Tom', '2010-12-03', 'EMIS', NOW()),
-- Réservation 5 (1 passager)
('TKT-2024-005-1', 5, 7, 'Thomas', 'Luc', '1982-06-18', 'EMIS', NOW());

-- 12. Paiements
INSERT INTO paiements (id_reservation, montant, date_paiement, mode_paiement, statut, numero_transaction, created_at) VALUES
(1, 900.00, '2024-01-10 14:35:00', 'CARTE_CREDIT', 'EFFECTUE', 'TXN-20240110-001', NOW()),
(2, 450.00, '2024-01-12 09:20:00', 'CARTE_CREDIT', 'EFFECTUE', 'TXN-20240112-002', NOW()),
(3, 1740.00, '2024-01-15 16:50:00', 'PAYPAL', 'EFFECTUE', 'TXN-20240115-003', NOW()),
(5, 620.00, '2024-01-25 10:05:00', 'VIREMENT', 'EFFECTUE', 'TXN-20240125-005', NOW());

-- 13. Rôles d''équipage
INSERT INTO roles_equipage (nom_role, description, niveau_responsabilite, created_at) VALUES
('Commandant de Bord', 'Pilote responsable du vol', 5, NOW()),
('Copilote', 'Second pilote assistant', 4, NOW()),
('Chef de Cabine', 'Responsable du personnel de cabine', 3, NOW()),
('Steward/Hôtesse', 'Personnel de service en cabine', 2, NOW()),
('Mécanicien de Bord', 'Technicien pour maintenance en vol', 3, NOW());

-- 14. Membres d''équipage
INSERT INTO equipages (nom, prenom, email, telephone, date_naissance, id_role, numero_licence, heures_vol, created_at) VALUES
('Moreau', 'Antoine', 'antoine.moreau@airline.fr', '+33601020304', '1975-03-10', 1, 'CPT-FR-001', 12500, NOW()),
('Laurent', 'Julie', 'julie.laurent@airline.fr', '+33602030405', '1982-07-22', 2, 'FO-FR-002', 8200, NOW()),
('Petit', 'François', 'francois.petit@airline.fr', '+33603040506', '1980-11-15', 1, 'CPT-FR-003', 10800, NOW()),
('Roux', 'Isabelle', 'isabelle.roux@airline.fr', '+33604050607', '1988-05-30', 3, 'PNC-FR-004', 5600, NOW()),
('Simon', 'Marc', 'marc.simon@airline.fr', '+33605060708', '1992-09-18', 4, 'PNC-FR-005', 3200, NOW()),
('Michel', 'Emma', 'emma.michel@airline.fr', '+33606070809', '1994-02-25', 4, 'PNC-FR-006', 2800, NOW());

-- 15. Affectation équipage aux vols
INSERT INTO equipage_vol (id_equipage, id_vol) VALUES
-- Vol AF1234
(1, 1), -- Commandant Moreau
(2, 1), -- Copilote Laurent
(4, 1), -- Chef de cabine Roux
(5, 1), -- Steward Simon
-- Vol AF2468
(3, 3), -- Commandant Petit
(2, 3), -- Copilote Laurent
(4, 3), -- Chef de cabine Roux
(6, 3); -- Hôtesse Michel

-- Vérification des données insérées
SELECT 'Modèles d''avion' as Table_Name, COUNT(*) as Nombre FROM modeles_avion
UNION ALL SELECT 'Avions', COUNT(*) FROM avions
UNION ALL SELECT 'Sièges', COUNT(*) FROM sieges
UNION ALL SELECT 'Aéroports', COUNT(*) FROM aeroports
UNION ALL SELECT 'Vols', COUNT(*) FROM vols
UNION ALL SELECT 'Prix Vols', COUNT(*) FROM prix_vols
UNION ALL SELECT 'Clients', COUNT(*) FROM clients
UNION ALL SELECT 'Réservations', COUNT(*) FROM reservations
UNION ALL SELECT 'Billets', COUNT(*) FROM billets
UNION ALL SELECT 'Paiements', COUNT(*) FROM paiements
UNION ALL SELECT 'Rôles Équipage', COUNT(*) FROM roles_equipage
UNION ALL SELECT 'Membres Équipage', COUNT(*) FROM equipages
UNION ALL SELECT 'Affectations Équipage', COUNT(*) FROM equipage_vol;
