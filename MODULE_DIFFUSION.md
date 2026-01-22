# Module de Diffusion

Ce module gère la diffusion des vols programmés auprès de différentes sociétés partenaires avec suivi automatique des modifications de configuration et calcul du chiffre d'affaires.

## Fonctionnalités principales

### 1. Gestion des Diffusions
- Enregistrement des diffusions vers les sociétés partenaires
- Association des diffusions aux vols programmés
- Configuration des coûts de diffusion

### 2. Historisation Automatique
- **Trigger BDD**: Insertion automatique dans `historique_configuration_cout` lors de modifications
- **Listener JPA**: Enregistrement côté application Java des changements
- Double sécurité pour garantir la traçabilité

### 3. Calcul du Chiffre d'Affaires
- Fonction `getChiffreAffaireDiffusion(mois, annee)` pour calculer le CA par période
- Formule: `Σ(prix_configuration × nombre_diffusions)`
- Accessible via endpoint: `/chiffre-affaire/diffusion/{mois}/{annee}`

## Tables créées

### 1. **Societe**
Représente les sociétés partenaires (agences de voyage, plateformes de réservation, etc.)
- `id_societe` (BIGSERIAL) : Identifiant unique
- `libelle` (VARCHAR) : Nom de la société

### 2. **DiffusionSociete**
Enregistre les diffusions envoyées aux sociétés
- `id_diffusion_societe` (BIGSERIAL) : Identifiant unique
- `id_societe` (BIGINT) : Référence vers la société
- `nombre` (INTEGER) : Nombre d'éléments diffusés
- `date` (TIMESTAMP) : Date et heure de la diffusion

### 3. **ConfigurationCoutDiffusion**
Configure les coûts associés aux diffusions
- `id_configuration_cout_diffusion` (BIGSERIAL) : Identifiant unique
- `id_diffusion` (BIGINT) : Référence vers une diffusion
- `prix` (DOUBLE) : Prix de la diffusion

### 4. **HistoriqueConfigurationCout**
Historise les changements de configuration de coûts
- `id_historique_configuration_cout` (BIGSERIAL) : Identifiant unique
- `prix` (DOUBLE) : Prix configuré
- `status` (VARCHAR) : Statut (ACTIF, INACTIF, etc.)
- `date` (TIMESTAMP) : Date de modification

### 5. **DiffusionVolProgramme**
Lie les diffusions aux vols programmés
- `id_diffusion_vol_programme` (BIGSERIAL) : Identifiant unique
- `id_diffusion_societe` (BIGINT) : Référence vers la diffusion
- `nombre` (INTEGER) : Nombre de places diffusées
- `id_vol_programme` (BIGINT) : Référence vers le vol programmé

## Entités Java créées

Les entités suivantes ont été créées dans le package `com.airline.airline_app.entity` :
- `Societe.java`
- `DiffusionSociete.java`
- `ConfigurationCoutDiffusion.java`
- `HistoriqueConfigurationCout.java`
- `DiffusionVolProgramme.java`

## Repositories créés

Les repositories suivants ont été créés dans le package `com.airline.airline_app.repository` :
- `SocieteRepository.java`
- `DiffusionSocieteRepository.java`
- `ConfigurationCoutDiffusionRepository.java`
- `HistoriqueConfigurationCoutRepository.java`
- `DiffusionVolProgrammeRepository.java`

## Services créés

Les services suivants ont été créés dans le package `com.airline.airline_app.service` :
- `SocieteService.java`
- `DiffusionSocieteService.java`
- `ConfigurationCoutDiffusionService.java`
- `HistoriqueConfigurationCoutService.java`
- `DiffusionVolProgrammeService.java`

## Listeners créés

- **ConfigurationCoutDiffusionListener.java**: Enregistre automatiquement dans l'historique lors de la création ou modification d'une configuration de coût

## Controllers

- **ChiffreAffaireController.java**: Ajout de l'endpoint `/chiffre-affaire/diffusion/{mois}/{annee}`

## Installation

1. Exécuter le script SQL :
```bash
psql -U votre_utilisateur -d votre_base_de_donnees -f database/diffusion_module.sql
```

2. Ou bien laisser JPA créer les tables automatiquement en configurant dans `application.properties` :
```properties
spring.jpa.hibernate.ddl-auto=update
```

## Relations entre les tables

```
Societe (1) ----< (N) DiffusionSociete (1) ----< (N) DiffusionVolProgramme >---- (1) VolProgramme
                                                                               
ConfigurationCoutDiffusion (référence idDiffusion)

HistoriqueConfigurationCout (table indépendante pour l'historique)
```

## Utilisation

### Créer une nouvelle société
```java
Societe societe = new Societe();
societe.setLibelle("Nouvelle Agence");
societeService.save(societe);
```

### Créer une diffusion
```java
DiffusionSociete diffusion = new DiffusionSociete();
diffusion.setSociete(societe);
diffusion.setNombre(100);
diffusion.setDate(LocalDateTime.now());
diffusionSocieteService.save(diffusion);
```

### Configurer le coût de diffusion
```java
ConfigurationCoutDiffusion config = new ConfigurationCoutDiffusion();
config.setIdDiffusion(diffusion.getIdDiffusionSociete());
config.setPrix(15.50);
configurationCoutDiffusionService.save(config);
// ⚠️ L'insertion dans historique_configuration_cout se fera automatiquement!
```

### Lier une diffusion à un vol programmé
```java
DiffusionVolProgramme diffusionVol = new DiffusionVolProgramme();
diffusionVol.setDiffusionSociete(diffusion);
diffusionVol.setVolProgramme(volProgramme);
diffusionVol.setNombre(50);
diffusionVolProgrammeService.save(diffusionVol);
```

### Calculer le chiffre d'affaires des diffusions
```java
// Pour janvier 2026
ChiffreAffaireDTO ca = chiffreAffaireService.getChiffreAffaireDiffusion(1, 2026);
System.out.println("CA Diffusion: " + ca.getMontantTotal());
System.out.println("Nombre de diffusions: " + ca.getNombreBillets());
```

### Accès via URL
```
http://localhost:8080/chiffre-affaire/diffusion/1/2026
```

## Mécanisme d'Historisation

### Double Protection

1. **Trigger PostgreSQL** (recommandé pour la production)
   - S'exécute au niveau de la base de données
   - Garantit l'historisation même en cas d'accès direct à la BDD
   - Fonction: `fn_insert_historique_configuration_cout()`

2. **Listener JPA** (ConfigurationCoutDiffusionListener)
   - S'exécute au niveau de l'application Java
   - Utilise `@PostPersist` et `@PostUpdate`
   - Permet une logique métier plus complexe si nécessaire

### Statuts dans l'historique
- `CREATION`: Nouvelle configuration créée
- `MODIFICATION`: Configuration mise à jour
- `ACTIF`: Configuration active (manuel)
- `INACTIF`: Configuration inactive (manuel)

## Tests

Exécutez le script de test pour valider le fonctionnement :
```bash
psql -U votre_utilisateur -d votre_base_de_donnees -f database/test_diffusion_module.sql
```

## Formule de Calcul du CA

```
CA Diffusion = Σ (prix_configuration × nombre_diffusions)
```

Pour chaque diffusion dans la période (mois/année) :
- Récupère le prix depuis `configuration_cout_diffusion`
- Multiplie par le `nombre` dans `diffusion_vol_programme`
- Additionne tous les montants
