# 📁 Structure du Projet - Documentation Complète

## 🗂️ Organisation des Fichiers

```
airline-app/
│
├── 📄 pom.xml                          # Configuration Maven avec dépendances
├── 📄 README.md                        # Documentation principale du projet
├── 📄 QUICKSTART.md                    # Guide de démarrage rapide (5 minutes)
├── 📄 TEST_DATA.md                     # Exemples de données de test
├── 📄 DEPLOYMENT_CHECKLIST.md          # Check-list pour le déploiement
├── 📄 PROJECT_STRUCTURE.md             # Ce fichier - Vue d'ensemble
│
├── 📁 database/                        # Scripts SQL
│   ├── init.sql                        # Initialisation de la base
│   └── demo_data.sql                   # Données de démonstration
│
├── 📄 start.sh                         # Script de démarrage Linux/Mac
├── 📄 start.bat                        # Script de démarrage Windows
│
└── 📁 src/
    ├── 📁 main/
    │   ├── 📁 java/com/airline/airline_app/
    │   │   │
    │   │   ├── 📄 AirlineAppApplication.java     # Point d'entrée Spring Boot
    │   │   │
    │   │   ├── 📁 entity/                         # 🏛️ Entités JPA (12 fichiers)
    │   │   │   ├── Avion.java                     # Avions de la compagnie
    │   │   │   ├── Siege.java                     # Sièges des avions
    │   │   │   ├── Vol.java                       # Vols programmés
    │   │   │   ├── SiegeVol.java                  # Disponibilité des sièges par vol
    │   │   │   ├── Client.java                    # Clients
    │   │   │   ├── Reservation.java               # Réservations
    │   │   │   ├── Billet.java                    # Billets émis
    │   │   │   ├── Paiement.java                  # Paiements
    │   │   │   ├── MethodePaiement.java           # Méthodes de paiement disponibles
    │   │   │   ├── PaiementMethode.java           # Répartition paiements multi-méthodes
    │   │   │   ├── Equipage.java                  # Membres d'équipage
    │   │   │   └── VolEquipage.java               # Affectation équipage aux vols
    │   │   │
    │   │   ├── 📁 entity/enums/                   # 🏷️ Énumérations (6 fichiers)
    │   │   │   ├── ClasseSiege.java               # ECONOMY, BUSINESS
    │   │   │   ├── StatutReservation.java         # CONFIRMEE, ANNULEE
    │   │   │   ├── StatutBillet.java              # EMIS, ANNULE
    │   │   │   ├── StatutPaiement.java            # EN_ATTENTE, VALIDE, REFUSE
    │   │   │   ├── TypeMethodePaiement.java       # CARTE, CASH, MOBILE_MONEY, VIREMENT
    │   │   │   └── RoleEquipage.java              # PILOTE, COPILOTE, HOTESSE, STEWARD
    │   │   │
    │   │   ├── 📁 repository/                     # 💾 Repositories (12 fichiers)
    │   │   │   ├── AvionRepository.java
    │   │   │   ├── SiegeRepository.java
    │   │   │   ├── VolRepository.java
    │   │   │   ├── SiegeVolRepository.java
    │   │   │   ├── ClientRepository.java
    │   │   │   ├── ReservationRepository.java
    │   │   │   ├── BilletRepository.java
    │   │   │   ├── PaiementRepository.java
    │   │   │   ├── MethodePaiementRepository.java
    │   │   │   ├── PaiementMethodeRepository.java
    │   │   │   ├── EquipageRepository.java
    │   │   │   └── VolEquipageRepository.java
    │   │   │
    │   │   ├── 📁 service/                        # ⚙️ Services (13 fichiers)
    │   │   │   ├── GenericService.java            # Interface générique CRUD
    │   │   │   ├── GenericServiceImpl.java        # Implémentation générique
    │   │   │   ├── AvionService.java              # ✨ Génération auto des sièges
    │   │   │   ├── SiegeService.java
    │   │   │   ├── VolService.java                # ✨ Génération auto des sièges-vol
    │   │   │   ├── SiegeVolService.java
    │   │   │   ├── ClientService.java
    │   │   │   ├── ReservationService.java        # ✨ Émission + Annulation billets
    │   │   │   ├── BilletService.java
    │   │   │   ├── PaiementService.java           # ✨ Paiements multi-méthodes
    │   │   │   ├── MethodePaiementService.java
    │   │   │   ├── EquipageService.java
    │   │   │   └── VolEquipageService.java
    │   │   │
    │   │   ├── 📁 specification/                  # 🔍 Filtres dynamiques (8 fichiers)
    │   │   │   ├── AvionSpecification.java
    │   │   │   ├── SiegeSpecification.java
    │   │   │   ├── VolSpecification.java
    │   │   │   ├── ClientSpecification.java
    │   │   │   ├── ReservationSpecification.java
    │   │   │   ├── BilletSpecification.java
    │   │   │   ├── PaiementSpecification.java
    │   │   │   └── EquipageSpecification.java
    │   │   │
    │   │   ├── 📁 controller/                     # 🎮 Controllers MVC (6 fichiers)
    │   │   │   ├── HomeController.java            # Page d'accueil
    │   │   │   ├── AvionController.java           # CRUD Avions
    │   │   │   ├── VolController.java             # CRUD Vols
    │   │   │   ├── ClientController.java          # CRUD Clients
    │   │   │   ├── ReservationController.java     # CRUD Réservations + Billets
    │   │   │   └── EquipageController.java        # CRUD Équipage
    │   │   │
    │   │   └── 📁 config/                         # ⚙️ Configuration
    │   │       └── DataInitializer.java           # Initialisation méthodes paiement
    │   │
    │   └── 📁 resources/
    │       ├── 📄 application.properties                    # Configuration développement
    │       ├── 📄 application-prod.properties.example       # Exemple config production
    │       │
    │       └── 📁 templates/                                # 🎨 Vues Thymeleaf
    │           ├── 📄 layout.html                           # Layout de base avec sidebar
    │           ├── 📄 index.html                            # Dashboard avec 6 modules
    │           │
    │           ├── 📁 avion/                                # Vues Avions
    │           │   ├── list.html                            # Liste avec filtres
    │           │   ├── form.html                            # Création/édition
    │           │   └── detail.html                          # Détails + liste sièges
    │           │
    │           ├── 📁 vol/                                  # Vues Vols
    │           │   ├── list.html                            # Liste avec filtres
    │           │   ├── form.html                            # Création/édition
    │           │   └── detail.html                          # Détails + statistiques
    │           │
    │           ├── 📁 client/                               # Vues Clients
    │           │   ├── list.html                            # Liste avec filtres
    │           │   ├── form.html                            # Création/édition
    │           │   └── detail.html                          # Détails + réservations
    │           │
    │           ├── 📁 reservation/                          # Vues Réservations
    │           │   ├── list.html                            # Liste avec filtres
    │           │   ├── form.html                            # Création
    │           │   └── detail.html                          # Détails + émission billets
    │           │
    │           └── 📁 equipage/                             # Vues Équipage
    │               ├── list.html                            # Liste avec filtres
    │               ├── form.html                            # Création/édition
    │               └── detail.html                          # Détails + vols assignés
    │
    └── 📁 test/
        └── 📁 java/com/airline/airline_app/
            └── 📄 AirlineAppApplicationTests.java
```

## 🎯 Points d'Entrée Principaux

### 🚀 Démarrage de l'Application

1. **Méthode Manuelle**
   ```bash
   mvn spring-boot:run
   ```

2. **Avec Script (Windows)**
   ```cmd
   start.bat
   ```

3. **Avec Script (Linux/Mac)**
   ```bash
   ./start.sh
   ```

### 🌐 URLs Principales

| URL | Description | Controller |
|-----|-------------|-----------|
| http://localhost:8081 | Dashboard | HomeController |
| http://localhost:8081/avions | Gestion Avions | AvionController |
| http://localhost:8081/vols | Gestion Vols | VolController |
| http://localhost:8081/clients | Gestion Clients | ClientController |
| http://localhost:8081/reservations | Gestion Réservations | ReservationController |
| http://localhost:8081/equipages | Gestion Équipage | EquipageController |

## 📊 Flux de Données

### 1️⃣ Création d'un Avion
```
User Input → AvionController 
          → AvionService.save(avion)
          → genererSieges() [Automatique]
          → SiegeRepository.saveAll()
          → Database
```

### 2️⃣ Création d'un Vol
```
User Input → VolController 
          → VolService.save(vol)
          → creerSiegesVol() [Automatique]
          → SiegeVolRepository.saveAll()
          → Database
```

### 3️⃣ Création d'une Réservation avec Billet
```
User Input (Réservation) → ReservationController
                         → ReservationService.save()
                         → Database

User Input (Billet) → ReservationController.creerBillet()
                    → ReservationService.creerBillet()
                    → Validation siège disponible
                    → Création Billet
                    → Mise à jour SiegeVol (occupe=true)
                    → Database
```

### 4️⃣ Annulation d'une Réservation
```
User Click → ReservationController.annuler()
          → ReservationService.annulerReservation()
          → Mise à jour statut réservation (ANNULEE)
          → Mise à jour tous billets (ANNULE)
          → Libération tous sièges (occupe=false)
          → Database
```

### 5️⃣ Filtrage de Liste
```
User Input Filters → Controller (ex: AvionController)
                  → Build Specification
                  → Repository.findAll(spec)
                  → List<Entity>
                  → View avec résultats filtrés
```

## 🔑 Concepts Clés

### Architecture MVC

```
┌─────────────┐
│   CLIENT    │ (Navigateur)
└──────┬──────┘
       │ HTTP Request
       ▼
┌─────────────┐
│ CONTROLLER  │ (@Controller)
└──────┬──────┘
       │ Appelle
       ▼
┌─────────────┐
│   SERVICE   │ (Logique métier)
└──────┬──────┘
       │ Utilise
       ▼
┌─────────────┐
│ REPOSITORY  │ (Accès données)
└──────┬──────┘
       │ JPA/Hibernate
       ▼
┌─────────────┐
│  DATABASE   │ (PostgreSQL)
└─────────────┘
```

### Pattern Service Générique

```java
GenericService<T, ID>           // Interface
       ↑
       │ implements
       │
GenericServiceImpl<T, ID>       // Implémentation abstraite
       ↑
       │ extends
       │
AvionService                    // Service spécifique
```

### JPA Specification Pattern

```java
Specification<Avion> = 
    AvionSpecification.filterBy(code, modele, capaciteMin, capaciteMax)
    
    ↓ Transformé en
    
WHERE LOWER(code) LIKE '%af%'
  AND LOWER(modele) LIKE '%boeing%'
  AND capacite >= 100
  AND capacite <= 200
```

## 📦 Dépendances Principales

```xml
spring-boot-starter-web         → Controllers REST/MVC
spring-boot-starter-data-jpa    → Repositories + JPA
spring-boot-starter-thymeleaf   → Templates
postgresql                      → Driver PostgreSQL
lombok                          → Réduction boilerplate
spring-boot-starter-validation  → Validation (future)
```

## 🎓 Fonctionnalités Automatiques

### ✨ Génération Automatique

1. **Sièges d'Avion** (AvionService)
   - 70% Economy, 30% Business
   - Numérotation: 1E, 2E, ..., nE, 1B, 2B, ..., nB

2. **Sièges-Vol** (VolService)
   - Crée un SiegeVol pour chaque siège de l'avion
   - Tous avec occupe=false au départ

3. **Méthodes de Paiement** (DataInitializer)
   - Initialise les 4 méthodes au démarrage
   - Exécuté une seule fois

### 🔄 Logique Métier

1. **Émission de Billet** (ReservationService)
   - Vérifie disponibilité du siège
   - Crée le billet
   - Marque le siège comme occupé

2. **Annulation** (ReservationService)
   - Annule la réservation
   - Annule tous les billets
   - Libère tous les sièges

3. **Paiements Multi-Méthodes** (PaiementService)
   - Accepte plusieurs méthodes pour 1 paiement
   - Répartition avec montants différents

## 📚 Guides Disponibles

| Fichier | Objectif | Audience |
|---------|----------|----------|
| README.md | Documentation complète | Tous |
| QUICKSTART.md | Démarrage rapide | Débutants |
| TEST_DATA.md | Données de test | Testeurs |
| DEPLOYMENT_CHECKLIST.md | Déploiement | DevOps |
| PROJECT_STRUCTURE.md | Architecture | Développeurs |

## 🎯 Prochaines Étapes

Voir [DEPLOYMENT_CHECKLIST.md](DEPLOYMENT_CHECKLIST.md) pour :
- ✅ Fonctionnalités manquantes
- 🔐 Sécurité à ajouter
- 📈 Optimisations possibles
- 🐳 Containerisation Docker
- 🚀 CI/CD

---

**Dernière mise à jour**: Janvier 2024  
**Version**: 1.0.0  
**Technologies**: Spring Boot 4.0.1 + PostgreSQL + Thymeleaf
