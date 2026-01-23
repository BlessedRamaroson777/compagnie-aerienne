-- Script de création de la table paiement_diffusion

-- Création de la table paiement_diffusion
CREATE TABLE IF NOT EXISTS paiement_diffusion (
    id_paiement_diffusion SERIAL PRIMARY KEY,
    id_diffusion_societe INTEGER NOT NULL,
    montant_paye NUMERIC(15, 2) NOT NULL,
    date_paiement DATE NOT NULL,
    CONSTRAINT fk_paiement_diffusion_societe 
        FOREIGN KEY (id_diffusion_societe) 
        REFERENCES diffusion_societe(id_diffusion_societe) 
        ON DELETE CASCADE
);

-- Commentaires
COMMENT ON TABLE paiement_diffusion IS 'Table des paiements effectués pour les diffusions société';
COMMENT ON COLUMN paiement_diffusion.id_paiement_diffusion IS 'Identifiant unique du paiement';
COMMENT ON COLUMN paiement_diffusion.id_diffusion_societe IS 'Référence vers la diffusion société';
COMMENT ON COLUMN paiement_diffusion.montant_paye IS 'Montant payé en Ariary';
COMMENT ON COLUMN paiement_diffusion.date_paiement IS 'Date du paiement';

-- Données de test
INSERT INTO paiement_diffusion (id_diffusion_societe, montant_paye, date_paiement) VALUES
(1, 50000.00, '2026-01-15'),
(1, 30000.00, '2026-01-20'),
(2, 100000.00, '2026-01-18');
