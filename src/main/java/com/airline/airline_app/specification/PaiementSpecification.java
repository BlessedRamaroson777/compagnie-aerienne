package com.airline.airline_app.specification;

import com.airline.airline_app.entity.Paiement;
import com.airline.airline_app.entity.enums.StatutPaiement;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PaiementSpecification {
    
    public static Specification<Paiement> filterBy(StatutPaiement statut, LocalDateTime dateMin, 
                                                   LocalDateTime dateMax, BigDecimal montantMin, 
                                                   BigDecimal montantMax) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            if (statut != null) {
                predicates.add(criteriaBuilder.equal(root.get("statut"), statut));
            }
            
            if (dateMin != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("datePaiement"), dateMin));
            }
            
            if (dateMax != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("datePaiement"), dateMax));
            }
            
            if (montantMin != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("montant"), montantMin));
            }
            
            if (montantMax != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("montant"), montantMax));
            }
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
