package com.airline.airline_app.service;

import com.airline.airline_app.entity.Equipage;
import com.airline.airline_app.repository.EquipageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EquipageService extends GenericServiceImpl<Equipage, Long> {
    
    private final EquipageRepository equipageRepository;
    
    @Override
    protected JpaRepository<Equipage, Long> getRepository() {
        return equipageRepository;
    }
    
    @Override
    protected JpaSpecificationExecutor<Equipage> getSpecificationExecutor() {
        return equipageRepository;
    }
    
    public List<Equipage> findAll(Specification<Equipage> specification) {
        return equipageRepository.findAll(specification);
    }
}
