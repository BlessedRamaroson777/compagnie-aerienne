package com.airline.airline_app.specification;

import com.airline.airline_app.entity.Avion;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class AvionSpecification {
    
    public static Specification<Avion> filterBy(String codeAvion, String modele, Integer capaciteMin, Integer capaciteMax) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            if (codeAvion != null && !codeAvion.isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("codeAvion")), 
                    "%" + codeAvion.toLowerCase() + "%"));
            }
            
            if (modele != null && !modele.isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("modele")), 
                    "%" + modele.toLowerCase() + "%"));
            }
            
            if (capaciteMin != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("capaciteTotale"), capaciteMin));
            }
            
            if (capaciteMax != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("capaciteTotale"), capaciteMax));
            }
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
