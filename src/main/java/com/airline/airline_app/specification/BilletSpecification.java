package com.airline.airline_app.specification;

import com.airline.airline_app.entity.Billet;
import com.airline.airline_app.entity.enums.StatutBillet;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class BilletSpecification {
    
    public static Specification<Billet> filterBy(StatutBillet statut, Long volId, Long clientId) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            if (statut != null) {
                predicates.add(criteriaBuilder.equal(root.get("statut"), statut));
            }
            
            if (volId != null) {
                predicates.add(criteriaBuilder.equal(root.get("reservation").get("vol").get("id"), volId));
            }
            
            if (clientId != null) {
                predicates.add(criteriaBuilder.equal(root.get("reservation").get("client").get("id"), clientId));
            }
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
