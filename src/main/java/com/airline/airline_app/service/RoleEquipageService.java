package com.airline.airline_app.service;

import com.airline.airline_app.entity.RoleEquipage;
import com.airline.airline_app.repository.RoleEquipageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleEquipageService extends GenericServiceImpl<RoleEquipage, Long> {
    
    private final RoleEquipageRepository roleEquipageRepository;
    
    @Override
    protected JpaRepository<RoleEquipage, Long> getRepository() {
        return roleEquipageRepository;
    }
    
    @Override
    protected JpaSpecificationExecutor<RoleEquipage> getSpecificationExecutor() {
        return roleEquipageRepository;
    }
    
    public List<RoleEquipage> findAll(Specification<RoleEquipage> specification) {
        return roleEquipageRepository.findAll(specification);
    }
}
