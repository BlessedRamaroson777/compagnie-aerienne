package com.airline.airline_app.specification;

import com.airline.airline_app.entity.Equipage;
import com.airline.airline_app.entity.enums.RoleEquipage;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class EquipageSpecification {
    
    public static Specification<Equipage> filterBy(String nom, String prenom, RoleEquipage role) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            if (nom != null && !nom.isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("nom")), 
                    "%" + nom.toLowerCase() + "%"));
            }
            
            if (prenom != null && !prenom.isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("prenom")), 
                    "%" + prenom.toLowerCase() + "%"));
            }
            
            if (role != null) {
                predicates.add(criteriaBuilder.equal(root.get("role"), role));
            }
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
