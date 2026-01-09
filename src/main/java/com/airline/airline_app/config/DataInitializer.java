package com.airline.airline_app.config;

import com.airline.airline_app.entity.MethodePaiement;
import com.airline.airline_app.entity.enums.TypeMethodePaiement;
import com.airline.airline_app.repository.MethodePaiementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    
    private final MethodePaiementRepository methodePaiementRepository;
    
    @Override
    public void run(String... args) {
        // Initialiser les méthodes de paiement si elles n'existent pas
        if (methodePaiementRepository.count() == 0) {
            for (TypeMethodePaiement type : TypeMethodePaiement.values()) {
                if (methodePaiementRepository.findByLibelle(type).isEmpty()) {
                    MethodePaiement methode = new MethodePaiement();
                    methode.setLibelle(type);
                    methodePaiementRepository.save(methode);
                }
            }
            System.out.println("✓ Méthodes de paiement initialisées");
        }
    }
}
