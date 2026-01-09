package com.airline.airline_app.specification;

import com.airline.airline_app.entity.EtatAvion;
import org.springframework.data.jpa.domain.Specification;

public class EtatAvionSpecification {
    
    public static Specification<EtatAvion> filterBy(String libelle) {
        return Specification.where(hasLibelle(libelle));
    }
    
    private static Specification<EtatAvion> hasLibelle(String libelle) {
        return (root, query, cb) -> {
            if (libelle == null || libelle.isBlank()) {
                return cb.conjunction();
            }
            return cb.like(cb.lower(root.get("libelle")), "%" + libelle.toLowerCase() + "%");
        };
    }
}
