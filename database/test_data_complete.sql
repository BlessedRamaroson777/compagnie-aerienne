-- =====================================================
-- DONNÉES DE TEST COMPLÈTES POUR L'APPLICATION AIRLINE
-- =====================================================
-- Ce fichier contient toutes les données nécessaires pour tester l'application
-- Exécuter après le premier démarrage de l'application

-- =====================================================
-- 1) ÉTATS D'AVION
-- =====================================================
INSERT INTO etat_avion (libelle, description) VALUES
    ('OPERATIONNEL', 'Avion opérationnel et prêt au vol'),
    ('EN_MAINTENANCE', 'Avion en maintenance programmée'),
    ('IMMOBILISE', 'Avion immobilisé pour réparation'),
    ('DECOMMISSIONNE', 'Avion retiré du service');

-- =====================================================
-- 2) MODÈLES D'AVION
-- =====================================================
INSERT INTO model_avion (designation, fabricant, capacite, autonomie_km, vitesse_km_h, description) VALUES
    ('Boeing 747-8', 'Boeing', 416, 14685, 908, 'Gros porteur long-courrier quadriréacteur'),
    ('Airbus A380', 'Airbus', 555, 15000, 903, 'Plus gros avion de ligne du monde, double pont'),
    ('Boeing 777-300ER', 'Boeing', 365, 14685, 905, 'Gros porteur long-courrier biréacteur très fiable'),
    ('Airbus A350-900', 'Airbus', 325, 15000, 903, 'Long-courrier moderne avec cockpit numérique'),
    ('Boeing 787-9 Dreamliner', 'Boeing', 296, 14010, 903, 'Moyen-courrier long-distance économe en carburant'),
    ('Airbus A320neo', 'Airbus', 180, 6300, 840, 'Moyen-courrier nouvelle génération'),
    ('Boeing 737-800', 'Boeing', 189, 5765, 842, 'Avion moyen-courrier le plus populaire au monde');

-- =====================================================
-- 3) CLASSES DE SIÈGE
-- =====================================================
INSERT INTO classe_siege (libelle, description) VALUES 
    ('ECONOMY', 'Classe Économique - Sièges standards avec service de base'),
    ('BUSINESS', 'Classe Affaires - Sièges inclinables avec service premium');

-- =====================================================
-- 4) RÔLES D'ÉQUIPAGE
-- =====================================================
INSERT INTO role_equipage (libelle, description) VALUES 
    ('PILOTE', 'Commandant de bord'),
    ('COPILOTE', 'Second pilote'),
    ('HOTESSE', 'Hôtesse de l''air - Personnel de cabine féminin'),
    ('STEWARD', 'Steward - Personnel de cabine masculin');

-- =====================================================
-- 5) MÉTHODES DE PAIEMENT
-- =====================================================
INSERT INTO methode_paiement (libelle) VALUES 
    ('CARTE'),
    ('CASH'),
    ('MOBILE_MONEY'),
    ('VIREMENT');

-- =====================================================
-- 6) AVIONS (avec modèle et état)
-- =====================================================
-- Récupérer les IDs des états et modèles pour les références
-- État OPERATIONNEL = 1, Modèle Boeing 737-800 = 7, etc.

INSERT INTO avion (code_avion, model_avion_id, etat_avion_id, capacite_totale) VALUES
    ('AF-B737-001', 7, 1, 189),   -- Boeing 737-800, Opérationnel
    ('AF-A320-001', 6, 1, 180),   -- Airbus A320neo, Opérationnel
    ('AF-B777-001', 3, 1, 365),   -- Boeing 777-300ER, Opérationnel
    ('AF-A350-001', 4, 1, 325),   -- Airbus A350-900, Opérationnel
    ('AF-B787-001', 5, 1, 296),   -- Boeing 787-9, Opérationnel
    ('AF-A380-001', 2, 2, 555),   -- Airbus A380, En maintenance
    ('AF-B747-001', 1, 1, 416);   -- Boeing 747-8, Opérationnel

-- =====================================================
-- 7) SIÈGES (générés automatiquement par l'application)
-- =====================================================
-- Les sièges seront créés automatiquement par AvionService
-- lors de la création/modification d'un avion via l'interface
-- 70% Economy, 30% Business

-- =====================================================
-- 8) AÉROPORTS
-- =====================================================
INSERT INTO aeroport (code_aeroport, nom, ville, pays) VALUES
    ('CDG', 'Aéroport Paris-Charles de Gaulle', 'Paris', 'France'),
    ('ORY', 'Aéroport de Paris-Orly', 'Paris', 'France'),
    ('JFK', 'John F. Kennedy International Airport', 'New York', 'États-Unis'),
    ('LAX', 'Los Angeles International Airport', 'Los Angeles', 'États-Unis'),
    ('LHR', 'London Heathrow Airport', 'Londres', 'Royaume-Uni'),
    ('DXB', 'Dubai International Airport', 'Dubaï', 'Émirats arabes unis'),
    ('NRT', 'Narita International Airport', 'Tokyo', 'Japon'),
    ('SIN', 'Singapore Changi Airport', 'Singapour', 'Singapour'),
    ('HKG', 'Hong Kong International Airport', 'Hong Kong', 'Chine'),
    ('SYD', 'Sydney Kingsford Smith Airport', 'Sydney', 'Australie'),
    ('FRA', 'Frankfurt Airport', 'Francfort', 'Allemagne'),
    ('AMS', 'Amsterdam Airport Schiphol', 'Amsterdam', 'Pays-Bas'),
    ('MAD', 'Adolfo Suárez Madrid-Barajas Airport', 'Madrid', 'Espagne'),
    ('BCN', 'Barcelona-El Prat Airport', 'Barcelone', 'Espagne'),
    ('FCO', 'Leonardo da Vinci-Fiumicino Airport', 'Rome', 'Italie');

-- =====================================================
-- 9) VOLS
-- =====================================================
-- Vols Paris → New York
INSERT INTO vol (numero_vol, aeroport_depart_id, aeroport_arrivee_id, date_heure_depart, date_heure_arrivee, avion_id, status) VALUES
    ('AF100', 1, 3, '2026-02-15 10:00:00', '2026-02-15 18:30:00', 3, 'PLANIFIE'),
    ('AF101', 3, 1, '2026-02-16 20:00:00', '2026-02-17 10:00:00', 3, 'PLANIFIE');

-- Vols Paris → Londres
INSERT INTO vol (numero_vol, aeroport_depart_id, aeroport_arrivee_id, date_heure_depart, date_heure_arrivee, avion_id, status) VALUES
    ('AF200', 1, 5, '2026-02-20 08:00:00', '2026-02-20 09:30:00', 1, 'PLANIFIE'),
    ('AF201', 5, 1, '2026-02-20 14:00:00', '2026-02-20 15:30:00', 1, 'PLANIFIE');

-- Vols Paris → Tokyo
INSERT INTO vol (numero_vol, aeroport_depart_id, aeroport_arrivee_id, date_heure_depart, date_heure_arrivee, avion_id, status) VALUES
    ('AF300', 1, 7, '2026-03-01 23:00:00', '2026-03-02 18:00:00', 4, 'PLANIFIE'),
    ('AF301', 7, 1, '2026-03-05 10:00:00', '2026-03-05 16:30:00', 4, 'PLANIFIE');

-- Vols Paris → Dubaï
INSERT INTO vol (numero_vol, aeroport_depart_id, aeroport_arrivee_id, date_heure_depart, date_heure_arrivee, avion_id, status) VALUES
    ('AF400', 1, 6, '2026-03-10 14:00:00', '2026-03-10 23:00:00', 5, 'PLANIFIE'),
    ('AF401', 6, 1, '2026-03-12 02:00:00', '2026-03-12 07:30:00', 5, 'PLANIFIE');

-- Vols Paris → Sydney
INSERT INTO vol (numero_vol, aeroport_depart_id, aeroport_arrivee_id, date_heure_depart, date_heure_arrivee, avion_id, status) VALUES
    ('AF500', 1, 10, '2026-03-15 20:00:00', '2026-03-17 06:00:00', 7, 'PLANIFIE');

-- =====================================================
-- 10) PRIX DES VOLS (par classe)
-- =====================================================
-- Vol AF100 Paris → New York
INSERT INTO prix_vol (vol_id, classe_siege_id, prix) VALUES
    (1, 1, 450.00),    -- Economy
    (1, 2, 2500.00);   -- Business

-- Vol AF101 New York → Paris
INSERT INTO prix_vol (vol_id, classe_siege_id, prix) VALUES
    (2, 1, 480.00),    -- Economy
    (2, 2, 2800.00);   -- Business

-- Vol AF200 Paris → Londres
INSERT INTO prix_vol (vol_id, classe_siege_id, prix) VALUES
    (3, 1, 85.00),     -- Economy
    (3, 2, 350.00);    -- Business

-- Vol AF201 Londres → Paris
INSERT INTO prix_vol (vol_id, classe_siege_id, prix) VALUES
    (4, 1, 90.00),     -- Economy
    (4, 2, 380.00);    -- Business

-- Vol AF300 Paris → Tokyo
INSERT INTO prix_vol (vol_id, classe_siege_id, prix) VALUES
    (5, 1, 750.00),    -- Economy
    (5, 2, 4500.00);   -- Business

-- Vol AF301 Tokyo → Paris
INSERT INTO prix_vol (vol_id, classe_siege_id, prix) VALUES
    (6, 1, 780.00),    -- Economy
    (6, 2, 4800.00);   -- Business

-- Vol AF400 Paris → Dubaï
INSERT INTO prix_vol (vol_id, classe_siege_id, prix) VALUES
    (7, 1, 350.00),    -- Economy
    (7, 2, 1800.00);   -- Business

-- Vol AF401 Dubaï → Paris
INSERT INTO prix_vol (vol_id, classe_siege_id, prix) VALUES
    (8, 1, 380.00),    -- Economy
    (8, 2, 1950.00);   -- Business

-- Vol AF500 Paris → Sydney
INSERT INTO prix_vol (vol_id, classe_siege_id, prix) VALUES
    (9, 1, 950.00),    -- Economy
    (9, 2, 5500.00);   -- Business

-- =====================================================
-- 11) SIÈGES-VOL
-- =====================================================
-- Les sièges-vol seront créés automatiquement par VolService
-- lors de la création d'un vol via l'interface

-- =====================================================
-- 12) ÉQUIPAGE
-- =====================================================
-- Pilotes
INSERT INTO equipage (nom, prenom, role_id) VALUES
    ('Leclerc', 'Antoine', 1),
    ('Dubois', 'Jean-Pierre', 1),
    ('Martin', 'François', 1),
    ('Bernard', 'Philippe', 1);

-- Copilotes
INSERT INTO equipage (nom, prenom, role_id) VALUES
    ('Moreau', 'Nicolas', 2),
    ('Laurent', 'Alexandre', 2),
    ('Simon', 'Julien', 2),
    ('Michel', 'Thomas', 2);

-- Hôtesses
INSERT INTO equipage (nom, prenom, role_id) VALUES
    ('Petit', 'Sophie', 3),
    ('Durand', 'Marie', 3),
    ('Lefebvre', 'Camille', 3),
    ('Rousseau', 'Julie', 3),
    ('Vincent', 'Isabelle', 3);

-- Stewards
INSERT INTO equipage (nom, prenom, role_id) VALUES
    ('Robert', 'Luc', 4),
    ('Richard', 'Marc', 4),
    ('Garnier', 'David', 4),
    ('Faure', 'Pierre', 4);

-- =====================================================
-- 13) AFFECTATION ÉQUIPAGE AUX VOLS
-- =====================================================
-- Vol AF100 (Paris → New York)
INSERT INTO vol_equipage (vol_id, equipage_id) VALUES
    (1, 1),    -- Pilote: Leclerc
    (1, 5),    -- Copilote: Moreau
    (1, 9),    -- Hôtesse: Petit
    (1, 10),   -- Hôtesse: Durand
    (1, 14);   -- Steward: Robert

-- Vol AF200 (Paris → Londres)
INSERT INTO vol_equipage (vol_id, equipage_id) VALUES
    (3, 2),    -- Pilote: Dubois
    (3, 6),    -- Copilote: Laurent
    (3, 11),   -- Hôtesse: Lefebvre
    (3, 15);   -- Steward: Richard

-- Vol AF300 (Paris → Tokyo)
INSERT INTO vol_equipage (vol_id, equipage_id) VALUES
    (5, 3),    -- Pilote: Martin
    (5, 7),    -- Copilote: Simon
    (5, 12),   -- Hôtesse: Rousseau
    (5, 13),   -- Hôtesse: Vincent
    (5, 16);   -- Steward: Garnier

-- =====================================================
-- 14) CLIENTS
-- =====================================================
INSERT INTO client (nom, prenom, email, telephone) VALUES
    ('Dupont', 'Jean', 'jean.dupont@email.com', '+33 6 12 34 56 78'),
    ('Martin', 'Sophie', 'sophie.martin@email.com', '+33 6 98 76 54 32'),
    ('Bernard', 'Pierre', 'pierre.bernard@email.com', '+33 6 11 22 33 44'),
    ('Dubois', 'Marie', 'marie.dubois@email.com', '+33 6 55 66 77 88'),
    ('Moreau', 'Luc', 'luc.moreau@email.com', '+33 6 44 33 22 11'),
    ('Laurent', 'Isabelle', 'isabelle.laurent@email.com', '+33 6 77 88 99 00'),
    ('Simon', 'Thomas', 'thomas.simon@email.com', '+33 6 22 33 44 55'),
    ('Michel', 'Emma', 'emma.michel@email.com', '+33 6 66 77 88 99'),
    ('Lefebvre', 'Nicolas', 'nicolas.lefebvre@email.com', '+33 6 33 44 55 66'),
    ('Rousseau', 'Camille', 'camille.rousseau@email.com', '+33 6 88 99 00 11');

-- =====================================================
-- 15) RÉSERVATIONS
-- =====================================================
-- Réservation 1: Jean Dupont sur vol AF100 (Paris → New York)
INSERT INTO reservation (client_id, vol_id, date_reservation, statut) VALUES
    (1, 1, '2026-01-05 14:30:00', 'CONFIRMEE');

-- Réservation 2: Sophie Martin sur vol AF200 (Paris → Londres)
INSERT INTO reservation (client_id, vol_id, date_reservation, statut) VALUES
    (2, 3, '2026-01-06 10:15:00', 'CONFIRMEE');

-- Réservation 3: Pierre Bernard sur vol AF300 (Paris → Tokyo)
INSERT INTO reservation (client_id, vol_id, date_reservation, statut) VALUES
    (3, 5, '2026-01-07 16:45:00', 'CONFIRMEE');

-- Réservation 4: Marie Dubois sur vol AF100 (Paris → New York)
INSERT INTO reservation (client_id, vol_id, date_reservation, statut) VALUES
    (4, 1, '2026-01-08 09:20:00', 'CONFIRMEE');

-- Réservation 5: Luc Moreau sur vol AF400 (Paris → Dubaï)
INSERT INTO reservation (client_id, vol_id, date_reservation, statut) VALUES
    (5, 7, '2026-01-09 11:00:00', 'EN_ATTENTE');

-- =====================================================
-- 16) BILLETS
-- =====================================================
-- Note: Les billets doivent être créés via l'interface web
-- pour garantir l'intégrité (vérification siège disponible, etc.)
-- Mais voici des exemples si besoin de créer manuellement

-- =====================================================
-- 17) PAIEMENTS
-- =====================================================
-- Paiement pour réservation 1 (Jean Dupont)
INSERT INTO paiement (reservation_id, montant, date_paiement, statut) VALUES
    (1, 2500.00, '2026-01-05 14:35:00', 'VALIDE');

-- Répartition paiement multi-méthodes (Carte + Mobile Money)
INSERT INTO paiement_methode (paiement_id, methode_paiement_id, montant) VALUES
    (1, 1, 2000.00),   -- Carte: 2000€
    (1, 3, 500.00);    -- Mobile Money: 500€

-- Paiement pour réservation 2 (Sophie Martin)
INSERT INTO paiement (reservation_id, montant, date_paiement, statut) VALUES
    (2, 85.00, '2026-01-06 10:20:00', 'VALIDE');

INSERT INTO paiement_methode (paiement_id, methode_paiement_id, montant) VALUES
    (2, 1, 85.00);     -- Carte: 85€

-- Paiement pour réservation 3 (Pierre Bernard)
INSERT INTO paiement (reservation_id, montant, date_paiement, statut) VALUES
    (3, 4500.00, '2026-01-07 16:50:00', 'VALIDE');

INSERT INTO paiement_methode (paiement_id, methode_paiement_id, montant) VALUES
    (3, 4, 4500.00);   -- Virement: 4500€

-- =====================================================
-- VÉRIFICATIONS
-- =====================================================

-- Compter les entités créées
SELECT 'États avion' as Entité, COUNT(*) as Nombre FROM etat_avion
UNION ALL
SELECT 'Modèles avion', COUNT(*) FROM model_avion
UNION ALL
SELECT 'Classes siège', COUNT(*) FROM classe_siege
UNION ALL
SELECT 'Rôles équipage', COUNT(*) FROM role_equipage
UNION ALL
SELECT 'Méthodes paiement', COUNT(*) FROM methode_paiement
UNION ALL
SELECT 'Avions', COUNT(*) FROM avion
UNION ALL
SELECT 'Aéroports', COUNT(*) FROM aeroport
UNION ALL
SELECT 'Vols', COUNT(*) FROM vol
UNION ALL
SELECT 'Prix vols', COUNT(*) FROM prix_vol
UNION ALL
SELECT 'Membres équipage', COUNT(*) FROM equipage
UNION ALL
SELECT 'Affectations équipage', COUNT(*) FROM vol_equipage
UNION ALL
SELECT 'Clients', COUNT(*) FROM client
UNION ALL
SELECT 'Réservations', COUNT(*) FROM reservation
UNION ALL
SELECT 'Paiements', COUNT(*) FROM paiement
UNION ALL
SELECT 'Paiements méthodes', COUNT(*) FROM paiement_methode;

-- Voir tous les vols avec détails
SELECT 
    v.numero_vol,
    ad.code_aeroport || ' (' || ad.ville || ')' as depart,
    aa.code_aeroport || ' (' || aa.ville || ')' as arrivee,
    v.date_heure_depart,
    v.date_heure_arrivee,
    ma.designation as avion,
    v.status
FROM vol v
JOIN aeroport ad ON v.aeroport_depart_id = ad.id
JOIN aeroport aa ON v.aeroport_arrivee_id = aa.id
JOIN avion a ON v.avion_id = a.id
JOIN model_avion ma ON a.model_avion_id = ma.id
ORDER BY v.date_heure_depart;

-- Voir les prix par vol et classe
SELECT 
    v.numero_vol,
    cs.libelle as classe,
    pv.prix || ' €' as prix
FROM prix_vol pv
JOIN vol v ON pv.vol_id = v.id
JOIN classe_siege cs ON pv.classe_siege_id = cs.id
ORDER BY v.numero_vol, cs.libelle;

-- Voir l'équipage par vol
SELECT 
    v.numero_vol,
    e.nom || ' ' || e.prenom as membre,
    re.libelle as role
FROM vol_equipage ve
JOIN vol v ON ve.vol_id = v.id
JOIN equipage e ON ve.equipage_id = e.id
JOIN role_equipage re ON e.role_id = re.id
ORDER BY v.numero_vol, re.libelle;

-- Voir les réservations avec détails
SELECT 
    r.id,
    c.nom || ' ' || c.prenom as client,
    v.numero_vol,
    ad.ville || ' → ' || aa.ville as trajet,
    r.statut,
    COALESCE(p.montant, 0) || ' €' as montant_paye
FROM reservation r
JOIN client c ON r.client_id = c.id
JOIN vol v ON r.vol_id = v.id
JOIN aeroport ad ON v.aeroport_depart_id = ad.id
JOIN aeroport aa ON v.aeroport_arrivee_id = aa.id
LEFT JOIN paiement p ON p.reservation_id = r.id
ORDER BY r.date_reservation DESC;

-- =====================================================
-- FIN DU SCRIPT DE DONNÉES DE TEST
-- =====================================================
