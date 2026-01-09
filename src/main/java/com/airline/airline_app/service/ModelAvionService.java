package com.airline.airline_app.service;

import com.airline.airline_app.entity.ModelAvion;
import com.airline.airline_app.repository.ModelAvionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ModelAvionService extends GenericServiceImpl<ModelAvion, Long> {
    
    private final ModelAvionRepository modelAvionRepository;
    
    @Override
    protected JpaRepository<ModelAvion, Long> getRepository() {
        return modelAvionRepository;
    }
    
    @Override
    protected JpaSpecificationExecutor<ModelAvion> getSpecificationExecutor() {
        return modelAvionRepository;
    }
    
    public List<ModelAvion> findAll(Specification<ModelAvion> specification) {
        return modelAvionRepository.findAll(specification);
    }
}
