-- =====================================================
-- DONNÉES DE TEST SIMPLIFIÉES - COMPATIBLE JPA
-- =====================================================
-- À exécuter APRÈS le premier démarrage de l'application
-- Les tables classe_siege, role_equipage, etat_avion et methode_paiement 
-- sont déjà initialisées par DataInitializer

-- =====================================================
-- 1) MODÈLES D'AVION
-- =====================================================
INSERT INTO model_avion (designation, fabricant, capacite, autonomie_km, vitesse_km_h, description, created_at, updated_at) VALUES
    ('Boeing 737-800', 'Boeing', 189, 5765, 842, 'Avion moyen-courrier le plus populaire au monde', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Airbus A320neo', 'Airbus', 180, 6300, 840, 'Moyen-courrier nouvelle génération', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Boeing 777-300ER', 'Boeing', 365, 14685, 905, 'Gros porteur long-courrier biréacteur', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Airbus A350-900', 'Airbus', 325, 15000, 903, 'Long-courrier moderne', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Boeing 787-9', 'Boeing', 296, 14010, 903, 'Dreamliner économe en carburant', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- =====================================================
-- 2) AÉROPORTS
-- =====================================================
INSERT INTO aeroport (code_aeroport, nom, ville, pays, created_at, updated_at) VALUES
    ('CDG', 'Charles de Gaulle', 'Paris', 'France', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('JFK', 'John F. Kennedy', 'New York', 'États-Unis', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('LHR', 'Heathrow', 'Londres', 'Royaume-Uni', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('DXB', 'Dubai International', 'Dubaï', 'Émirats arabes unis', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('NRT', 'Narita', 'Tokyo', 'Japon', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('SIN', 'Changi', 'Singapour', 'Singapour', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('SYD', 'Kingsford Smith', 'Sydney', 'Australie', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('LAX', 'Los Angeles International', 'Los Angeles', 'États-Unis', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- =====================================================
-- 3) AVIONS
-- =====================================================
-- Récupérer les IDs: model_avion_id, etat_avion_id (1=OPERATIONNEL)
INSERT INTO avion (code_avion, model_avion_id, etat_avion_id, capacite_totale, created_at, updated_at) VALUES
    ('AF-B737-001', 1, 1, 189, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('AF-A320-001', 2, 1, 180, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('AF-B777-001', 3, 1, 365, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('AF-A350-001', 4, 1, 325, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('AF-B787-001', 5, 1, 296, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- =====================================================
-- 4) CLIENTS
-- =====================================================
INSERT INTO client (nom, prenom, email, telephone, created_at, updated_at) VALUES
    ('Dupont', 'Jean', 'jean.dupont@email.com', '+33612345678', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Martin', 'Sophie', 'sophie.martin@email.com', '+33698765432', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Bernard', 'Pierre', 'pierre.bernard@email.com', '+33611223344', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Dubois', 'Marie', 'marie.dubois@email.com', '+33655667788', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Moreau', 'Luc', 'luc.moreau@email.com', '+33644332211', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- =====================================================
-- 5) ÉQUIPAGE
-- =====================================================
-- role_id: 1=PILOTE, 2=COPILOTE, 3=HOTESSE, 4=STEWARD
INSERT INTO equipage (nom, prenom, role_id, created_at, updated_at) VALUES
    ('Leclerc', 'Antoine', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Dubois', 'Jean-Pierre', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Moreau', 'Nicolas', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Laurent', 'Alexandre', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Petit', 'Sophie', 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Durand', 'Marie', 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Robert', 'Luc', 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Richard', 'Marc', 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- =====================================================
-- NOTE IMPORTANTE
-- =====================================================
-- Les VOLS, PRIX_VOL, SIÈGES et RÉSERVATIONS doivent être créés 
-- via l'interface web pour garantir :
-- - Génération automatique des sièges
-- - Création automatique des siege_vol
-- - Vérification des contraintes métier
-- - Initialisation correcte des relations

-- Pour créer un vol complet via l'interface :
-- 1. Aller sur /vols/new
-- 2. Remplir le formulaire (numéro, aéroports, dates, avion)
-- 3. Le système génère automatiquement les siege_vol
-- 4. Aller sur /prix-vol/new pour définir les prix par classe
-- 5. Créer une réservation via /reservations/new
-- 6. Créer des billets en sélectionnant des sièges

-- =====================================================
-- VÉRIFICATION DES DONNÉES
-- =====================================================
SELECT 'États avion' as entite, COUNT(*) as nombre FROM etat_avion
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
SELECT 'Clients', COUNT(*) FROM client
UNION ALL
SELECT 'Membres équipage', COUNT(*) FROM equipage;
