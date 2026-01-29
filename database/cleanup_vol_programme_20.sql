-- Script de nettoyage pour supprimer uniquement le vol_programme problématique (id=20)
-- Ce script garde toutes vos autres données

-- Supprimer d'abord les enregistrements dépendants dans diffusion_vol_programme
DELETE FROM diffusion_vol_programme WHERE id_vol_programme = 20;

-- Message de confirmation
SELECT 'Nettoyage terminé: vol_programme id=20 et ses dépendances supprimés' AS statut;
