package com.airline.airline_app.specification;

import com.airline.airline_app.entity.ModelAvion;
import org.springframework.data.jpa.domain.Specification;

public class ModelAvionSpecification {
    
    public static Specification<ModelAvion> filterBy(String designation, String fabricant, Integer capaciteMin, Integer capaciteMax) {
        return Specification
            .where(hasDesignation(designation))
            .and(hasFabricant(fabricant))
            .and(hasCapaciteMin(capaciteMin))
            .and(hasCapaciteMax(capaciteMax));
    }
    
    private static Specification<ModelAvion> hasDesignation(String designation) {
        return (root, query, cb) -> {
            if (designation == null || designation.isBlank()) {
                return cb.conjunction();
            }
            return cb.like(cb.lower(root.get("designation")), "%" + designation.toLowerCase() + "%");
        };
    }
    
    private static Specification<ModelAvion> hasFabricant(String fabricant) {
        return (root, query, cb) -> {
            if (fabricant == null || fabricant.isBlank()) {
                return cb.conjunction();
            }
            return cb.like(cb.lower(root.get("fabricant")), "%" + fabricant.toLowerCase() + "%");
        };
    }
    
    private static Specification<ModelAvion> hasCapaciteMin(Integer capaciteMin) {
        return (root, query, cb) -> {
            if (capaciteMin == null) {
                return cb.conjunction();
            }
            return cb.greaterThanOrEqualTo(root.get("capacite"), capaciteMin);
        };
    }
    
    private static Specification<ModelAvion> hasCapaciteMax(Integer capaciteMax) {
        return (root, query, cb) -> {
            if (capaciteMax == null) {
                return cb.conjunction();
            }
            return cb.lessThanOrEqualTo(root.get("capacite"), capaciteMax);
        };
    }
}
