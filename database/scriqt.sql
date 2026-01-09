-- =====================================================
-- 1) TYPES ENUM
-- =====================================================

CREATE TYPE statut_reservation_enum AS ENUM ('EN_ATTENTE', 'CONFIRMEE', 'ANNULEA','REMBOURSEE');
CREATE TYPE statut_billet_enum AS ENUM ('EMIS', 'ANNULE','UTILISE','REMBOURSE');
CREATE TYPE statut_paiement_enum AS ENUM ('EN_ATTENTE', 'VALIDE', 'REFUSE');
CREATE TYPE status_vol_enum AS ENUM ('PLANIFIE', 'DECOLLE', 'EN_VOL', 'ATTERRI', 'ANNULE', 'RETARDE');

-- =====================================================
-- 2) TABLE CLASSE_SIEGE
-- =====================================================

CREATE TABLE classe_siege (
    id SERIAL PRIMARY KEY,
    libelle VARCHAR(50) NOT NULL UNIQUE,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =====================================================
-- 3) TABLE ROLE_EQUIPAGE
-- =====================================================

CREATE TABLE role_equipage (
    id SERIAL PRIMARY KEY,
    libelle VARCHAR(50) NOT NULL UNIQUE,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =====================================================
-- 4) TABLE MODEL_AVION
-- =====================================================

CREATE TABLE model_avion (
    id SERIAL PRIMARY KEY,
    designation VARCHAR(100) NOT NULL UNIQUE,
    fabricant VARCHAR(100) NOT NULL,
    capacite INT NOT NULL CHECK (capacite > 0),
    autonomie_km INT,
    vitesse_km_h INT,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =====================================================
-- 5) TABLE ETAT_AVION
-- =====================================================

CREATE TABLE etat_avion (
    id SERIAL PRIMARY KEY,
    libelle VARCHAR(50) NOT NULL UNIQUE,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =====================================================
-- 6) TABLE AVION
-- =====================================================

CREATE TABLE avion (
    id SERIAL PRIMARY KEY,
    code_avion VARCHAR(50) NOT NULL UNIQUE,
    model_avion_id INT NOT NULL,
    etat_avion_id INT NOT NULL,
    capacite_totale INT NOT NULL CHECK (capacite_totale > 0),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (model_avion_id) REFERENCES model_avion(id) ON DELETE RESTRICT,
    FOREIGN KEY (etat_avion_id) REFERENCES etat_avion(id) ON DELETE RESTRICT
);

-- =====================================================
-- 7) TABLE SIEGE
-- =====================================================

CREATE TABLE siege (
    id SERIAL PRIMARY KEY,
    numero_siege VARCHAR(10) NOT NULL,
    classe_siege_id INT NOT NULL,
    avion_id INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (classe_siege_id) REFERENCES classe_siege(id) ON DELETE RESTRICT,
    FOREIGN KEY (avion_id) REFERENCES avion(id) ON DELETE CASCADE,
    UNIQUE (avion_id, numero_siege)
);

-- =====================================================
-- 😎 TABLE EQUIPAGE
-- =====================================================

CREATE TABLE equipage (
    id SERIAL PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    role_id INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (role_id) REFERENCES role_equipage(id) ON DELETE RESTRICT
);

-- =====================================================
-- 9) TABLE AEROPORT
-- =====================================================

CREATE TABLE aeroport (
    id SERIAL PRIMARY KEY,
    code_aeroport VARCHAR(10) NOT NULL UNIQUE,
    nom VARCHAR(150) NOT NULL,
    ville VARCHAR(100) NOT NULL,
    pays VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =====================================================
-- 10) TABLE VOL
-- =====================================================

CREATE TABLE vol (
    id SERIAL PRIMARY KEY,
    numero_vol VARCHAR(50) NOT NULL UNIQUE,
    aeroport_depart_id INT NOT NULL,
    aeroport_arrivee_id INT NOT NULL,
    date_heure_depart TIMESTAMP NOT NULL,
    date_heure_arrivee TIMESTAMP NOT NULL,
    avion_id INT NOT NULL,
    status status_vol_enum DEFAULT 'PLANIFIE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (aeroport_depart_id) REFERENCES aeroport(id) ON DELETE RESTRICT,
    FOREIGN KEY (aeroport_arrivee_id) REFERENCES aeroport(id) ON DELETE RESTRICT,
    FOREIGN KEY (avion_id) REFERENCES avion(id) ON DELETE RESTRICT,
    CHECK (date_heure_depart < date_heure_arrivee),
    CHECK (aeroport_depart_id != aeroport_arrivee_id)
);

-- =====================================================
-- 11) TABLE PRIX_VOL
-- =====================================================

CREATE TABLE prix_vol (
    id SERIAL PRIMARY KEY,
    vol_id INT NOT NULL,
    classe_siege_id INT NOT NULL,
    prix DECIMAL(10, 2) NOT NULL CHECK (prix >= 0),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (vol_id) REFERENCES vol(id) ON DELETE CASCADE,
    FOREIGN KEY (classe_siege_id) REFERENCES classe_siege(id) ON DELETE RESTRICT,
    UNIQUE (vol_id, classe_siege_id)
);

-- =====================================================
-- 12) TABLE SIEGE_VOL
-- =====================================================

CREATE TABLE siege_vol (
    id SERIAL PRIMARY KEY,
    vol_id INT NOT NULL,
    siege_id INT NOT NULL,
    occupe BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (vol_id) REFERENCES vol(id) ON DELETE CASCADE,
    FOREIGN KEY (siege_id) REFERENCES siege(id) ON DELETE CASCADE,
    UNIQUE (vol_id, siege_id)
);

-- =====================================================
-- 13) TABLE VOL_EQUIPAGE
-- =====================================================

CREATE TABLE vol_equipage (
    id SERIAL PRIMARY KEY,
    vol_id INT NOT NULL,
    equipage_id INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (vol_id) REFERENCES vol(id) ON DELETE CASCADE,
    FOREIGN KEY (equipage_id) REFERENCES equipage(id) ON DELETE CASCADE,
    UNIQUE (vol_id, equipage_id)
);

-- =====================================================
-- 14) TABLE CLIENT
-- =====================================================

CREATE TABLE client (
    id SERIAL PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    telephone VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =====================================================
-- 15) TABLE RESERVATION
-- =====================================================

CREATE TABLE reservation (
    id SERIAL PRIMARY KEY,
    client_id INT NOT NULL,
    vol_id INT NOT NULL,
    date_reservation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    statut statut_reservation_enum DEFAULT 'CONFIRMEE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (client_id) REFERENCES client(id) ON DELETE RESTRICT,
    FOREIGN KEY (vol_id) REFERENCES vol(id) ON DELETE RESTRICT
);

-- =====================================================
-- 16) TABLE BILLET
-- =====================================================

CREATE TABLE billet (
    id SERIAL PRIMARY KEY,
    reservation_id INT NOT NULL,
    siege_vol_id INT NOT NULL,
    prix DECIMAL(10, 2) NOT NULL CHECK (prix >= 0),
    statut statut_billet_enum DEFAULT 'EMIS',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (reservation_id) REFERENCES reservation(id) ON DELETE CASCADE,
    FOREIGN KEY (siege_vol_id) REFERENCES siege_vol(id) ON DELETE RESTRICT
);

-- =====================================================
-- 17) TABLE METHODE_PAIEMENT
-- =====================================================

CREATE TABLE methode_paiement (
    id SERIAL PRIMARY KEY,
    libelle VARCHAR(50) NOT NULL UNIQUE,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =====================================================
-- 18) TABLE PAIEMENT
-- =====================================================

CREATE TABLE paiement (
    id SERIAL PRIMARY KEY,
    reservation_id INT NOT NULL,
    montant DECIMAL(10, 2) NOT NULL CHECK (montant > 0),
    date_paiement TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    statut statut_paiement_enum DEFAULT 'EN_ATTENTE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (reservation_id) REFERENCES reservation(id) ON DELETE CASCADE
);

-- =====================================================
-- 19) TABLE PAIEMENT_METHODE
-- =====================================================

CREATE TABLE paiement_methode (
    id SERIAL PRIMARY KEY,
    paiement_id INT NOT NULL,
    methode_paiement_id INT NOT NULL,
    montant DECIMAL(10, 2) NOT NULL CHECK (montant > 0),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (paiement_id) REFERENCES paiement(id) ON DELETE CASCADE,
    FOREIGN KEY (methode_paiement_id) REFERENCES methode_paiement(id) ON DELETE RESTRICT,
    UNIQUE (paiement_id, methode_paiement_id)
);

-- =====================================================
-- 20) INDEXES
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
-- 21) DONNEES PAR DEFAUT
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