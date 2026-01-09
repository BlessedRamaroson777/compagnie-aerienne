package com.airline.airline_app.specification;

import com.airline.airline_app.entity.Vol;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class VolSpecification {
    
    public static Specification<Vol> filterBy(String numeroVol, String depart, String arrivee, 
                                              LocalDateTime dateMin, LocalDateTime dateMax,
                                              BigDecimal prixMin, BigDecimal prixMax,
                                              Long avionId) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            if (numeroVol != null && !numeroVol.isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("numeroVol")), 
                    "%" + numeroVol.toLowerCase() + "%"));
            }
            
            if (depart != null && !depart.isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("depart")), 
                    "%" + depart.toLowerCase() + "%"));
            }
            
            if (arrivee != null && !arrivee.isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("arrivee")), 
                    "%" + arrivee.toLowerCase() + "%"));
            }
            
            if (dateMin != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("dateHeureDepart"), dateMin));
            }
            
            if (dateMax != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("dateHeureDepart"), dateMax));
            }
            
            if (prixMin != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("prixBase"), prixMin));
            }
            
            if (prixMax != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("prixBase"), prixMax));
            }
            
            if (avionId != null) {
                predicates.add(criteriaBuilder.equal(root.get("avion").get("id"), avionId));
            }
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
