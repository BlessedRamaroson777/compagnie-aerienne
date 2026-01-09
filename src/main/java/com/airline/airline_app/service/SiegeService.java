package com.airline.airline_app.service;

import com.airline.airline_app.entity.Siege;
import com.airline.airline_app.repository.SiegeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SiegeService extends GenericServiceImpl<Siege, Long> {
    
    private final SiegeRepository siegeRepository;
    
    @Override
    protected JpaRepository<Siege, Long> getRepository() {
        return siegeRepository;
    }
    
    @Override
    protected JpaSpecificationExecutor<Siege> getSpecificationExecutor() {
        return siegeRepository;
    }
    
    public List<Siege> findAll(Specification<Siege> specification) {
        return siegeRepository.findAll(specification);
    }
}
