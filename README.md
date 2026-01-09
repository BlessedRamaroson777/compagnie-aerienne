# Système de Gestion de Compagnie Aérienne - Back-Office

Application Spring Boot complète pour la gestion d'une compagnie aérienne.

## Technologies Utilisées

- **Spring Boot 4.0.1**
- **Thymeleaf** - Moteur de templates
- **PostgreSQL** - Base de données
- **Spring Data JPA** - Accès aux données
- **JPA Specification** - Filtres dynamiques
- **Lombok** - Réduction du code boilerplate
- **Bootstrap 5** - Interface utilisateur
- **Architecture MVC**

## Fonctionnalités

### Entités Gérées

1. **Avion** - Gestion de la flotte
   - Code avion, modèle, capacité
   - Génération automatique des sièges (70% Economy, 30% Business)

2. **Siège** - Sièges physiques des avions
   - Numéro, classe (Economy/Business)
   - Lié à un avion

3. **Vol** - Planification des vols
   - Numéro, départ, arrivée, dates, prix
   - Création automatique des sièges disponibles
   - Affectation d'équipage

4. **SiègeVol** - État des sièges par vol
   - Gestion de l'occupation
   - Liaison vol-siège

5. **Client** - Base clients
   - Nom, prénom, email, téléphone
   - Historique des réservations

6. **Réservation** - Gestion des réservations
   - Création, modification, annulation
   - Statuts : CONFIRMEE, ANNULEE
   - Gestion des billets

7. **Billet** - Billets émis
   - Liaison réservation-siège
   - Statuts : EMIS, ANNULE
   - Calcul automatique du prix

8. **Paiement** - Système de paiement
   - Paiement partiel ou total
   - Multi-méthodes
   - Statuts : EN_ATTENTE, VALIDE, REFUSE

9. **MéthodePaiement** - Moyens de paiement
   - CARTE, CASH, MOBILE_MONEY, VIREMENT

10. **PaiementMethode** - Répartition des paiements
    - Montants par méthode

11. **Équipage** - Membres d'équipage
    - Nom, prénom, rôle
    - Rôles : PILOTE, COPILOTE, HOTESSE, STEWARD

12. **VolÉquipage** - Affectation équipage-vol

## Architecture du Projet

```
src/main/java/com/airline/airline_app/
├── config/              # Configuration et initialisation
├── controller/          # Controllers MVC
├── entity/             # Entités JPA
│   └── enums/          # Énumérations
├── repository/         # Repositories Spring Data
├── service/            # Services métier
└── specification/      # Filtres JPA Specification

src/main/resources/
├── application.properties
└── templates/          # Vues Thymeleaf
    ├── avion/
    ├── vol/
    ├── client/
    ├── reservation/
    ├── equipage/
    ├── index.html
    └── layout.html
```

## Installation et Configuration

### Prérequis

- Java 17 ou supérieur
- PostgreSQL 12 ou supérieur
- Maven 3.6 ou supérieur

### Configuration de la Base de Données

1. Créer une base de données PostgreSQL :
```sql
CREATE DATABASE airline_db;
```

2. Modifier le fichier `application.properties` avec vos identifiants :
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/airline_db
spring.datasource.username=votre_utilisateur
spring.datasource.password=votre_mot_de_passe
```

### Démarrage de l'Application

1. Compiler le projet :
```bash
mvn clean install
```

2. Lancer l'application :
```bash
mvn spring-boot:run
```

3. Accéder à l'application :
```
http://localhost:8080
```

## Fonctionnalités Métier

### Gestion des Avions
- CRUD complet
- Génération automatique des sièges à la création
- Filtrage : code, modèle, capacité

### Gestion des Vols
- CRUD complet
- Création automatique des sièges disponibles
- Affectation d'avion
- Filtrage : numéro, départ, arrivée, date, prix

### Gestion des Réservations
- Création de réservation
- Émission de billets avec sélection de siège
- Annulation de réservation (libère les sièges)
- Filtrage : client, vol, statut, date

### Gestion des Clients
- CRUD complet
- Filtrage : nom, prénom, email

### Gestion de l'Équipage
- CRUD complet
- Filtrage : nom, prénom, rôle

### Système de Paiement
- Paiement multi-méthodes
- Répartition du montant sur plusieurs méthodes
- Validation automatique

## Service CRUD Générique

Le projet utilise un service CRUD générique pour réduire la duplication de code :

```java
GenericService<T, ID>
├── save(entity)
├── update(id, entity)
├── delete(id)
├── findById(id)
├── findAll()
└── findAll(specification)
```

## Filtres Multi-Critères

Tous les listings utilisent JPA Specification pour des filtres dynamiques et combinables :

- Filtres optionnels
- Combinaison AND automatique
- Recherche insensible à la casse
- Support des intervalles (dates, prix, etc.)

## Structure des URLs

- `/` - Page d'accueil
- `/avions` - Liste des avions
- `/avions/new` - Création d'avion
- `/avions/{id}` - Détail d'avion
- `/avions/{id}/edit` - Modification d'avion
- `/avions/{id}/delete` - Suppression d'avion

(Pattern similaire pour toutes les entités)

## Bonnes Pratiques Implémentées

- ✅ Architecture MVC stricte
- ✅ Séparation des responsabilités
- ✅ Service CRUD générique
- ✅ Specifications pour filtres
- ✅ Transactions gérées avec @Transactional
- ✅ Validation des données
- ✅ Gestion des erreurs avec messages flash
- ✅ Interface responsive avec Bootstrap
- ✅ Code documenté
- ✅ Utilisation de Lombok
- ✅ Relations JPA optimisées

## Auteurs

Projet développé dans le cadre d'un système de gestion de compagnie aérienne.

## Licence

Ce projet est à usage éducatif.
