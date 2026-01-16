package com.airline.airline_app.listener;

import com.airline.airline_app.entity.PrixVol;
import com.airline.airline_app.service.ConfigurationRemisePersonnaliseeService;
import jakarta.persistence.PostUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * Listener JPA pour synchroniser automatiquement les remises
 * quand le prix d'un vol change
 */
@Component
public class PrixVolListener {
    
    private static ApplicationContext applicationContext;
    
    @Autowired
    public void setApplicationContext(ApplicationContext context) {
        applicationContext = context;
    }
    
    /**
     * Appelé après la mise à jour d'un PrixVol
     * Synchronise automatiquement les remises dans remise_categorie_place
     */
    @PostUpdate
    public void afterUpdate(PrixVol prixVol) {
        if (applicationContext != null && prixVol != null) {
            ConfigurationRemisePersonnaliseeService service = 
                applicationContext.getBean(ConfigurationRemisePersonnaliseeService.class);
            
            if (prixVol.getVolProgramme() != null && prixVol.getClasseSiege() != null) {
                service.synchroniserRemisesApresChangementPrix(
                    prixVol.getVolProgramme().getId(),
                    prixVol.getClasseSiege().getId(),
                    prixVol.getPrix()
                );
            }
        }
    }
}
