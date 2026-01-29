-- =============================================
-- Système de Gestion des Produits
-- =============================================

-- Table 1: Produits
CREATE TABLE IF NOT EXISTS produit (
    id_produit SERIAL PRIMARY KEY,
    libelle VARCHAR(255) NOT NULL,
    stock INTEGER DEFAULT 0 CHECK (stock >= 0),
    prix DECIMAL(15,2) NOT NULL CHECK (prix >= 0),
    date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table 2: Historique des mouvements de produit
CREATE TABLE IF NOT EXISTS historique_mouvement_produit (
    id_historique SERIAL PRIMARY KEY,
    id_produit INTEGER NOT NULL,
    quantite INTEGER NOT NULL CHECK (quantite > 0),
    est_entree BOOLEAN NOT NULL,  -- true = entrée en stock, false = sortie de stock
    date_mouvement TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_historique_produit FOREIGN KEY (id_produit) 
        REFERENCES produit(id_produit) ON DELETE CASCADE
);

-- Table 3: Prix Produit (historique des prix)
CREATE TABLE IF NOT EXISTS prix_produit (
    id_prix_produit SERIAL PRIMARY KEY,
    id_produit INTEGER NOT NULL,
    prix DECIMAL(15,2) NOT NULL CHECK (prix >= 0),
    date_modification TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_prix_produit FOREIGN KEY (id_produit) 
        REFERENCES produit(id_produit) ON DELETE CASCADE
);

-- Table 4: Produit-Catégorie Siège Passager (association)
CREATE TABLE IF NOT EXISTS produit_categorie_siege_passager (
    id SERIAL PRIMARY KEY,
    id_produit INTEGER NOT NULL,
    id_categorie_passager INTEGER NOT NULL,
    id_classe_siege INTEGER NOT NULL,
    CONSTRAINT fk_produit_categorie FOREIGN KEY (id_produit) 
        REFERENCES produit(id_produit) ON DELETE CASCADE,
    CONSTRAINT fk_categorie_passager FOREIGN KEY (id_categorie_passager) 
        REFERENCES categorie_passager(id_categorie_passager) ON DELETE CASCADE,
    CONSTRAINT fk_classe_siege FOREIGN KEY (id_classe_siege) 
        REFERENCES classe_siege(id_classe_siege) ON DELETE CASCADE,
    CONSTRAINT unique_produit_categorie_classe UNIQUE (id_produit, id_categorie_passager, id_classe_siege)
);

-- Table 5: Vente de Produit
CREATE TABLE IF NOT EXISTS vente_produit (
    id_vente_produit SERIAL PRIMARY KEY,
    id_produit INTEGER NOT NULL,
    quantite INTEGER NOT NULL CHECK (quantite > 0),
    id_vol_programme INTEGER NOT NULL,
    date_vente TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_vente_produit FOREIGN KEY (id_produit) 
        REFERENCES produit(id_produit) ON DELETE CASCADE,
    CONSTRAINT fk_vente_vol_programme FOREIGN KEY (id_vol_programme) 
        REFERENCES vol_programme(id_vol_programme) ON DELETE CASCADE
);

-- =============================================
-- Triggers pour gestion automatique
-- =============================================

-- Trigger 1: Enregistrer l'historique lors de l'ajout d'un nouveau produit
CREATE OR REPLACE FUNCTION trigger_nouveau_produit()
RETURNS TRIGGER AS $$
BEGIN
    -- Si le stock initial est > 0, enregistrer une entrée dans l'historique
    IF NEW.stock > 0 THEN
        INSERT INTO historique_mouvement_produit (id_produit, quantite, est_entree)
        VALUES (NEW.id_produit, NEW.stock, TRUE);
    END IF;
    
    -- Enregistrer le prix initial dans l'historique des prix
    INSERT INTO prix_produit (id_produit, prix)
    VALUES (NEW.id_produit, NEW.prix);
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER after_insert_produit
AFTER INSERT ON produit
FOR EACH ROW
EXECUTE FUNCTION trigger_nouveau_produit();

-- Trigger 2: Enregistrer l'historique lors d'une vente de produit
CREATE OR REPLACE FUNCTION trigger_vente_produit()
RETURNS TRIGGER AS $$
DECLARE
    stock_actuel INTEGER;
BEGIN
    -- Vérifier si le stock est suffisant
    SELECT stock INTO stock_actuel FROM produit WHERE id_produit = NEW.id_produit;
    
    IF stock_actuel < NEW.quantite THEN
        RAISE EXCEPTION 'Stock insuffisant pour le produit %. Stock actuel: %, Quantité demandée: %', 
            NEW.id_produit, stock_actuel, NEW.quantite;
    END IF;
    
    -- Enregistrer la sortie dans l'historique (est_entree = FALSE)
    INSERT INTO historique_mouvement_produit (id_produit, quantite, est_entree)
    VALUES (NEW.id_produit, NEW.quantite, FALSE);
    
    -- Décrémenter le stock
    UPDATE produit 
    SET stock = stock - NEW.quantite
    WHERE id_produit = NEW.id_produit;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER after_insert_vente_produit
AFTER INSERT ON vente_produit
FOR EACH ROW
EXECUTE FUNCTION trigger_vente_produit();

-- Trigger 4: Enregistrer l'historique lors de la modification du stock
CREATE OR REPLACE FUNCTION trigger_modification_stock()
RETURNS TRIGGER AS $$
BEGIN
    -- Si le stock a augmenté
    IF NEW.stock > OLD.stock THEN
        INSERT INTO historique_mouvement_produit (id_produit, quantite, est_entree)
        VALUES (NEW.id_produit, NEW.stock - OLD.stock, TRUE);
    -- Si le stock a diminué
    ELSIF NEW.stock < OLD.stock THEN
        INSERT INTO historique_mouvement_produit (id_produit, quantite, est_entree)
        VALUES (NEW.id_produit, OLD.stock - NEW.stock, FALSE);
    END IF;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER after_update_stock_produit
AFTER UPDATE OF stock ON produit
FOR EACH ROW
WHEN (OLD.stock IS DISTINCT FROM NEW.stock)
EXECUTE FUNCTION trigger_modification_stock();

-- Trigger 3: Enregistrer l'historique des modifications de prix
CREATE OR REPLACE FUNCTION trigger_modification_prix()
RETURNS TRIGGER AS $$
BEGIN
    -- Si le prix a changé, enregistrer dans l'historique des prix
    IF OLD.prix IS DISTINCT FROM NEW.prix THEN
        INSERT INTO prix_produit (id_produit, prix)
        VALUES (NEW.id_produit, NEW.prix);
    END IF;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER after_update_prix_produit
AFTER UPDATE OF prix ON produit
FOR EACH ROW
EXECUTE FUNCTION trigger_modification_prix();

-- =============================================
-- Fonction utilitaire: Ajout de stock (entrée manuelle)
-- =============================================
CREATE OR REPLACE FUNCTION ajouter_stock_produit(
    p_id_produit INTEGER,
    p_quantite INTEGER
)
RETURNS VOID AS $$
BEGIN
    -- Vérifier que la quantité est positive
    IF p_quantite <= 0 THEN
        RAISE EXCEPTION 'La quantité doit être positive';
    END IF;
    
    -- Enregistrer l'entrée dans l'historique
    INSERT INTO historique_mouvement_produit (id_produit, quantite, est_entree)
    VALUES (p_id_produit, p_quantite, TRUE);
    
    -- Incrémenter le stock
    UPDATE produit 
    SET stock = stock + p_quantite
    WHERE id_produit = p_id_produit;
END;
$$ LANGUAGE plpgsql;

-- =============================================
-- Index pour optimisation des performances
-- =============================================
CREATE INDEX idx_historique_produit ON historique_mouvement_produit(id_produit);
CREATE INDEX idx_historique_date ON historique_mouvement_produit(date_mouvement);
CREATE INDEX idx_prix_produit ON prix_produit(id_produit);
CREATE INDEX idx_prix_date ON prix_produit(date_modification);
CREATE INDEX idx_vente_produit ON vente_produit(id_produit);
CREATE INDEX idx_vente_vol ON vente_produit(id_vol_programme);

-- =============================================
-- Données de test
-- =============================================
-- Note: Les données de test pour les produits doivent être insérées APRÈS
-- avoir des vols programmés dans la base de données.
-- Exécutez d'abord les données de base (vol, avion, aéroport, etc.)
-- puis exécutez les inserts ci-dessous.

/*
INSERT INTO produit (libelle, stock, prix) VALUES 
    ('Eau minérale 500ml', 500, 1500.00),
    ('Jus d''orange 250ml', 300, 2500.00),
    ('Sandwich jambon-fromage', 200, 8000.00),
    ('Chips', 400, 3000.00),
    ('Café', 350, 2000.00),
    ('Coca-Cola 330ml', 300, 2000.00),
    ('Chocolat', 250, 3500.00),
    ('Salade César', 100, 12000.00),
    ('Croissant', 150, 2500.00),
    ('Thé', 200, 1800.00);
*/

-- =============================================
-- Vues utiles
-- =============================================

-- Vue: Stock actuel avec dernier prix
CREATE OR REPLACE VIEW v_produit_stock AS
SELECT 
    p.id_produit,
    p.libelle,
    p.stock AS stock_actuel,
    p.prix AS prix_actuel,
    p.date_creation,
    (SELECT COUNT(*) FROM historique_mouvement_produit 
     WHERE id_produit = p.id_produit AND est_entree = TRUE) AS total_entrees,
    (SELECT COUNT(*) FROM historique_mouvement_produit 
     WHERE id_produit = p.id_produit AND est_entree = FALSE) AS total_sorties
FROM produit p;

-- Vue: Historique complet des mouvements
CREATE OR REPLACE VIEW v_historique_mouvements AS
SELECT 
    h.id_historique,
    p.libelle AS produit,
    h.quantite,
    CASE WHEN h.est_entree THEN 'Entrée' ELSE 'Sortie' END AS type_mouvement,
    h.date_mouvement
FROM historique_mouvement_produit h
JOIN produit p ON h.id_produit = p.id_produit
ORDER BY h.date_mouvement DESC;

-- Vue: Historique des prix
CREATE OR REPLACE VIEW v_historique_prix AS
SELECT 
    pp.id_prix_produit,
    p.libelle AS produit,
    pp.prix,
    pp.date_modification
FROM prix_produit pp
JOIN produit p ON pp.id_produit = p.id_produit
ORDER BY pp.date_modification DESC;
