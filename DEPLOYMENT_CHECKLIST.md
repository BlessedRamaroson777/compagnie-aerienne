# ✅ Check-list de Déploiement et Production

## 📋 Avant le Déploiement

### 1. Configuration

- [ ] Créer un fichier `application-prod.properties` pour la production
- [ ] Configurer les identifiants de base de données de production
- [ ] Changer `spring.jpa.hibernate.ddl-auto` de `update` à `validate` en production
- [ ] Désactiver `spring.jpa.show-sql` en production
- [ ] Configurer le niveau de log approprié (INFO ou WARN)
- [ ] Activer le cache Thymeleaf (`spring.thymeleaf.cache=true`)

### 2. Sécurité

- [ ] Ajouter Spring Security
- [ ] Implémenter l'authentification et l'autorisation
- [ ] Protéger les endpoints sensibles
- [ ] Ajouter la protection CSRF
- [ ] Configurer HTTPS
- [ ] Changer les mots de passe par défaut
- [ ] Ajouter la validation des entrées utilisateur
- [ ] Protéger contre les injections SQL

### 3. Base de Données

- [ ] Créer des sauvegardes régulières
- [ ] Configurer un pool de connexions optimal
- [ ] Ajouter des index sur les colonnes fréquemment recherchées
- [ ] Optimiser les requêtes N+1 avec `@EntityGraph`
- [ ] Migrer vers Flyway ou Liquibase pour la gestion des migrations

### 4. Tests

- [ ] Écrire des tests unitaires pour les services
- [ ] Écrire des tests d'intégration pour les controllers
- [ ] Tester tous les scénarios de réservation
- [ ] Tester l'annulation de réservations
- [ ] Tester la génération automatique des sièges
- [ ] Tester les filtres avec différents critères
- [ ] Tests de performance avec JMeter ou Gatling

## 🚀 Optimisations Recommandées

### Performance

- [ ] Ajouter la pagination sur toutes les listes
```java
Page<Avion> findAll(Specification<Avion> spec, Pageable pageable);
```

- [ ] Ajouter le cache avec Spring Cache
```java
@Cacheable("avions")
public List<Avion> findAll() { ... }
```

- [ ] Optimiser les relations JPA avec `FetchType.LAZY`
- [ ] Ajouter des index sur les colonnes de recherche

### Validation

- [ ] Ajouter `@Valid` sur les entités dans les controllers
- [ ] Ajouter des validations sur les entités :
```java
@NotBlank(message = "Le code avion est obligatoire")
private String codeAvion;

@Min(value = 1, message = "La capacité doit être au moins 1")
private Integer capaciteTotale;

@Email(message = "Email invalide")
private String email;
```

### Gestion des Erreurs

- [ ] Créer un `GlobalExceptionHandler` avec `@ControllerAdvice`
- [ ] Créer des pages d'erreur personnalisées (404, 500)
- [ ] Logger les erreurs de manière appropriée
- [ ] Retourner des messages d'erreur clairs à l'utilisateur

## 📊 Fonctionnalités Supplémentaires

### Priorité Haute

- [ ] **Page de Paiement**
  - Interface pour effectuer des paiements
  - Support multi-méthodes
  - Historique des paiements

- [ ] **Gestion des VolEquipage**
  - Interface pour assigner l'équipage aux vols
  - Vérification des disponibilités

- [ ] **Rapports et Statistiques**
  - Dashboard avec graphiques
  - Rapport de réservations par vol
  - Statistiques de revenus

### Priorité Moyenne

- [ ] **Recherche Avancée**
  - Recherche de vols disponibles
  - Affichage des sièges libres en temps réel

- [ ] **Notifications**
  - Email de confirmation de réservation
  - Email d'annulation
  - Rappels de vol

- [ ] **Export de Données**
  - Export PDF des billets
  - Export Excel des réservations
  - Rapport CSV

### Priorité Basse

- [ ] **API REST**
  - Créer des endpoints REST pour intégration mobile
  - Documentation avec Swagger/OpenAPI

- [ ] **Internationalisation**
  - Support multi-langues (FR, EN)
  - Messages en français et anglais

- [ ] **Historique**
  - Audit trail des modifications
  - Historique des actions utilisateur

## 🔐 Sécurité Avancée

### Spring Security Configuration

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/css/**", "/js/**").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .permitAll()
            )
            .logout(logout -> logout.permitAll());
        
        return http.build();
    }
}
```

### Rôles Suggérés

- `ADMIN` : Accès complet
- `MANAGER` : Gestion des vols et avions
- `AGENT` : Gestion des réservations
- `VIEWER` : Consultation uniquement

## 📈 Monitoring

- [ ] Ajouter Spring Boot Actuator
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

- [ ] Configurer les endpoints de health check
- [ ] Surveiller les métriques (CPU, RAM, connexions DB)
- [ ] Logger les actions importantes
- [ ] Configurer des alertes pour les erreurs

## 🐳 Containerisation (Docker)

### Dockerfile

```dockerfile
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY target/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### docker-compose.yml

```yaml
version: '3.8'
services:
  postgres:
    image: postgres:15
    environment:
      POSTGRES_DB: airline_db
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: postgres
    ports:
      - "5432:5432"
  
  app:
    build: .
    ports:
      - "8081:8081"
    depends_on:
      - postgres
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/airline_db
```

## 📝 Documentation

- [ ] Documenter l'API (si créée)
- [ ] Créer un guide utilisateur
- [ ] Documenter les procédures de déploiement
- [ ] Créer un guide de troubleshooting
- [ ] Documenter la base de données (schéma ER)

## 🎯 CI/CD

- [ ] Configurer GitHub Actions / GitLab CI
- [ ] Tests automatiques sur chaque commit
- [ ] Déploiement automatique en staging
- [ ] Déploiement manuel en production

### Exemple GitHub Actions

```yaml
name: Build and Test

on: [push, pull_request]

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      - name: Set up JDK 17
        uses: actions/setup-java@v2
        with:
          java-version: '17'
      - name: Build with Maven
        run: mvn clean install
      - name: Run tests
        run: mvn test
```

## ✅ Check-list Finale

Avant de mettre en production :

- [ ] Tous les tests passent
- [ ] Les logs n'affichent pas d'erreurs
- [ ] La base de données est sauvegardée
- [ ] Les identifiants de production sont configurés
- [ ] Les variables d'environnement sensibles sont sécurisées
- [ ] Le cache Thymeleaf est activé
- [ ] Spring Security est configuré
- [ ] Les endpoints sont protégés
- [ ] Les erreurs sont gérées correctement
- [ ] Le monitoring est en place
- [ ] La documentation est à jour
- [ ] Les sauvegardes automatiques sont configurées

---

**Note**: Cette check-list est un guide. Adaptez-la en fonction de vos besoins spécifiques et de votre infrastructure.
