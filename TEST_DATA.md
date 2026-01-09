# 📊 Données de Test

Ce document contient des exemples de données pour tester l'application.

## 🛩️ Avions

### Avion 1
- **Code**: `AF001`
- **Modèle**: `Boeing 737-800`
- **Capacité**: `150`

### Avion 2
- **Code**: `AF002`
- **Modèle**: `Airbus A320`
- **Capacité**: `180`

### Avion 3
- **Code**: `AF003`
- **Modèle**: `Boeing 777-300`
- **Capacité**: `350`

## ✈️ Vols

### Vol 1
- **Numéro**: `AF100`
- **Départ**: `Paris CDG`
- **Arrivée**: `New York JFK`
- **Date/Heure Départ**: `2024-06-15 10:00`
- **Date/Heure Arrivée**: `2024-06-15 18:30`
- **Prix Base**: `850.00`
- **Avion**: `AF001`

### Vol 2
- **Numéro**: `AF200`
- **Départ**: `Lyon`
- **Arrivée**: `Londres`
- **Date/Heure Départ**: `2024-06-20 14:00`
- **Date/Heure Arrivée**: `2024-06-20 15:30`
- **Prix Base**: `120.00`
- **Avion**: `AF002`

### Vol 3
- **Numéro**: `AF300`
- **Départ**: `Paris CDG`
- **Arrivée**: `Tokyo Narita`
- **Date/Heure Départ**: `2024-07-01 23:00`
- **Date/Heure Arrivée**: `2024-07-02 18:00`
- **Prix Base**: `1200.00`
- **Avion**: `AF003`

## 👥 Clients

### Client 1
- **Nom**: `Dupont`
- **Prénom**: `Jean`
- **Email**: `jean.dupont@email.com`
- **Téléphone**: `+33 6 12 34 56 78`

### Client 2
- **Nom**: `Martin`
- **Prénom**: `Sophie`
- **Email**: `sophie.martin@email.com`
- **Téléphone**: `+33 6 98 76 54 32`

### Client 3
- **Nom**: `Bernard`
- **Prénom**: `Pierre`
- **Email**: `pierre.bernard@email.com`
- **Téléphone**: `+33 6 11 22 33 44`

### Client 4
- **Nom**: `Dubois`
- **Prénom**: `Marie`
- **Email**: `marie.dubois@email.com`
- **Téléphone**: `+33 6 55 66 77 88`

## 👨‍✈️ Équipage

### Pilote 1
- **Nom**: `Leclerc`
- **Prénom**: `Antoine`
- **Rôle**: `PILOTE`

### Copilote 1
- **Nom**: `Moreau`
- **Prénom**: `François`
- **Rôle**: `COPILOTE`

### Hôtesse 1
- **Nom**: `Laurent`
- **Prénom**: `Isabelle`
- **Rôle**: `HOTESSE`

### Steward 1
- **Nom**: `Simon`
- **Prénom**: `Thomas`
- **Rôle**: `STEWARD`

## 📋 Scénarios de test

### Scénario 1 : Réservation simple

1. Créer l'avion `AF001`
2. Créer le vol `AF100`
3. Créer le client `Jean Dupont`
4. Créer une réservation pour `Jean Dupont` sur le vol `AF100`
5. Dans les détails de la réservation, émettre un billet pour un siège Economy

**Résultat attendu** :
- Le siège sélectionné devient occupé
- Le billet est créé avec le statut `EMIS`
- Le client a 1 réservation active

### Scénario 2 : Réservation avec plusieurs billets

1. Utiliser les mêmes vol et client
2. Créer une nouvelle réservation
3. Émettre 2 billets :
   - 1 siège Business
   - 1 siège Economy

**Résultat attendu** :
- 2 sièges sont occupés
- 2 billets sont créés
- Les prix sont calculés correctement

### Scénario 3 : Annulation de réservation

1. Créer une réservation avec 2 billets
2. Annuler la réservation

**Résultat attendu** :
- Le statut de la réservation devient `ANNULEE`
- Les billets passent au statut `ANNULE`
- Les sièges sont libérés (occupe = false)
- Les sièges peuvent être réservés à nouveau

### Scénario 4 : Filtres

#### Test des filtres Avions
- Filtrer par code : `AF001`
- Filtrer par modèle : `Boeing`
- Filtrer par capacité : Min `100`, Max `200`

#### Test des filtres Vols
- Filtrer par départ : `Paris`
- Filtrer par arrivée : `New York`
- Filtrer par date : `2024-06-01` à `2024-06-30`
- Filtrer par prix : Min `500`, Max `1000`

#### Test des filtres Clients
- Filtrer par nom : `Dupont`
- Filtrer par email : `jean`

#### Test des filtres Réservations
- Filtrer par statut : `CONFIRMEE`
- Filtrer par client
- Filtrer par vol
- Filtrer par date

### Scénario 5 : Génération automatique des sièges

1. Créer un avion avec une capacité de `100` places
2. Vérifier dans les détails de l'avion :
   - **70 sièges Economy** (70%)
   - **30 sièges Business** (30%)
   - Numérotation : `1E`, `2E`, ..., `70E`, `1B`, `2B`, ..., `30B`

### Scénario 6 : Génération automatique des sièges-vol

1. Créer un vol avec un avion de 100 places
2. Vérifier dans la base de données :
   - **100 SiegeVol** créés automatiquement
   - Tous avec `occupe = false` au départ

### Scénario 7 : Affectation d'équipage

1. Créer 4 membres d'équipage (Pilote, Copilote, Hôtesse, Steward)
2. Créer un vol
3. Associer l'équipage au vol via VolEquipage
4. Vérifier dans les détails du vol et de l'équipage

## 🔍 Vérifications SQL

Pour vérifier les données dans la base PostgreSQL :

```sql
-- Voir tous les avions avec le nombre de sièges
SELECT a.code_avion, a.modele, a.capacite_totale, COUNT(s.id) as nb_sieges
FROM avion a
LEFT JOIN siege s ON s.avion_id = a.id
GROUP BY a.id;

-- Voir tous les vols avec le nombre de sièges occupés
SELECT v.numero_vol, v.depart, v.arrivee, 
       COUNT(sv.id) as total_sieges,
       SUM(CASE WHEN sv.occupe = true THEN 1 ELSE 0 END) as sieges_occupes
FROM vol v
LEFT JOIN siege_vol sv ON sv.vol_id = v.id
GROUP BY v.id;

-- Voir toutes les réservations avec détails
SELECT r.id, c.nom, c.prenom, v.numero_vol, r.statut, COUNT(b.id) as nb_billets
FROM reservation r
JOIN client c ON r.client_id = c.id
JOIN vol v ON r.vol_id = v.id
LEFT JOIN billet b ON b.reservation_id = r.id
GROUP BY r.id, c.nom, c.prenom, v.numero_vol, r.statut;

-- Voir les sièges disponibles pour un vol
SELECT s.numero_siege, s.classe_siege
FROM siege_vol sv
JOIN siege s ON sv.siege_id = s.id
WHERE sv.vol_id = 1 AND sv.occupe = false;
```

## 💡 Conseils de test

1. **Toujours créer les avions en premier** (pour avoir des avions disponibles pour les vols)
2. **Les sièges sont automatiques** - ne pas essayer de les créer manuellement
3. **Tester les filtres** avec des valeurs partielles (ex: "Paris" trouve "Paris CDG")
4. **Tester l'annulation** pour vérifier que les sièges sont bien libérés
5. **Vérifier les contraintes** en essayant de réserver un siège déjà occupé
6. **Tester avec plusieurs clients** sur le même vol

## 🐛 Cas d'erreur à tester

1. ❌ Essayer de créer 2 avions avec le même code
2. ❌ Essayer de créer 2 vols avec le même numéro
3. ❌ Essayer de créer 2 clients avec le même email
4. ❌ Essayer d'émettre un billet pour un siège déjà occupé
5. ❌ Essayer d'annuler une réservation déjà annulée

Ces cas devraient être gérés par les contraintes de base de données et la validation côté application.
