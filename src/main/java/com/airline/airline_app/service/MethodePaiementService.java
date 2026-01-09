package com.airline.airline_app.service;

import com.airline.airline_app.entity.MethodePaiement;
import com.airline.airline_app.repository.MethodePaiementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MethodePaiementService extends GenericServiceImpl<MethodePaiement, Long> {
    
    private final MethodePaiementRepository methodePaiementRepository;
    
    @Override
    protected JpaRepository<MethodePaiement, Long> getRepository() {
        return methodePaiementRepository;
    }
    
    @Override
    protected JpaSpecificationExecutor<MethodePaiement> getSpecificationExecutor() {
        return methodePaiementRepository;
    }
}
