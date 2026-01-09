package com.airline.airline_app.specification;

import com.airline.airline_app.entity.Reservation;
import com.airline.airline_app.entity.enums.StatutReservation;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ReservationSpecification {
    
    public static Specification<Reservation> filterBy(Long clientId, Long volId, 
                                                     StatutReservation statut,
                                                     LocalDateTime dateMin, LocalDateTime dateMax) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            if (clientId != null) {
                predicates.add(criteriaBuilder.equal(root.get("client").get("id"), clientId));
            }
            
            if (volId != null) {
                predicates.add(criteriaBuilder.equal(root.get("vol").get("id"), volId));
            }
            
            if (statut != null) {
                predicates.add(criteriaBuilder.equal(root.get("statut"), statut));
            }
            
            if (dateMin != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("dateReservation"), dateMin));
            }
            
            if (dateMax != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("dateReservation"), dateMax));
            }
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
