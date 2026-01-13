package com.airline.airline_app.service;

import com.airline.airline_app.entity.Siege;
import com.airline.airline_app.entity.SiegeVol;
import com.airline.airline_app.entity.Vol;
import com.airline.airline_app.repository.SiegeRepository;
import com.airline.airline_app.repository.SiegeVolRepository;
import com.airline.airline_app.repository.VolRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VolService extends GenericServiceImpl<Vol, Long> {
    
    private final VolRepository volRepository;
    private final SiegeRepository siegeRepository;
    private final SiegeVolRepository siegeVolRepository;
    
    @Override
    protected JpaRepository<Vol, Long> getRepository() {
        return volRepository;
    }
    
    @Override
    protected JpaSpecificationExecutor<Vol> getSpecificationExecutor() {
        return volRepository;
    }
    
    public List<Vol> findAll(Specification<Vol> specification) {
        return volRepository.findAll(specification);
    }
}
