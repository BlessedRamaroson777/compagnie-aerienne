package com.airline.airline_app.specification;

import com.airline.airline_app.entity.PrixVol;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public class PrixVolSpecification {
    
    public static Specification<PrixVol> filterBy(Long volId, Long classeSiegeId, BigDecimal prixMin, BigDecimal prixMax) {
        return Specification
            .where(hasVolId(volId))
            .and(hasClasseSiegeId(classeSiegeId))
            .and(hasPrixMin(prixMin))
            .and(hasPrixMax(prixMax));
    }
    
    private static Specification<PrixVol> hasVolId(Long volId) {
        return (root, query, cb) -> {
            if (volId == null) {
                return cb.conjunction();
            }
            return cb.equal(root.get("vol").get("id"), volId);
        };
    }
    
    private static Specification<PrixVol> hasClasseSiegeId(Long classeSiegeId) {
        return (root, query, cb) -> {
            if (classeSiegeId == null) {
                return cb.conjunction();
            }
            return cb.equal(root.get("classeSiege").get("id"), classeSiegeId);
        };
    }
    
    private static Specification<PrixVol> hasPrixMin(BigDecimal prixMin) {
        return (root, query, cb) -> {
            if (prixMin == null) {
                return cb.conjunction();
            }
            return cb.greaterThanOrEqualTo(root.get("prix"), prixMin);
        };
    }
    
    private static Specification<PrixVol> hasPrixMax(BigDecimal prixMax) {
        return (root, query, cb) -> {
            if (prixMax == null) {
                return cb.conjunction();
            }
            return cb.lessThanOrEqualTo(root.get("prix"), prixMax);
        };
    }
}
