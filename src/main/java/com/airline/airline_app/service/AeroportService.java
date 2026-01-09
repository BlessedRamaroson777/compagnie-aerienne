package com.airline.airline_app.service;

import com.airline.airline_app.entity.Aeroport;
import com.airline.airline_app.repository.AeroportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AeroportService extends GenericServiceImpl<Aeroport, Long> {
    
    private final AeroportRepository aeroportRepository;
    
    @Override
    protected JpaRepository<Aeroport, Long> getRepository() {
        return aeroportRepository;
    }
    
    @Override
    protected JpaSpecificationExecutor<Aeroport> getSpecificationExecutor() {
        return aeroportRepository;
    }
    
    public List<Aeroport> findAll(Specification<Aeroport> specification) {
        return aeroportRepository.findAll(specification);
    }
}
