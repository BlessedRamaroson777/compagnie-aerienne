package com.airline.airline_app.specification;

import com.airline.airline_app.entity.Siege;
import com.airline.airline_app.entity.ClasseSiege;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class SiegeSpecification {
    
    public static Specification<Siege> filterBy(String numeroSiege, ClasseSiege classeSiege, Long avionId) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            if (numeroSiege != null && !numeroSiege.isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("numeroSiege")), 
                    "%" + numeroSiege.toLowerCase() + "%"));
            }
            
            if (classeSiege != null) {
                predicates.add(criteriaBuilder.equal(root.get("classeSiege"), classeSiege));
            }
            
            if (avionId != null) {
                predicates.add(criteriaBuilder.equal(root.get("avion").get("id"), avionId));
            }
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
