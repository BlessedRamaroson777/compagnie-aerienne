# Modifications du système de réservation

## Vue d'ensemble
Le système de réservation a été modifié pour prendre en compte les catégories de passagers avec leurs remises par classe de siège.

## Modifications apportées

### 1. Contrôleur ReservationController.java

**Ajout des repositories :**
- `CategoriePassagerRepository` - pour charger les catégories de passagers
- `RemiseCategoriePlaceRepository` - pour charger les remises

**Méthode `configurerReservation()` mise à jour :**
```java
// Obtenir toutes les catégories de passagers
List<CategoriePassager> categories = categoriePassagerRepository.findAll();

// Obtenir les remises pour ce vol programmé
List<RemiseCategoriePlace> remises = remiseCategoriePlaceRepository.findByVolProgramme_Id(volProgrammeId);

model.addAttribute("categories", categories);
model.addAttribute("remises", remises);
```

### 2. Template reservation/configurer.html

**Structure modifiée :**

Pour chaque classe de siège :
1. **En-tête** : affiche le nom de la classe, prix de base et total de passagers (calculé automatiquement)
2. **Table des catégories** : liste toutes les catégories avec :
   - Libellé de la catégorie
   - Remise applicable
   - Prix après remise
   - Input pour le nombre de passagers

**Champs du formulaire :**
- `places_{classeId}` : Total de passagers pour cette classe (readonly, auto-calculé)
- `categorie_{classeId}_{categorieId}` : Nombre de passagers pour une catégorie donnée dans une classe

### 3. JavaScript - Calculs automatiques

**Fonction `updateCategoryTotal(input)` :**
- Calcule le total de passagers pour une classe donnée
- Vérifie que le total ne dépasse pas le nombre de places disponibles
- Met à jour le champ total automatiquement

**Fonction `updateTotal()` :**
- Calcule le prix total en tenant compte des remises
- Formule : `(prix_base - remise) × nombre_passagers`
- Affiche le détail par classe et par catégorie
- Active/désactive le bouton de réservation selon la sélection

## Exemple d'utilisation

### 1. Configuration des remises

Avant de faire une réservation, assurez-vous que les remises sont configurées dans la table `remise_categorie_place` :

```sql
INSERT INTO remise_categorie_place (vol_programme_id, id_categorie_passager, id_classe_siege, remise_prix)
VALUES 
    (1, 1, 1, 0),      -- ADULTE Economy : 0 Ar de remise
    (1, 2, 1, 50000),  -- ENFANT Economy : 50000 Ar de remise
    (1, 3, 1, 120000), -- BEBE Economy : 120000 Ar de remise
    (1, 4, 1, 40000),  -- SENIOR Economy : 40000 Ar de remise
    (1, 5, 1, 30000);  -- ETUDIANT Economy : 30000 Ar de remise
```

### 2. Processus de réservation

1. Aller sur `/reservations/new`
2. Sélectionner l'aéroport de départ et d'arrivée
3. Choisir un vol programmé
4. Sur la page de configuration :
   - Sélectionner un client
   - Pour chaque classe, entrer le nombre de passagers par catégorie
   - Le total se calcule automatiquement
   - Le prix tient compte des remises

### 3. Exemple de calcul

**Classe Economy : 150 000 Ar**
- 2 ADULTE × (150 000 - 0) = 300 000 Ar
- 1 ENFANT × (150 000 - 50 000) = 100 000 Ar
- 1 BEBE × (150 000 - 120 000) = 30 000 Ar

**Total : 4 passagers = 430 000 Ar**

## Validation

Le système vérifie automatiquement que :
- Le total de passagers par classe ne dépasse pas le nombre de places disponibles
- Au moins un passager est sélectionné avant de permettre la réservation
- Les remises sont correctement appliquées

## Données dans le formulaire

Le formulaire envoie :
- `volProgrammeId` : ID du vol programmé
- `clientId` : ID du client
- `places_{classeId}` : Total de passagers par classe
- `categorie_{classeId}_{categorieId}` : Nombre de passagers par catégorie et classe

## Prochaines étapes

Pour que le backend traite correctement les données par catégorie, il faudra modifier la méthode `confirmerReservation()` pour :
1. Parser les paramètres `categorie_{classeId}_{categorieId}`
2. Créer des enregistrements `ReservationCategoriePassager` pour chaque catégorie
3. Calculer le prix total en tenant compte des remises

## Accès à l'application

- **URL** : http://localhost:8081
- **Page de réservation** : http://localhost:8081/reservations/new
- **Gestion des catégories** : http://localhost:8081/categories-passager
- **Gestion des remises** : http://localhost:8081/remises-categorie-place
