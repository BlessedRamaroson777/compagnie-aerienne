package com.airline.airline_app.repository;

import com.airline.airline_app.entity.MethodePaiement;
import com.airline.airline_app.entity.enums.TypeMethodePaiement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MethodePaiementRepository extends JpaRepository<MethodePaiement, Long>, JpaSpecificationExecutor<MethodePaiement> {
    Optional<MethodePaiement> findByLibelle(TypeMethodePaiement libelle);
}
