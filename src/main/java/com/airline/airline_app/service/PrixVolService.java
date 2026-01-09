package com.airline.airline_app.service;

import com.airline.airline_app.entity.PrixVol;
import com.airline.airline_app.repository.PrixVolRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PrixVolService extends GenericServiceImpl<PrixVol, Long> {
    
    private final PrixVolRepository prixVolRepository;
    
    @Override
    protected JpaRepository<PrixVol, Long> getRepository() {
        return prixVolRepository;
    }
    
    @Override
    protected JpaSpecificationExecutor<PrixVol> getSpecificationExecutor() {
        return prixVolRepository;
    }
    
    public List<PrixVol> findAll(Specification<PrixVol> specification) {
        return prixVolRepository.findAll(specification);
    }
    
    public List<PrixVol> findByVolId(Long volId) {
        return prixVolRepository.findByVolId(volId);
    }
}
