-- Script de création des tables pour le module de diffusion

-- Création de l'enum pour les statuts
CREATE TYPE status_historique AS ENUM ('CREATION', 'MODIFICATION', 'ACTIF', 'INACTIF');

-- Table Societe
CREATE TABLE IF NOT EXISTS societe (
    id_societe BIGSERIAL PRIMARY KEY,
    libelle VARCHAR(100) NOT NULL
);

-- Table DiffusionSociete
CREATE TABLE IF NOT EXISTS diffusion_societe (
    id_diffusion_societe BIGSERIAL PRIMARY KEY,
    id_societe BIGINT NOT NULL,
    nombre INTEGER NOT NULL,
    date TIMESTAMP NOT NULL,
    CONSTRAINT fk_diffusion_societe_societe FOREIGN KEY (id_societe) 
        REFERENCES societe(id_societe) ON DELETE CASCADE
);

-- Table ConfigurationCoutDiffusion
CREATE TABLE IF NOT EXISTS configuration_cout_diffusion (
    id_configuration_cout_diffusion BIGSERIAL PRIMARY KEY,
    id_diffusion_societe BIGINT NOT NULL,
    prix DOUBLE PRECISION NOT NULL,
    CONSTRAINT fk_configuration_cout_diffusion_diffusion_societe FOREIGN KEY (id_diffusion_societe) 
        REFERENCES diffusion_societe(id_diffusion_societe) ON DELETE CASCADE
);

-- Table HistoriqueConfigurationCout
CREATE TABLE IF NOT EXISTS historique_configuration_cout (
    id_historique_configuration_cout BIGSERIAL PRIMARY KEY,
    id_configuration_cout_diffusion BIGINT,
    prix DOUBLE PRECISION NOT NULL,
    status status_historique NOT NULL,
    date TIMESTAMP NOT NULL,
    CONSTRAINT fk_historique_configuration_cout FOREIGN KEY (id_configuration_cout_diffusion) 
        REFERENCES configuration_cout_diffusion(id_configuration_cout_diffusion) ON DELETE CASCADE
);

-- Table DiffusionVolProgramme
CREATE TABLE IF NOT EXISTS diffusion_vol_programme (
    id_diffusion_vol_programme BIGSERIAL PRIMARY KEY,
    id_diffusion_societe BIGINT NOT NULL,
    nombre INTEGER NOT NULL,
    id_vol_programme BIGINT NOT NULL,
    CONSTRAINT fk_diffusion_vol_programme_diffusion_societe FOREIGN KEY (id_diffusion_societe) 
        REFERENCES diffusion_societe(id_diffusion_societe) ON DELETE CASCADE,
    CONSTRAINT fk_diffusion_vol_programme_vol_programme FOREIGN KEY (id_vol_programme) 
        REFERENCES vol_programme(id) ON DELETE CASCADE
);

-- Index pour améliorer les performances
CREATE INDEX IF NOT EXISTS idx_diffusion_societe_societe ON diffusion_societe(id_societe);
CREATE INDEX IF NOT EXISTS idx_diffusion_societe_date ON diffusion_societe(date);
CREATE INDEX IF NOT EXISTS idx_configuration_cout_diffusion_diffusion_societe ON configuration_cout_diffusion(id_diffusion_societe);
CREATE INDEX IF NOT EXISTS idx_historique_configuration_cout_status ON historique_configuration_cout(status);
CREATE INDEX IF NOT EXISTS idx_historique_configuration_cout_date ON historique_configuration_cout(date);
CREATE INDEX IF NOT EXISTS idx_historique_configuration_cout_config ON historique_configuration_cout(id_configuration_cout_diffusion);
CREATE INDEX IF NOT EXISTS idx_diffusion_vol_programme_diffusion_societe ON diffusion_vol_programme(id_diffusion_societe);
CREATE INDEX IF NOT EXISTS idx_diffusion_vol_programme_vol_programme ON diffusion_vol_programme(id_vol_programme);

-- Données de test
INSERT INTO societe (libelle) VALUES 
    ('Société A'),
    ('Société B'),
    ('Société C'),
    ('Agence de Voyage XYZ'),
    ('Partenaire Premium');

INSERT INTO diffusion_societe (id_societe, nombre, date) VALUES 
    (1, 100, NOW()),
    (2, 50, NOW() - INTERVAL '1 day'),
    (3, 75, NOW() - INTERVAL '2 days');

INSERT INTO configuration_cout_diffusion (id_diffusion_societe, prix) VALUES 
    (1, 10.50),
    (2, 15.00),
    (3, 12.75);

INSERT INTO historique_configuration_cout (prix, status, date) VALUES 
    (10.00, 'ACTIF', NOW() - INTERVAL '10 days'),
    (12.00, 'INACTIF', NOW() - INTERVAL '5 days'),
    (15.00, 'ACTIF', NOW());

-- Fonction trigger pour insérer automatiquement dans historique_configuration_cout
CREATE OR REPLACE FUNCTION fn_insert_historique_configuration_cout()
RETURNS TRIGGER AS $$
BEGIN
    -- Insérer dans l'historique lors d'une création
    IF (TG_OP = 'INSERT') THEN
        INSERT INTO historique_configuration_cout (id_configuration_cout_diffusion, prix, status, date)
        VALUES (NEW.id_configuration_cout_diffusion, NEW.prix, 'CREATION'::status_historique, NOW());
    -- Insérer dans l'historique lors d'une modification
    ELSIF (TG_OP = 'UPDATE') THEN
        INSERT INTO historique_configuration_cout (id_configuration_cout_diffusion, prix, status, date)
        VALUES (NEW.id_configuration_cout_diffusion, NEW.prix, 'MODIFICATION'::status_historique, NOW());
    END IF;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Créer le trigger
DROP TRIGGER IF EXISTS trg_configuration_cout_diffusion_historique ON configuration_cout_diffusion;
CREATE TRIGGER trg_configuration_cout_diffusion_historique
    AFTER INSERT OR UPDATE ON configuration_cout_diffusion
    FOR EACH ROW
    EXECUTE FUNCTION fn_insert_historique_configuration_cout();

-- Commentaires pour documentation
COMMENT ON TABLE societe IS 'Table des sociétés partenaires (agences de voyage, plateformes de réservation)';
COMMENT ON TABLE diffusion_societe IS 'Enregistrement des diffusions envoyées aux sociétés';
COMMENT ON TABLE configuration_cout_diffusion IS 'Configuration des coûts de diffusion';
COMMENT ON TABLE historique_configuration_cout IS 'Historique des modifications de configuration de coûts';
COMMENT ON TABLE diffusion_vol_programme IS 'Liaison entre diffusions et vols programmés';
