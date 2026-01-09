package com.airline.airline_app.service;

import com.airline.airline_app.entity.ClasseSiege;
import com.airline.airline_app.repository.ClasseSiegeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClasseSiegeService extends GenericServiceImpl<ClasseSiege, Long> {
    
    private final ClasseSiegeRepository classeSiegeRepository;
    
    @Override
    protected JpaRepository<ClasseSiege, Long> getRepository() {
        return classeSiegeRepository;
    }
    
    @Override
    protected JpaSpecificationExecutor<ClasseSiege> getSpecificationExecutor() {
        return classeSiegeRepository;
    }
    
    public List<ClasseSiege> findAll(Specification<ClasseSiege> specification) {
        return classeSiegeRepository.findAll(specification);
    }
}
