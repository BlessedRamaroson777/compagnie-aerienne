-- Donnees de demonstration pour la version actuelle de l'application.
-- Pre-requis : demarrer Spring Boot une premiere fois pour creer les tables.
-- Execution :
-- psql -U postgres -d airline_db -f data-test.sql

BEGIN;

-- Nettoyage complet, y compris les tables des modules avances.
TRUNCATE TABLE
    paiement_methode,
    paiement,
    billet,
    reservation_categorie_passager,
    reservation_detail,
    reservation,
    siege_vol,
    prix_vol,
    vol_programme_classe_prix,
    vol_equipage,
    vol_programme,
    vol,
    siege,
    avion_classe_configuration,
    avion,
    equipage,
    client,
    methode_paiement,
    categorie_passager,
    role_equipage,
    etat_avion,
    model_avion_classe_siege,
    classe_siege,
    aeroport,
    model_avion
RESTART IDENTITY CASCADE;

-- Referentiels
INSERT INTO classe_siege (libelle, description, created_at, updated_at) VALUES
    ('Economique', 'Classe standard', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Affaires', 'Classe premium', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Premiere', 'Classe luxe', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO etat_avion (libelle, description, created_at, updated_at) VALUES
    ('En service', 'Avion operationnel', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('En maintenance', 'Avion indisponible pour maintenance', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Au sol', 'Avion temporairement immobilise', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO role_equipage (libelle, description, created_at, updated_at) VALUES
    ('Pilote', 'Commandant de bord', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Copilote', 'Second pilote', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Hotesse', 'Personnel de cabine', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Steward', 'Personnel de cabine', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO methode_paiement (libelle) VALUES
    ('CARTE'), ('CASH'), ('MOBILE_MONEY'), ('VIREMENT');

INSERT INTO categorie_passager (libelle, description) VALUES
    ('Adulte', 'Passager adulte'),
    ('Enfant', 'Passager de moins de 12 ans'),
    ('Bebe', 'Passager de moins de 2 ans');

INSERT INTO model_avion
    (designation, fabricant, capacite, autonomie_km, vitesse_km_h, description, created_at, updated_at)
VALUES
    ('Boeing 737-800', 'Boeing', 189, 5765, 842, 'Moyen-courrier', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Airbus A320neo', 'Airbus', 180, 6300, 840, 'Moyen-courrier nouvelle generation', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Airbus A350-900', 'Airbus', 325, 15000, 903, 'Long-courrier moderne', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO aeroport
    (code_aeroport, nom, ville, pays, created_at, updated_at)
VALUES
    ('TNR', 'Ivato', 'Antananarivo', 'Madagascar', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('CDG', 'Charles de Gaulle', 'Paris', 'France', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('JFK', 'John F. Kennedy', 'New York', 'Etats-Unis', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('DXB', 'Dubai International', 'Dubai', 'Emirats Arabes Unis', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('NRT', 'Narita', 'Tokyo', 'Japon', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Avions
INSERT INTO avion
    (code_avion, model_avion_id, etat_avion_id, capacite_totale, created_at, updated_at)
VALUES
    ('AF-001', 1, 1, 189, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('AF-002', 2, 1, 180, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('AF-003', 3, 2, 325, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 70 % de sieges Economique et 30 % Affaires, pour chaque avion.
INSERT INTO siege (numero_siege, classe_siege_id, avion_id, created_at, updated_at)
SELECT
    CONCAT(CHR(65 + ((s.numero - 1) / 6)::integer), ((s.numero - 1) % 6) + 1),
    CASE
        WHEN s.numero <= CEIL(a.capacite_totale * 0.70) THEN 1
        ELSE 2
    END,
    a.id,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
FROM avion a
CROSS JOIN LATERAL generate_series(1, a.capacite_totale) AS s(numero);

-- Vols commerciaux et vols programmes.
INSERT INTO vol
    (numero_vol, aeroport_depart_id, aeroport_arrivee_id, duree_vol_minutes)
VALUES
    ('AF100', 1, 2, 660),
    ('AF200', 2, 3, 450),
    ('AF300', 2, 4, 540),
    ('AF400', 4, 5, 540);

INSERT INTO vol_programme
    (vol_id, avion_id, date_heure_depart, date_heure_arrivee, status)
VALUES
    (1, 1, '2026-12-10 08:00:00', '2026-12-10 19:00:00', 'PLANIFIE'),
    (2, 2, '2026-12-12 10:00:00', '2026-12-12 17:30:00', 'PLANIFIE'),
    (3, 3, '2026-12-15 21:00:00', '2026-12-16 06:00:00', 'PLANIFIE'),
    (4, 3, '2026-12-20 09:00:00', '2026-12-20 18:00:00', 'PLANIFIE');

INSERT INTO vol_programme_classe_prix (vol_programme_id, classe_siege_id, prix) VALUES
    (1, 1, 850.00), (1, 2, 1800.00),
    (2, 1, 620.00), (2, 2, 1450.00),
    (3, 1, 980.00), (3, 2, 2300.00),
    (4, 1, 1100.00), (4, 2, 2600.00);

-- Disponibilite initiale des sieges pour chaque vol programme.
INSERT INTO siege_vol (vol_programme_id, siege_id, occupe)
SELECT vp.id, s.id, false
FROM vol_programme vp
JOIN siege s ON s.avion_id = vp.avion_id;

-- Clients et equipages.
INSERT INTO client (nom, prenom, email, telephone) VALUES
    ('Rakoto', 'Aina', 'aina.rakoto@example.com', '+261340000001'),
    ('Martin', 'Sophie', 'sophie.martin@example.com', '+33600000002'),
    ('Dupont', 'Jean', 'jean.dupont@example.com', '+33600000003');

INSERT INTO equipage (nom, prenom, role_id) VALUES
    ('Rasoanaivo', 'Hery', 1),
    ('Moreau', 'Julie', 2),
    ('Randria', 'Lova', 3),
    ('Petit', 'Marc', 4);

INSERT INTO vol_equipage (vol_programme_id, equipage_id) VALUES
    (1, 1), (1, 2), (1, 3), (1, 4),
    (2, 1), (2, 2), (2, 3), (2, 4);

-- Deux reservations pour alimenter les ecrans reservation, billet et paiement.
INSERT INTO reservation
    (client_id, vol_programme_id, classe_siege_id, nombre_places, prix_total, statut)
VALUES
    (1, 1, 1, 2, 1700.00, 'CONFIRMEE'),
    (2, 2, 2, 1, 1450.00, 'EN_ATTENTE');

INSERT INTO reservation_detail
    (reservation_id, classe_siege_id, nombre_places, prix_unitaire, sous_total)
VALUES
    (1, 1, 2, 850.00, 1700.00),
    (2, 2, 1, 1450.00, 1450.00);

-- Emission de deux billets sur deux sieges du premier vol.
WITH sieges_a_reserver AS (
    SELECT sv.id, ROW_NUMBER() OVER (ORDER BY s.id) AS rang
    FROM siege_vol sv
    JOIN siege s ON s.id = sv.siege_id
    WHERE sv.vol_programme_id = 1
      AND s.classe_siege_id = 1
    LIMIT 2
)
INSERT INTO billet
    (reservation_id, siege_vol_id, prix, prix_base, remise, statut)
SELECT 1, id, 850.00, 850.00, 0.00, 'EMIS'
FROM sieges_a_reserver;

UPDATE siege_vol
SET occupe = true
WHERE id IN (
    SELECT siege_vol_id
    FROM billet
    WHERE reservation_id = 1
);

INSERT INTO paiement (reservation_id, montant, date_paiement, statut)
VALUES (1, 1700.00, CURRENT_TIMESTAMP, 'VALIDE');

INSERT INTO paiement_methode (paiement_id, methode_paiement_id, montant)
VALUES (1, 1, 1200.00), (1, 3, 500.00);

COMMIT;

-- Verification rapide.
SELECT 'Avions' AS element, COUNT(*) AS total FROM avion
UNION ALL SELECT 'Sieges', COUNT(*) FROM siege
UNION ALL SELECT 'Vols programmes', COUNT(*) FROM vol_programme
UNION ALL SELECT 'Sieges de vols', COUNT(*) FROM siege_vol
UNION ALL SELECT 'Clients', COUNT(*) FROM client
UNION ALL SELECT 'Reservations', COUNT(*) FROM reservation
UNION ALL SELECT 'Billets', COUNT(*) FROM billet
UNION ALL SELECT 'Paiements', COUNT(*) FROM paiement;
