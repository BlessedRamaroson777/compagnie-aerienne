package com.airline.airline_app.listener;

import com.airline.airline_app.entity.ConfigurationCoutDiffusion;
import com.airline.airline_app.entity.HistoriqueConfigurationCout;
import com.airline.airline_app.entity.enums.StatusHistorique;
import com.airline.airline_app.service.HistoriqueConfigurationCoutService;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Listener JPA pour enregistrer automatiquement l'historique
 * quand une configuration de coût de diffusion est créée ou modifiée
 */
@Component
public class ConfigurationCoutDiffusionListener {
    
    private static ApplicationContext applicationContext;
    
    @Autowired
    public void setApplicationContext(ApplicationContext context) {
        applicationContext = context;
    }
    
    /**
     * Appelé après la création d'une ConfigurationCoutDiffusion
     */
    @PostPersist
    public void afterCreate(ConfigurationCoutDiffusion config) {
        enregistrerHistorique(config, StatusHistorique.CREATION);
    }
    
    /**
     * Appelé après la mise à jour d'une ConfigurationCoutDiffusion
     */
    @PostUpdate
    public void afterUpdate(ConfigurationCoutDiffusion config) {
        enregistrerHistorique(config, StatusHistorique.MODIFICATION);
    }
    
    /**
     * Enregistre l'historique de la configuration
     */
    private void enregistrerHistorique(ConfigurationCoutDiffusion config, StatusHistorique status) {
        if (applicationContext != null && config != null) {
            HistoriqueConfigurationCoutService service = 
                applicationContext.getBean(HistoriqueConfigurationCoutService.class);
            
            HistoriqueConfigurationCout historique = new HistoriqueConfigurationCout();
            historique.setConfigurationCoutDiffusion(config);
            historique.setPrix(config.getPrix());
            historique.setStatus(status);
            historique.setDate(LocalDateTime.now());
            
            service.save(historique);
        }
    }
}
