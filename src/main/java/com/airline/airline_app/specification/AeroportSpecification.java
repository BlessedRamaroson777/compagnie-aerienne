package com.airline.airline_app.specification;

import com.airline.airline_app.entity.Aeroport;
import org.springframework.data.jpa.domain.Specification;

public class AeroportSpecification {
    
    public static Specification<Aeroport> filterBy(String codeAeroport, String nom, String ville, String pays) {
        return Specification
            .where(hasCodeAeroport(codeAeroport))
            .and(hasNom(nom))
            .and(hasVille(ville))
            .and(hasPays(pays));
    }
    
    private static Specification<Aeroport> hasCodeAeroport(String codeAeroport) {
        return (root, query, cb) -> {
            if (codeAeroport == null || codeAeroport.isBlank()) {
                return cb.conjunction();
            }
            return cb.like(cb.upper(root.get("codeAeroport")), "%" + codeAeroport.toUpperCase() + "%");
        };
    }
    
    private static Specification<Aeroport> hasNom(String nom) {
        return (root, query, cb) -> {
            if (nom == null || nom.isBlank()) {
                return cb.conjunction();
            }
            return cb.like(cb.lower(root.get("nom")), "%" + nom.toLowerCase() + "%");
        };
    }
    
    private static Specification<Aeroport> hasVille(String ville) {
        return (root, query, cb) -> {
            if (ville == null || ville.isBlank()) {
                return cb.conjunction();
            }
            return cb.like(cb.lower(root.get("ville")), "%" + ville.toLowerCase() + "%");
        };
    }
    
    private static Specification<Aeroport> hasPays(String pays) {
        return (root, query, cb) -> {
            if (pays == null || pays.isBlank()) {
                return cb.conjunction();
            }
            return cb.like(cb.lower(root.get("pays")), "%" + pays.toLowerCase() + "%");
        };
    }
}
