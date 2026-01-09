package com.airline.airline_app.service;

import com.airline.airline_app.entity.Avion;
import com.airline.airline_app.entity.Siege;
import com.airline.airline_app.entity.enums.ClasseSiege;
import com.airline.airline_app.repository.AvionRepository;
import com.airline.airline_app.repository.SiegeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AvionService extends GenericServiceImpl<Avion, Long> {
    
    private final AvionRepository avionRepository;
    private final SiegeRepository siegeRepository;
    
    @Override
    protected JpaRepository<Avion, Long> getRepository() {
        return avionRepository;
    }
    
    @Override
    protected JpaSpecificationExecutor<Avion> getSpecificationExecutor() {
        return avionRepository;
    }
    
    @Override
    @Transactional
    public Avion save(Avion avion) {
        Avion savedAvion = super.save(avion);
        genererSieges(savedAvion);
        return savedAvion;
    }
    
    /**
     * Génère automatiquement les sièges pour un avion
     */
    private void genererSieges(Avion avion) {
        List<Siege> sieges = new ArrayList<>();
        int capacite = avion.getCapaciteTotale();
        int siegesEconomy = (int) (capacite * 0.7); // 70% Economy
        int siegesBusiness = capacite - siegesEconomy; // 30% Business
        
        // Génération des sièges Business
        for (int i = 1; i <= siegesBusiness; i++) {
            Siege siege = new Siege();
            siege.setNumeroSiege(i + "A");
            siege.setClasseSiege(ClasseSiege.BUSINESS);
            siege.setAvion(avion);
            sieges.add(siege);
        }
        
        // Génération des sièges Economy
        for (int i = 1; i <= siegesEconomy; i++) {
            Siege siege = new Siege();
            siege.setNumeroSiege((siegesBusiness + i) + "A");
            siege.setClasseSiege(ClasseSiege.ECONOMY);
            siege.setAvion(avion);
            sieges.add(siege);
        }
        
        siegeRepository.saveAll(sieges);
    }
    
    public List<Avion> findAll(Specification<Avion> specification) {
        return avionRepository.findAll(specification);
    }
}
