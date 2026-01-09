# 🚀 Guide de Démarrage Rapide

## Prérequis

Avant de commencer, assurez-vous d'avoir installé :

- ✅ **Java 17** ou supérieur ([Télécharger](https://www.oracle.com/java/technologies/downloads/))
- ✅ **Maven 3.6+** ([Télécharger](https://maven.apache.org/download.cgi))
- ✅ **PostgreSQL 12+** ([Télécharger](https://www.postgresql.org/download/))

## Installation en 5 minutes ⚡

### 1. Cloner ou télécharger le projet

```bash
cd f:\Studies\S5\Mme Baovola\airline-app
```

### 2. Configurer PostgreSQL

#### Option A : Utilisation automatique (Windows)
```cmd
start.bat
```

#### Option B : Configuration manuelle

**Démarrer PostgreSQL**, puis créer la base de données :

```sql
CREATE DATABASE airline_db;
```

**Ou avec psql en ligne de commande :**
```bash
psql -U postgres -c "CREATE DATABASE airline_db;"
```

### 3. Configurer les identifiants (si nécessaire)

Si votre utilisateur PostgreSQL n'est pas `postgres` ou le mot de passe n'est pas `postgres`, modifiez le fichier :

📄 `src/main/resources/application.properties`

```properties
spring.datasource.username=VOTRE_USERNAME
spring.datasource.password=VOTRE_PASSWORD
```

### 4. Lancer l'application

#### Option A : Avec Maven
```bash
mvn spring-boot:run
```

#### Option B : Avec le script de démarrage
**Windows:**
```cmd
start.bat
```

**Linux/Mac:**
```bash
chmod +x start.sh
./start.sh
```

### 5. Accéder à l'application

Ouvrez votre navigateur et allez sur :

🌐 **http://localhost:8080**

## ✅ Vérification de l'installation

L'application devrait :
1. ✅ Créer automatiquement toutes les tables dans la base de données
2. ✅ Initialiser les méthodes de paiement (Carte, Cash, Mobile Money, Virement)
3. ✅ Afficher la page d'accueil avec 6 modules

## 🎯 Premier test

1. **Créer un avion**
   - Aller sur "Avions" → "Nouveau"
   - Code: `AF001`, Modèle: `Boeing 737`, Capacité: `150`
   - Les sièges seront créés automatiquement (70% Economy, 30% Business)

2. **Créer un vol**
   - Aller sur "Vols" → "Nouveau"
   - Numéro: `AF100`, Départ: `Paris`, Arrivée: `New York`
   - Sélectionner l'avion créé
   - Les sièges du vol seront créés automatiquement

3. **Créer un client**
   - Aller sur "Clients" → "Nouveau"
   - Remplir le formulaire

4. **Créer une réservation**
   - Aller sur "Réservations" → "Nouveau"
   - Sélectionner le client et le vol
   - Voir les détails pour émettre des billets

## 🐛 Résolution des problèmes

### Erreur : "Cannot connect to database"
- ✅ Vérifiez que PostgreSQL est démarré
- ✅ Vérifiez les identifiants dans `application.properties`
- ✅ Vérifiez que la base `airline_db` existe

### Erreur : "Port 8080 already in use"
Modifiez le port dans `application.properties` :
```properties
server.port=8081
```

### Erreur de compilation Maven
```bash
mvn clean install -U
```

### Les tables ne sont pas créées
Vérifiez dans `application.properties` :
```properties
spring.jpa.hibernate.ddl-auto=update
```

## 📚 Documentation complète

Pour plus de détails, consultez le fichier [README.md](README.md)

## 🎓 Fonctionnalités principales

| Module | Fonctionnalités |
|--------|----------------|
| ✈️ **Avions** | CRUD + Génération automatique des sièges |
| 🛫 **Vols** | CRUD + Génération automatique des sièges-vol |
| 👤 **Clients** | CRUD + Historique des réservations |
| 📋 **Réservations** | CRUD + Émission de billets + Annulation |
| 👨‍✈️ **Équipage** | CRUD + Affectation aux vols |
| 💳 **Paiements** | Paiements multi-méthodes |

## 🔗 URLs principales

- 🏠 Accueil : http://localhost:8080
- ✈️ Avions : http://localhost:8080/avions
- 🛫 Vols : http://localhost:8080/vols
- 👤 Clients : http://localhost:8080/clients
- 📋 Réservations : http://localhost:8080/reservations
- 👨‍✈️ Équipage : http://localhost:8080/equipages

## 💡 Astuces

- Les filtres sont **optionnels** - laissez vide pour voir tout
- Utilisez le bouton 👁️ pour voir les détails
- Les sièges sont générés automatiquement (pas besoin de les créer)
- Les réservations peuvent être annulées (libère les sièges)
- Les paiements peuvent utiliser plusieurs méthodes

---

**Besoin d'aide ?** Consultez le [README complet](README.md) ou vérifiez les logs de l'application dans la console.
