package com.airline.airline_app.service;

import com.airline.airline_app.entity.Billet;
import com.airline.airline_app.repository.BilletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BilletService extends GenericServiceImpl<Billet, Long> {
    
    private final BilletRepository billetRepository;
    
    @Override
    protected JpaRepository<Billet, Long> getRepository() {
        return billetRepository;
    }
    
    @Override
    protected JpaSpecificationExecutor<Billet> getSpecificationExecutor() {
        return billetRepository;
    }
    
    public List<Billet> findAll(Specification<Billet> specification) {
        return billetRepository.findAll(specification);
    }
}
