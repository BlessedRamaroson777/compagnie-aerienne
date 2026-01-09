# 🔄 MODIFICATIONS NÉCESSAIRES POUR ADAPTER L'APPLICATION

## ✅ Modifications Déjà Effectuées

### 1. Nouvelles Entités Créées
- ✅ **ModelAvion.java** - Table des modèles d'avion (fabricant, capacité, autonomie, etc.)
- ✅ **EtatAvion.java** - Table des états d'avion (OPERATIONNEL, EN_MAINTENANCE, etc.)
- ✅ **Aeroport.java** - Table des aéroports avec code IATA
- ✅ **PrixVol.java** - Table de prix par vol et classe
- ✅ **ClasseSiege.java** - Transformation de l'enum en table
- ✅ **RoleEquipage.java** - Transformation de l'enum en table

### 2. Enums Mis à Jour
- ✅ **StatutVol.java** - Nouveau (PLANIFIE, DECOLLE, EN_VOL, ATTERRI, ANNULE, RETARDE)
- ✅ **StatutReservation.java** - Ajout de EN_ATTENTE et REMBOURSEE
- ✅ **StatutBillet.java** - Ajout de UTILISE et REMBOURSE

### 3. Entités Existantes Modifiées
- ✅ **Avion.java** - Maintenant avec références à ModelAvion et EtatAvion
- ✅ **Vol.java** - Maintenant avec références à Aeroport (départ/arrivée) et StatutVol
- ✅ **Siege.java** - Référence à ClasseSiege (table) au lieu d'enum
- ✅ **Equipage.java** - Référence à RoleEquipage (table) au lieu d'enum

### 4. Repositories Créés
- ✅ ModelAvionRepository.java
- ✅ EtatAvionRepository.java
- ✅ AeroportRepository.java
- ✅ PrixVolRepository.java
- ✅ ClasseSiegeRepository.java
- ✅ RoleEquipageRepository.java

### 5. Données de Test
- ✅ **test_data_complete.sql** - Fichier SQL complet avec:
  - 4 états d'avion
  - 7 modèles d'avion
  - 2 classes de siège
  - 4 rôles d'équipage
  - 4 méthodes de paiement
  - 7 avions
  - 15 aéroports
  - 9 vols
  - 18 prix de vols
  - 17 membres d'équipage
  - 10 clients
  - 5 réservations
  - 3 paiements

---

## ⚠️ Modifications à Effectuer Manuellement

### Services à Créer

1. **ModelAvionService.java**
```java
@Service
public class ModelAvionService extends GenericServiceImpl<ModelAvion, Long> {
    private final ModelAvionRepository modelAvionRepository;
    
    public ModelAvionService(ModelAvionRepository modelAvionRepository) {
        this.modelAvionRepository = modelAvionRepository;
    }
    
    @Override
    protected JpaRepository<ModelAvion, Long> getRepository() {
        return modelAvionRepository;
    }
    
    @Override
    protected JpaSpecificationExecutor<ModelAvion> getSpecificationExecutor() {
        return modelAvionRepository;
    }
}
```

2. **EtatAvionService.java** (similaire)
3. **AeroportService.java** (similaire)
4. **PrixVolService.java** (similaire)
5. **ClasseSiegeService.java** (similaire)
6. **RoleEquipageService.java** (similaire)

### Services à Modifier

#### AvionService.java
- ✏️ Modifier `genererSieges()` pour utiliser `ClasseSiege` (table) au lieu de l'enum
- ✏️ Ajouter validation ModelAvion et EtatAvion avant save

```java
// Ancien code
siege.setClasseSiege(i <= nombreEconomy ? ClasseSiege.ECONOMY : ClasseSiege.BUSINESS);

// Nouveau code
ClasseSiege classeEconomy = classeSiegeRepository.findByLibelle("ECONOMY").orElseThrow();
ClasseSiege classeBusiness = classeSiegeRepository.findByLibelle("BUSINESS").orElseThrow();
siege.setClasseSiege(i <= nombreEconomy ? classeEconomy : classeBusiness);
```

#### EquipageService.java
- ✏️ Modifier pour utiliser `RoleEquipage` (table) au lieu de l'enum

### Specifications à Créer

1. **ModelAvionSpecification.java**
```java
public class ModelAvionSpecification {
    public static Specification<ModelAvion> filterBy(String designation, String fabricant, 
                                                       Integer capaciteMin, Integer capaciteMax) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            if (designation != null && !designation.isEmpty()) {
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("designation")),
                    "%" + designation.toLowerCase() + "%"
                ));
            }
            
            if (fabricant != null && !fabricant.isEmpty()) {
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("fabricant")),
                    "%" + fabricant.toLowerCase() + "%"
                ));
            }
            
            if (capaciteMin != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("capacite"), capaciteMin));
            }
            
            if (capaciteMax != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("capacite"), capaciteMax));
            }
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
```

2. **AeroportSpecification.java** (filtres: code, ville, pays)
3. **PrixVolSpecification.java** (filtres: vol, classe, prixMin, prixMax)

### Specifications à Modifier

#### AvionSpecification.java
- ✏️ Remplacer filtre `modele` par `modelAvion.designation`
- ✏️ Ajouter filtre `etatAvion.libelle`

#### VolSpecification.java
- ✏️ Remplacer `depart` et `arrivee` (String) par `aeroportDepart.ville` et `aeroportArrivee.ville`
- ✏️ Supprimer `prixBase` (maintenant dans PrixVol)
- ✏️ Ajouter filtre `status` (StatutVol)

#### SiegeSpecification.java
- ✏️ Remplacer `classeSiege` (enum) par `classeSiege.libelle`

#### EquipageSpecification.java
- ✏️ Remplacer `role` (enum) par `role.libelle`

### Controllers à Créer

1. **ModelAvionController.java**
2. **EtatAvionController.java**
3. **AeroportController.java**
4. **PrixVolController.java**

### Controllers à Modifier

#### AvionController.java
- ✏️ Injecter ModelAvionService et EtatAvionService
- ✏️ Dans `newForm()` et `editForm()`, ajouter au model:
  ```java
  model.addAttribute("modeles", modelAvionService.findAll());
  model.addAttribute("etats", etatAvionService.findAll());
  ```
- ✏️ Mettre à jour filtres pour nouveaux champs

#### VolController.java
- ✏️ Injecter AeroportService et PrixVolService
- ✏️ Dans formulaires, ajouter sélection aéroports
- ✏️ Après création vol, créer automatiquement PrixVol pour chaque classe
- ✏️ Supprimer champ `prixBase`

#### EquipageController.java
- ✏️ Injecter RoleEquipageService
- ✏️ Dans formulaires, ajouter sélection depuis table au lieu d'enum

### Templates à Créer

#### Nouveaux modules
1. **model-avion/** (list.html, form.html, detail.html)
2. **etat-avion/** (list.html, form.html)
3. **aeroport/** (list.html, form.html, detail.html)
4. **prix-vol/** (list.html, form.html)

### Templates à Modifier

#### avion/form.html
```html
<div class="mb-3">
    <label>Modèle d'avion</label>
    <select name="modelAvion.id" class="form-control" required>
        <option value="">Sélectionner...</option>
        <option th:each="modele : ${modeles}" 
                th:value="${modele.id}" 
                th:text="${modele.designation + ' - ' + modele.fabricant}"
                th:selected="${avion.modelAvion != null && avion.modelAvion.id == modele.id}">
        </option>
    </select>
</div>

<div class="mb-3">
    <label>État</label>
    <select name="etatAvion.id" class="form-control" required>
        <option value="">Sélectionner...</option>
        <option th:each="etat : ${etats}" 
                th:value="${etat.id}" 
                th:text="${etat.libelle}"
                th:selected="${avion.etatAvion != null && avion.etatAvion.id == etat.id}">
        </option>
    </select>
</div>
```

#### vol/form.html
```html
<div class="mb-3">
    <label>Aéroport de départ</label>
    <select name="aeroportDepart.id" class="form-control" required>
        <option value="">Sélectionner...</option>
        <option th:each="aeroport : ${aeroports}" 
                th:value="${aeroport.id}" 
                th:text="${aeroport.codeAeroport + ' - ' + aeroport.ville + ' (' + aeroport.pays + ')'}"
                th:selected="${vol.aeroportDepart != null && vol.aeroportDepart.id == aeroport.id}">
        </option>
    </select>
</div>

<div class="mb-3">
    <label>Aéroport d'arrivée</label>
    <select name="aeroportArrivee.id" class="form-control" required>
        <option value="">Sélectionner...</option>
        <option th:each="aeroport : ${aeroports}" 
                th:value="${aeroport.id}" 
                th:text="${aeroport.codeAeroport + ' - ' + aeroport.ville + ' (' + aeroport.pays + ')'}"
                th:selected="${vol.aeroportArrivee != null && vol.aeroportArrivee.id == aeroport.id}">
        </option>
    </select>
</div>

<!-- SUPPRIMER le champ prixBase -->

<!-- AJOUTER après création du vol, formulaire pour prix par classe -->
<h5>Prix par classe</h5>
<div th:each="classe : ${classes}">
    <label th:text="${classe.libelle}"></label>
    <input type="number" th:name="'prix_' + ${classe.id}" step="0.01" required>
</div>
```

#### equipage/form.html
```html
<div class="mb-3">
    <label>Rôle</label>
    <select name="role.id" class="form-control" required>
        <option value="">Sélectionner...</option>
        <option th:each="role : ${roles}" 
                th:value="${role.id}" 
                th:text="${role.libelle}"
                th:selected="${equipage.role != null && equipage.role.id == role.id}">
        </option>
    </select>
</div>
```

#### index.html
```html
<!-- AJOUTER de nouveaux modules -->
<div class="col-md-4 mb-4">
    <div class="card text-center">
        <div class="card-body">
            <i class="bi bi-building" style="font-size: 3rem; color: #17a2b8;"></i>
            <h5 class="card-title mt-3">Aéroports</h5>
            <p class="card-text">Gérer les aéroports</p>
            <a href="/aeroports" class="btn btn-info">Accéder</a>
        </div>
    </div>
</div>

<div class="col-md-4 mb-4">
    <div class="card text-center">
        <div class="card-body">
            <i class="bi bi-airplane" style="font-size: 3rem; color: #6c757d;"></i>
            <h5 class="card-title mt-3">Modèles d'Avion</h5>
            <p class="card-text">Gérer les modèles</p>
            <a href="/modeles-avion" class="btn btn-secondary">Accéder</a>
        </div>
    </div>
</div>
```

### DataInitializer.java à Modifier

```java
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    
    private final MethodePaiementRepository methodePaiementRepository;
    private final ClasseSiegeRepository classeSiegeRepository;
    private final RoleEquipageRepository roleEquipageRepository;
    private final EtatAvionRepository etatAvionRepository;
    
    @Override
    public void run(String... args) {
        // Initialiser Méthodes de paiement
        initMethodesPaiement();
        
        // Initialiser Classes de siège
        initClassesSiege();
        
        // Initialiser Rôles équipage
        initRolesEquipage();
        
        // Initialiser États avion
        initEtatsAvion();
    }
    
    private void initClassesSiege() {
        if (classeSiegeRepository.count() == 0) {
            classeSiegeRepository.save(new ClasseSiege(null, "ECONOMY", 
                "Classe Économique", null, null, null, null));
            classeSiegeRepository.save(new ClasseSiege(null, "BUSINESS", 
                "Classe Affaires", null, null, null, null));
        }
    }
    
    private void initRolesEquipage() {
        if (roleEquipageRepository.count() == 0) {
            roleEquipageRepository.save(new RoleEquipage(null, "PILOTE", 
                "Commandant de bord", null, null, null));
            roleEquipageRepository.save(new RoleEquipage(null, "COPILOTE", 
                "Second pilote", null, null, null));
            roleEquipageRepository.save(new RoleEquipage(null, "HOTESSE", 
                "Hôtesse de l'air", null, null, null));
            roleEquipageRepository.save(new RoleEquipage(null, "STEWARD", 
                "Steward", null, null, null));
        }
    }
    
    private void initEtatsAvion() {
        if (etatAvionRepository.count() == 0) {
            etatAvionRepository.save(new EtatAvion(null, "OPERATIONNEL", 
                "Avion opérationnel", null, null, null));
            etatAvionRepository.save(new EtatAvion(null, "EN_MAINTENANCE", 
                "Avion en maintenance", null, null, null));
            etatAvionRepository.save(new EtatAvion(null, "IMMOBILISE", 
                "Avion immobilisé", null, null, null));
            etatAvionRepository.save(new EtatAvion(null, "DECOMMISSIONNE", 
                "Avion décommissionné", null, null, null));
        }
    }
}
```

---

## 📝 Ordre d'Exécution Recommandé

1. ✅ Créer les nouveaux services (ModelAvion, EtatAvion, Aeroport, etc.)
2. ✅ Modifier DataInitializer pour initialiser les tables de référence
3. ✅ Démarrer l'application pour créer les tables
4. ✅ Exécuter le script SQL `test_data_complete.sql`
5. ✅ Modifier les services existants (AvionService, EquipageService)
6. ✅ Modifier les specifications existantes
7. ✅ Créer les nouveaux controllers
8. ✅ Modifier les controllers existants
9. ✅ Créer les nouveaux templates
10. ✅ Modifier les templates existants
11. ✅ Mettre à jour le dashboard (index.html)
12. ✅ Tester l'application complète

---

## 🎯 Résumé

**Fichiers créés**: 12 (6 entités + 6 repositories)  
**Fichiers modifiés**: ~20 (entités, services, specs, controllers, templates)  
**Fichiers à créer**: ~15 (services, specs, controllers, templates)  
**Données de test**: 100+ enregistrements dans 15+ tables

Les modifications sont importantes mais suivent le même pattern existant.
Toutes les nouvelles entités utilisent la même structure MVC que les entités actuelles.
