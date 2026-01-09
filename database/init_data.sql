-- =====================================================
-- DONNÉES DE TEST - NETTOYAGE ET INSERTION
-- =====================================================

-- Supprimer les données existantes (dans l'ordre inverse des dépendances)
DELETE FROM paiement_methode;
DELETE FROM paiement;
DELETE FROM billet;
DELETE FROM reservation;
DELETE FROM vol_equipage;
DELETE FROM equipage;
DELETE FROM siege_vol;
DELETE FROM prix_vol;
DELETE FROM vol;
DELETE FROM siege;
DELETE FROM client;
DELETE FROM avion;
DELETE FROM aeroport;
DELETE FROM model_avion;

-- Les tables classe_siege, role_equipage, etat_avion sont initialisées par DataInitializer

-- =====================================================
-- 1) MODÈLES D'AVION
-- =====================================================
INSERT INTO model_avion (designation, fabricant, capacite, autonomie_km, vitesse_km_h, description, created_at, updated_at) VALUES
    ('Boeing 737-800', 'Boeing', 189, 5765, 842, 'Avion moyen-courrier populaire', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Airbus A320neo', 'Airbus', 180, 6300, 840, 'Moyen-courrier nouvelle génération', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Boeing 777-300ER', 'Boeing', 365, 14685, 905, 'Gros porteur long-courrier', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Airbus A350-900', 'Airbus', 325, 15000, 903, 'Long-courrier moderne', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Boeing 787-9', 'Boeing', 296, 14010, 903, 'Dreamliner économe', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- =====================================================
-- 2) AÉROPORTS
-- =====================================================
INSERT INTO aeroport (code_aeroport, nom, ville, pays, created_at, updated_at) VALUES
    ('CDG', 'Charles de Gaulle', 'Paris', 'France', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('JFK', 'John F. Kennedy', 'New York', 'USA', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('LHR', 'Heathrow', 'Londres', 'UK', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('DXB', 'Dubai International', 'Dubaï', 'UAE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('NRT', 'Narita', 'Tokyo', 'Japon', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('LAX', 'Los Angeles', 'Los Angeles', 'USA', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('SYD', 'Kingsford Smith', 'Sydney', 'Australie', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- =====================================================
-- 3) CLIENTS
-- =====================================================
INSERT INTO client (nom, prenom, email, telephone) VALUES
    ('Dupont', 'Jean', 'jean.dupont@email.com', '+33612345678'),
    ('Martin', 'Sophie', 'sophie.martin@email.com', '+33698765432'),
    ('Bernard', 'Pierre', 'pierre.bernard@email.com', '+33611223344'),
    ('Dubois', 'Marie', 'marie.dubois@email.com', '+33655667788'),
    ('Moreau', 'Luc', 'luc.moreau@email.com', '+33644332211');

-- =====================================================
-- 4) ÉQUIPAGE
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
-- VÉRIFICATION
-- =====================================================
SELECT 'États avion' as entite, COUNT(*) as nombre FROM etat_avion
UNION ALL SELECT 'Modèles avion', COUNT(*) FROM model_avion
UNION ALL SELECT 'Classes siège', COUNT(*) FROM classe_siege
UNION ALL SELECT 'Rôles équipage', COUNT(*) FROM role_equipage
UNION ALL SELECT 'Méthodes paiement', COUNT(*) FROM methode_paiement
UNION ALL SELECT 'Aéroports', COUNT(*) FROM aeroport
UNION ALL SELECT 'Clients', COUNT(*) FROM client
UNION ALL SELECT 'Membres équipage', COUNT(*) FROM equipage;

-- =====================================================
-- NOTE : CRÉER LES AVIONS VIA L'INTERFACE WEB
-- =====================================================
-- Les avions DOIVENT être créés via /avions/new pour que :
-- - Les sièges soient générés automatiquement
-- - Les relations modelAvion et etatAvion soient correctes
--
-- Étapes :
-- 1. Aller sur http://localhost:8080/avions/new
-- 2. Sélectionner un modèle (ex: Boeing 737-800)
-- 3. Sélectionner un état (OPERATIONNEL)
-- 4. Le système génère automatiquement 189 sièges
