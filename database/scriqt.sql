-- =====================================================
-- 1) TYPES ENUM
-- =====================================================

CREATE TYPE statut_reservation_enum AS ENUM ('EN_ATTENTE', 'CONFIRMEE', 'ANNULEE', 'REMBOURSEE');
CREATE TYPE statut_billet_enum AS ENUM ('EMIS', 'ANNULE', 'UTILISE', 'REMBOURSE');
CREATE TYPE statut_paiement_enum AS ENUM ('EN_ATTENTE', 'VALIDE', 'REFUSE');
CREATE TYPE status_vol_enum AS ENUM ('PLANIFIE', 'DECOLLE', 'EN_VOL', 'ATTERRI', 'ANNULE', 'RETARDE');
CREATE TYPE type_methode_paiement_enum AS ENUM ('ESPECES', 'CARTE_BANCAIRE', 'VIREMENT', 'MOBILE_MONEY', 'CHEQUE');

-- =====================================================
-- 2) CLASSE_SIEGE
-- =====================================================

CREATE TABLE classe_siege (
    id SERIAL PRIMARY KEY,
    libelle VARCHAR(50) UNIQUE NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =====================================================
-- 3) ROLE_EQUIPAGE
-- =====================================================

CREATE TABLE role_equipage (
    id SERIAL PRIMARY KEY,
    libelle VARCHAR(50) UNIQUE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =====================================================
-- 4) MODEL_AVION avec classes disponibles
-- =====================================================

CREATE TABLE model_avion (
    id SERIAL PRIMARY KEY,
    designation VARCHAR(100) UNIQUE NOT NULL,
    fabricant VARCHAR(100) NOT NULL,
    capacite INT,
    autonomie_km INT,
    vitesse_km_h INT,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table de liaison pour les classes disponibles par modèle
CREATE TABLE model_avion_classe_siege (
    model_avion_id INT NOT NULL,
    classe_siege_id INT NOT NULL,
    PRIMARY KEY (model_avion_id, classe_siege_id),
    FOREIGN KEY (model_avion_id) REFERENCES model_avion(id) ON DELETE CASCADE,
    FOREIGN KEY (classe_siege_id) REFERENCES classe_siege(id) ON DELETE CASCADE
);

-- =====================================================
-- 5) ETAT_AVION
-- =====================================================

CREATE TABLE etat_avion (
    id SERIAL PRIMARY KEY,
    libelle VARCHAR(50) UNIQUE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =====================================================
-- 6) AVION
-- =====================================================

CREATE TABLE avion (
    id SERIAL PRIMARY KEY,
    code_avion VARCHAR(50) UNIQUE NOT NULL,
    model_avion_id INT NOT NULL,
    etat_avion_id INT NOT NULL,
    capacite_totale INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
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
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
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
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
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
    pays VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =====================================================
-- 10) VOL (TRAJET)
-- =====================================================

CREATE TABLE vol (
    id SERIAL PRIMARY KEY,
    numero_vol VARCHAR(50) NOT NULL,
    aeroport_depart_id INT NOT NULL,
    aeroport_arrivee_id INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
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
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
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
    email VARCHAR(100) UNIQUE NOT NULL,
    telephone VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =====================================================
-- 16) RESERVATION (avec support multi-classe)
-- =====================================================

CREATE TABLE reservation (
    id SERIAL PRIMARY KEY,
    client_id INT NOT NULL,
    vol_programme_id INT NOT NULL,
    classe_siege_id INT NOT NULL, -- Classe principale pour compatibilité
    nombre_places INT NOT NULL CHECK (nombre_places > 0),
    prix_total DECIMAL(10,2),
    statut statut_reservation_enum DEFAULT 'EN_ATTENTE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (client_id) REFERENCES client(id),
    FOREIGN KEY (vol_programme_id) REFERENCES vol_programme(id),
    FOREIGN KEY (classe_siege_id) REFERENCES classe_siege(id)
);

-- =====================================================
-- 16b) RESERVATION_DETAIL (détails multi-classe)
-- =====================================================

CREATE TABLE reservation_detail (
    id SERIAL PRIMARY KEY,
    reservation_id INT NOT NULL,
    classe_siege_id INT NOT NULL,
    nombre_places INT NOT NULL CHECK (nombre_places > 0),
    prix_unitaire DECIMAL(10,2) NOT NULL,
    sous_total DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (reservation_id) REFERENCES reservation(id) ON DELETE CASCADE,
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
    libelle type_methode_paiement_enum UNIQUE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =====================================================
-- 19) PAIEMENT (avec date automatique)
-- =====================================================

CREATE TABLE paiement (
    id SERIAL PRIMARY KEY,
    reservation_id INT NOT NULL,
    montant DECIMAL(10,2) NOT NULL CHECK (montant > 0),
    date_paiement TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    statut statut_paiement_enum DEFAULT 'EN_ATTENTE',
    FOREIGN KEY (reservation_id) REFERENCES reservation(id) ON DELETE CASCADE
);

-- =====================================================
-- 20) PAIEMENT_METHODE (montant par méthode + référence)
-- =====================================================

CREATE TABLE paiement_methode (
    id SERIAL PRIMARY KEY,
    paiement_id INT NOT NULL,
    methode_paiement_id INT NOT NULL,
    montant DECIMAL(10,2) NOT NULL,
    reference VARCHAR(500),
    FOREIGN KEY (paiement_id) REFERENCES paiement(id) ON DELETE CASCADE,
    FOREIGN KEY (methode_paiement_id) REFERENCES methode_paiement(id)
);

-- =====================================================
-- 21) INDEXES (mis à jour)
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
CREATE INDEX idx_vol_depart ON vol(aeroport_depart_id);
CREATE INDEX idx_vol_arrivee ON vol(aeroport_arrivee_id);
CREATE INDEX idx_vol_programme_date ON vol_programme(date_heure_depart);
CREATE INDEX idx_vol_programme_vol ON vol_programme(vol_id);
CREATE INDEX idx_vol_programme_avion ON vol_programme(avion_id);
CREATE INDEX idx_prixvol_vol_programme ON prix_vol(vol_programme_id);
CREATE INDEX idx_prixvol_classe ON prix_vol(classe_siege_id);
CREATE INDEX idx_siegevol_vol_programme ON siege_vol(vol_programme_id);
CREATE INDEX idx_siegevol_siege ON siege_vol(siege_id);
CREATE INDEX idx_siegevol_occupe ON siege_vol(occupe);
CREATE INDEX idx_vol_equipage_vol_programme ON vol_equipage(vol_programme_id);
CREATE INDEX idx_vol_equipage_equipage ON vol_equipage(equipage_id);
CREATE INDEX idx_client_email ON client(email);
CREATE INDEX idx_client_nom_prenom ON client(nom, prenom);
CREATE INDEX idx_reservation_client ON reservation(client_id);
CREATE INDEX idx_reservation_vol_programme ON reservation(vol_programme_id);
CREATE INDEX idx_reservation_statut ON reservation(statut);
CREATE INDEX idx_reservation_detail_reservation ON reservation_detail(reservation_id);
CREATE INDEX idx_billet_reservation ON billet(reservation_id);
CREATE INDEX idx_billet_siegevol ON billet(siege_vol_id);
CREATE INDEX idx_billet_statut ON billet(statut);
CREATE INDEX idx_paiement_reservation ON paiement(reservation_id);
CREATE INDEX idx_paiement_statut ON paiement(statut);
CREATE INDEX idx_paiement_methode_paiement ON paiement_methode(paiement_id);
CREATE INDEX idx_model_avion_classe_model ON model_avion_classe_siege(model_avion_id);
CREATE INDEX idx_model_avion_classe_classe ON model_avion_classe_siege(classe_siege_id);

-- =====================================================
-- 22) DONNEES PAR DEFAUT (mises à jour)
-- =====================================================

-- Insertion des états d'avion
INSERT INTO etat_avion (libelle) VALUES
    ('OPERATIONNEL'),
    ('EN_MAINTENANCE'),
    ('IMMOBILISE'),
    ('DECOMMISSIONNE');

-- Insertion des modèles d'avion
INSERT INTO model_avion (designation, fabricant, capacite, autonomie_km, vitesse_km_h, description) VALUES
    ('Boeing 747-8', 'Boeing', 416, 14685, 908, 'Gros porteur long-courrier'),
    ('Airbus A380', 'Airbus', 555, 15000, 903, 'Double pont long-courrier'),
    ('Boeing 777-300ER', 'Boeing', 365, 14685, 905, 'Gros porteur long-courrier biréacteur'),
    ('Airbus A350-900', 'Airbus', 325, 15000, 903, 'Long-courrier biréacteur'),
    ('Boeing 787-9', 'Boeing', 296, 14010, 903, 'Moyen-courrier long-distance');

-- Insertion des classes de siège
INSERT INTO classe_siege (libelle, description) VALUES 
    ('ECONOMIE', 'Classe Économique'),
    ('BUSINESS', 'Classe Affaires'),
    ('PREMIERE', 'Première Classe');

-- Insertion des rôles d'équipage
INSERT INTO role_equipage (libelle) VALUES 
    ('PILOTE'),
    ('COPILOTE'),
    ('HOTESSE'),
    ('STEWARD'),
    ('CHEF_DE_CABINE');

-- Insertion des méthodes de paiement
INSERT INTO methode_paiement (libelle) VALUES 
    ('ESPECES'),
    ('CARTE_BANCAIRE'),
    ('VIREMENT'),
    ('MOBILE_MONEY'),
    ('CHEQUE');

-- =====================================================
-- 23) COMMENTAIRES SUR LES MODIFICATIONS PRINCIPALES
-- =====================================================

-- NOUVELLES FONCTIONNALITES :
-- 1. Multi-classe : table reservation_detail pour gérer plusieurs classes dans une réservation
-- 2. Classes par modèle : table model_avion_classe_siege pour définir quelles classes un modèle supporte
-- 3. Billets auto-générés : créés automatiquement lors de la confirmation de réservation
-- 4. Paiements multiples : plusieurs paiements possibles par réservation
-- 5. Modes de paiement combinés : plusieurs méthodes pour un même paiement
-- 6. Timestamps : created_at et updated_at sur toutes les tables principales
-- 7. Gestion équipages : table vol_equipage pour assigner équipages aux vols

-- =====================================================
-- FIN DU SCRIPT
-- =====================================================