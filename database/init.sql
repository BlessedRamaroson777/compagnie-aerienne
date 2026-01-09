-- Script de création de la base de données PostgreSQL pour l'application Airline

-- Créer la base de données
CREATE DATABASE airline_db;

-- Se connecter à la base de données
\c airline_db;

-- Créer un utilisateur pour l'application (optionnel)
-- CREATE USER airline_user WITH PASSWORD 'airline_password';
-- GRANT ALL PRIVILEGES ON DATABASE airline_db TO airline_user;

-- Les tables seront créées automatiquement par Hibernate grâce à spring.jpa.hibernate.ddl-auto=update
-- Ce script est juste pour initialiser la base de données

-- Vérifier la connexion
SELECT version();

-- Afficher les tables créées après le démarrage de l'application
-- \dt
