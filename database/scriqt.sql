-- =====================================================
-- 1) TYPES ENUM
-- =====================================================

CREATE TYPE statut_reservation_enum AS ENUM ('EN_ATTENTE', 'CONFIRMEE', 'ANNULEE', 'REMBOURSEE');
CREATE TYPE statut_billet_enum AS ENUM ('EMIS', 'ANNULE', 'UTILISE', 'REMBOURSE');
CREATE TYPE statut_paiement_enum AS ENUM ('EN_ATTENTE', 'VALIDE', 'REFUSE');
CREATE TYPE status_vol_enum AS ENUM ('PLANIFIE', 'DECOLLE', 'EN_VOL', 'ATTERRI', 'ANNULE', 'RETARDE');

-- =====================================================
-- 2) CLASSE_SIEGE
-- =====================================================

CREATE TABLE classe_siege (
    id SERIAL PRIMARY KEY,
    libelle VARCHAR(50) UNIQUE NOT NULL,
    description TEXT
);

-- =====================================================
-- 3) ROLE_EQUIPAGE
-- =====================================================

CREATE TABLE role_equipage (
    id SERIAL PRIMARY KEY,
    libelle VARCHAR(50) UNIQUE NOT NULL
);

-- =====================================================
-- 4) MODEL_AVION
-- =====================================================

CREATE TABLE model_avion (
    id SERIAL PRIMARY KEY,
    designation VARCHAR(100) UNIQUE NOT NULL,
    fabricant VARCHAR(100) NOT NULL,
    capacite INT NOT NULL CHECK (capacite > 0)
);

-- =====================================================
-- 5) ETAT_AVION
-- =====================================================

CREATE TABLE etat_avion (
    id SERIAL PRIMARY KEY,
    libelle VARCHAR(50) UNIQUE NOT NULL
);

-- =====================================================
-- 6) AVION
-- =====================================================

CREATE TABLE avion (
    id SERIAL PRIMARY KEY,
    code_avion VARCHAR(50) UNIQUE NOT NULL,
    model_avion_id INT NOT NULL,
    etat_avion_id INT NOT NULL,
    capacite_totale INT NOT NULL,
    FOREIGN KEY (model_avion_id) REFERENCES model_avion(id),
    FOREIGN KEY (etat_avion_id) REFERENCES etat_avion(id)
);

-- =====================================================
-- 7) SIEGE
-- =====================================================

CREATE TABLE siege (
    id SERIAL PRIMARY KEY,
    numero_siege VARCHAR(10) NOT NULL,
    avion_id INT NOT NULL,
    classe_siege_id INT NOT NULL,
    FOREIGN KEY (avion_id) REFERENCES avion(id) ON DELETE CASCADE,
    FOREIGN KEY (classe_siege_id) REFERENCES classe_siege(id),
    UNIQUE (avion_id, numero_siege)
);

-- =====================================================
-- 8) EQUIPAGE
-- =====================================================

CREATE TABLE equipage (
    id SERIAL PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    role_id INT NOT NULL,
    FOREIGN KEY (role_id) REFERENCES role_equipage(id)
);

-- =====================================================
-- 9) AEROPORT
-- =====================================================

CREATE TABLE aeroport (
    id SERIAL PRIMARY KEY,
    code_aeroport VARCHAR(10) UNIQUE NOT NULL,
    nom VARCHAR(150) NOT NULL,
    ville VARCHAR(100) NOT NULL,
    pays VARCHAR(100) NOT NULL
);

-- =====================================================
-- 10) VOL (TRAJET)
-- =====================================================

CREATE TABLE vol (
    id SERIAL PRIMARY KEY,
    numero_vol VARCHAR(50) NOT NULL,
    aeroport_depart_id INT NOT NULL,
    aeroport_arrivee_id INT NOT NULL,
    FOREIGN KEY (aeroport_depart_id) REFERENCES aeroport(id),
    FOREIGN KEY (aeroport_arrivee_id) REFERENCES aeroport(id),
    CHECK (aeroport_depart_id <> aeroport_arrivee_id),
    UNIQUE (numero_vol, aeroport_depart_id, aeroport_arrivee_id)
);

-- =====================================================
-- 11) VOL_PROGRAMME (DATE + HEURE + AVION)
-- =====================================================

CREATE TABLE vol_programme (
    id SERIAL PRIMARY KEY,
    vol_id INT NOT NULL,
    avion_id INT NOT NULL,
    date_heure_depart TIMESTAMP NOT NULL,
    date_heure_arrivee TIMESTAMP NOT NULL,
    status status_vol_enum DEFAULT 'PLANIFIE',
    FOREIGN KEY (vol_id) REFERENCES vol(id) ON DELETE CASCADE,
    FOREIGN KEY (avion_id) REFERENCES avion(id),
    CHECK (date_heure_depart < date_heure_arrivee)
);

-- =====================================================
-- 12) PRIX_VOL
-- =====================================================

CREATE TABLE prix_vol (
    id SERIAL PRIMARY KEY,
    vol_programme_id INT NOT NULL,
    classe_siege_id INT NOT NULL,
    prix DECIMAL(10,2) NOT NULL CHECK (prix >= 0),
    FOREIGN KEY (vol_programme_id) REFERENCES vol_programme(id) ON DELETE CASCADE,
    FOREIGN KEY (classe_siege_id) REFERENCES classe_siege(id),
    UNIQUE (vol_programme_id, classe_siege_id)
);

-- =====================================================
-- 13) SIEGE_VOL
-- =====================================================

CREATE TABLE siege_vol (
    id SERIAL PRIMARY KEY,
    vol_programme_id INT NOT NULL,
    siege_id INT NOT NULL,
    occupe BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (vol_programme_id) REFERENCES vol_programme(id) ON DELETE CASCADE,
    FOREIGN KEY (siege_id) REFERENCES siege(id),
    UNIQUE (vol_programme_id, siege_id)
);

-- =====================================================
-- 14) VOL_EQUIPAGE
-- =====================================================

CREATE TABLE vol_equipage (
    id SERIAL PRIMARY KEY,
    vol_programme_id INT NOT NULL,
    equipage_id INT NOT NULL,
    FOREIGN KEY (vol_programme_id) REFERENCES vol_programme(id) ON DELETE CASCADE,
    FOREIGN KEY (equipage_id) REFERENCES equipage(id),
    UNIQUE (vol_programme_id, equipage_id)
);

-- =====================================================
-- 15) CLIENT
-- =====================================================

CREATE TABLE client (
    id SERIAL PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL
);

-- =====================================================
-- 16) RESERVATION
-- =====================================================

CREATE TABLE reservation (
    id SERIAL PRIMARY KEY,
    client_id INT NOT NULL,
    vol_programme_id INT NOT NULL,
    classe_siege_id INT NOT NULL, 
    nombre_places INT NOT NULL CHECK (nombre_places > 0),
    prix_total DECIMAL(10,2),
    statut statut_reservation_enum DEFAULT 'EN_ATTENTE',
    FOREIGN KEY (client_id) REFERENCES client(id),
    FOREIGN KEY (vol_programme_id) REFERENCES vol_programme(id),
    FOREIGN KEY (classe_siege_id) REFERENCES classe_siege(id)
);

-- =====================================================
-- 17) BILLET
-- =====================================================

CREATE TABLE billet (
    id SERIAL PRIMARY KEY,
    reservation_id INT NOT NULL,
    siege_vol_id INT NOT NULL,
    prix DECIMAL(10,2) NOT NULL,
    statut statut_billet_enum DEFAULT 'EMIS',
    FOREIGN KEY (reservation_id) REFERENCES reservation(id) ON DELETE CASCADE,
    FOREIGN KEY (siege_vol_id) REFERENCES siege_vol(id)
);

-- =====================================================
-- 18) METHODE_PAIEMENT
-- =====================================================

CREATE TABLE methode_paiement (
    id SERIAL PRIMARY KEY,
    libelle VARCHAR(50) UNIQUE NOT NULL
);

-- =====================================================
-- 19) PAIEMENT
-- =====================================================

CREATE TABLE paiement (
    id SERIAL PRIMARY KEY,
    reservation_id INT NOT NULL,
    montant DECIMAL(10,2) NOT NULL CHECK (montant > 0),
    statut statut_paiement_enum DEFAULT 'EN_ATTENTE',
    FOREIGN KEY (reservation_id) REFERENCES reservation(id) ON DELETE CASCADE
);

-- =====================================================
-- 20) PAIEMENT_METHODE
-- =====================================================

CREATE TABLE paiement_methode (
    id SERIAL PRIMARY KEY,
    paiement_id INT NOT NULL,
    methode_paiement_id INT NOT NULL,
    montant DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (paiement_id) REFERENCES paiement(id),
    FOREIGN KEY (methode_paiement_id) REFERENCES methode_paiement(id),
    UNIQUE (paiement_id, methode_paiement_id)
);

-- =====================================================
-- 21) INDEXES
-- =====================================================

-- Index sur les recherches fréquentes
CREATE INDEX idx_model_avion_designation ON model_avion(designation);
CREATE INDEX idx_etat_avion_libelle ON etat_avion(libelle);
CREATE INDEX idx_avion_code ON avion(code_avion);
CREATE INDEX idx_avion_model ON avion(model_avion_id);
CREATE INDEX idx_avion_etat ON avion(etat_avion_id);
CREATE INDEX idx_aeroport_code ON aeroport(code_aeroport);
CREATE INDEX idx_siege_avion ON siege(avion_id);
CREATE INDEX idx_siege_classe ON siege(classe_siege_id);
CREATE INDEX idx_equipage_role ON equipage(role_id);
CREATE INDEX idx_vol_avion ON vol(avion_id);
CREATE INDEX idx_vol_depart ON vol(aeroport_depart_id);
CREATE INDEX idx_vol_arrivee ON vol(aeroport_arrivee_id);
CREATE INDEX idx_vol_date ON vol(date_heure_depart);
CREATE INDEX idx_prixvol_vol ON prix_vol(vol_id);
CREATE INDEX idx_prixvol_classe ON prix_vol(classe_siege_id);
CREATE INDEX idx_siegevol_vol ON siege_vol(vol_id);
CREATE INDEX idx_siegevol_siege ON siege_vol(siege_id);
CREATE INDEX idx_client_email ON client(email);
CREATE INDEX idx_client_nom_prenom ON client(nom, prenom);
CREATE INDEX idx_reservation_client ON reservation(client_id);
CREATE INDEX idx_reservation_vol ON reservation(vol_id);
CREATE INDEX idx_reservation_statut ON reservation(statut);
CREATE INDEX idx_billet_reservation ON billet(reservation_id);
CREATE INDEX idx_billet_siegevol ON billet(siege_vol_id);
CREATE INDEX idx_billet_statut ON billet(statut);
CREATE INDEX idx_paiement_reservation ON paiement(reservation_id);
CREATE INDEX idx_paiement_statut ON paiement(statut);
CREATE INDEX idx_paiement_methode_paiement ON paiement_methode(paiement_id);
CREATE INDEX idx_voléquipage_vol ON vol_equipage(vol_id);

-- =====================================================
-- 22) DONNEES PAR DEFAUT
-- =====================================================

-- Insertion des états d'avion
INSERT INTO etat_avion (libelle, description) VALUES
    ('OPERATIONNEL', 'Avion opérationnel et prêt au vol'),
    ('EN_MAINTENANCE', 'Avion en maintenance'),
    ('IMMOBILISE', 'Avion immobilisé'),
    ('DECOMMISSIONNE', 'Avion décommissionné');

-- Insertion des modèles d'avion
INSERT INTO model_avion (designation, fabricant, capacite, autonomie_km, vitesse_km_h, description) VALUES
    ('Boeing 747-8', 'Boeing', 416, 14685, 908, 'Gros porteur long-courrier'),
    ('Airbus A380', 'Airbus', 555, 15000, 903, 'Double pont long-courrier'),
    ('Boeing 777-300ER', 'Boeing', 365, 14685, 905, 'Gros porteur long-courrier biréacteur'),
    ('Airbus A350-900', 'Airbus', 325, 15000, 903, 'Long-courrier biréacteur'),
    ('Boeing 787-9', 'Boeing', 296, 14010, 903, 'Moyen-courrier long-distance');

-- Insertion des classes de siège
INSERT INTO classe_siege (libelle, description) VALUES 
    ('ECONOMY', 'Classe Économique'),
    ('BUSINESS', 'Classe Affaires');

-- Insertion des rôles d'équipage
INSERT INTO role_equipage (libelle, description) VALUES 
    ('PILOTE', 'Pilote'),
    ('COPILOTE', 'Copilote'),
    ('HOTESSE', 'Hôtesse de l''air'),
    ('STEWARD', 'Steward');

-- Insertion des méthodes de paiement
INSERT INTO methode_paiement (libelle, description) VALUES 
    ('CARTE', 'Carte bancaire'),
    ('CASH', 'Espèces'),
    ('MOBILE_MONEY', 'Mobile Money'),
    ('VIREMENT', 'Virement bancaire');

-- =====================================================
-- FIN DU SCRIPT
-- =====================================================