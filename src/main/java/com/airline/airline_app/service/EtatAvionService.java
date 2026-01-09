package com.airline.airline_app.service;

import com.airline.airline_app.entity.EtatAvion;
import com.airline.airline_app.repository.EtatAvionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EtatAvionService extends GenericServiceImpl<EtatAvion, Long> {
    
    private final EtatAvionRepository etatAvionRepository;
    
    @Override
    protected JpaRepository<EtatAvion, Long> getRepository() {
        return etatAvionRepository;
    }
    
    @Override
    protected JpaSpecificationExecutor<EtatAvion> getSpecificationExecutor() {
        return etatAvionRepository;
    }
    
    public List<EtatAvion> findAll(Specification<EtatAvion> specification) {
        return etatAvionRepository.findAll(specification);
    }
}
