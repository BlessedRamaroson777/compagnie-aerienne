package com.airline.airline_app.config;

import com.airline.airline_app.entity.ClasseSiege;
import com.airline.airline_app.entity.EtatAvion;
import com.airline.airline_app.entity.MethodePaiement;
import com.airline.airline_app.entity.RoleEquipage;
import com.airline.airline_app.entity.enums.TypeMethodePaiement;
import com.airline.airline_app.repository.ClasseSiegeRepository;
import com.airline.airline_app.repository.EtatAvionRepository;
import com.airline.airline_app.repository.MethodePaiementRepository;
import com.airline.airline_app.repository.RoleEquipageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {
    
    private final MethodePaiementRepository methodePaiementRepository;
    private final ClasseSiegeRepository classeSiegeRepository;
    private final RoleEquipageRepository roleEquipageRepository;
    private final EtatAvionRepository etatAvionRepository;
    
    @Override
    public void run(String... args) {
        initMethodesPaiement();
        initClasseSiege();
        initRoleEquipage();
        initEtatAvion();
    }
    
    private void initMethodesPaiement() {
        // Initialiser les méthodes de paiement si elles n'existent pas
        if (methodePaiementRepository.count() == 0) {
            log.info("Initialisation des méthodes de paiement...");
            for (TypeMethodePaiement type : TypeMethodePaiement.values()) {
                if (methodePaiementRepository.findByLibelle(type).isEmpty()) {
                    MethodePaiement methode = new MethodePaiement();
                    methode.setLibelle(type);
                    methodePaiementRepository.save(methode);
                }
            }
            log.info("Méthodes de paiement initialisées avec succès");
        }
    }
    
    private void initClasseSiege() {
        if (classeSiegeRepository.count() == 0) {
            log.info("Initialisation des classes de siège...");
            
            ClasseSiege economy = new ClasseSiege();
            economy.setLibelle("ECONOMY");
            economy.setDescription("Classe économique");
            classeSiegeRepository.save(economy);
            
            ClasseSiege business = new ClasseSiege();
            business.setLibelle("BUSINESS");
            business.setDescription("Classe affaires");
            classeSiegeRepository.save(business);
            
            log.info("Classes de siège initialisées avec succès");
        }
    }
    
    private void initRoleEquipage() {
        if (roleEquipageRepository.count() == 0) {
            log.info("Initialisation des rôles d'équipage...");
            
            RoleEquipage pilote = new RoleEquipage();
            pilote.setLibelle("PILOTE");
            pilote.setDescription("Commandant de bord");
            roleEquipageRepository.save(pilote);
            
            RoleEquipage copilote = new RoleEquipage();
            copilote.setLibelle("COPILOTE");
            copilote.setDescription("Copilote");
            roleEquipageRepository.save(copilote);
            
            RoleEquipage hotesse = new RoleEquipage();
            hotesse.setLibelle("HOTESSE");
            hotesse.setDescription("Hôtesse de l'air");
            roleEquipageRepository.save(hotesse);
            
            RoleEquipage steward = new RoleEquipage();
            steward.setLibelle("STEWARD");
            steward.setDescription("Steward");
            roleEquipageRepository.save(steward);
            
            log.info("Rôles d'équipage initialisés avec succès");
        }
    }
    
    private void initEtatAvion() {
        if (etatAvionRepository.count() == 0) {
            log.info("Initialisation des états d'avion...");
            
            EtatAvion operationnel = new EtatAvion();
            operationnel.setLibelle("OPERATIONNEL");
            operationnel.setDescription("Avion opérationnel et prêt à voler");
            etatAvionRepository.save(operationnel);
            
            EtatAvion maintenance = new EtatAvion();
            maintenance.setLibelle("EN_MAINTENANCE");
            maintenance.setDescription("Avion en cours de maintenance");
            etatAvionRepository.save(maintenance);
            
            EtatAvion immobilise = new EtatAvion();
            immobilise.setLibelle("IMMOBILISE");
            immobilise.setDescription("Avion immobilisé");
            etatAvionRepository.save(immobilise);
            
            EtatAvion decommissionne = new EtatAvion();
            decommissionne.setLibelle("DECOMMISSIONNE");
            decommissionne.setDescription("Avion retiré du service");
            etatAvionRepository.save(decommissionne);
            
            log.info("États d'avion initialisés avec succès");
        }
    }
}
