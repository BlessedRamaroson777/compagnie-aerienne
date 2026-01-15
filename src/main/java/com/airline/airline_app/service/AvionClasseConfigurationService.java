package com.airline.airline_app.service;

import com.airline.airline_app.entity.AvionClasseConfiguration;
import com.airline.airline_app.repository.AvionClasseConfigurationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AvionClasseConfigurationService {
    
    private final AvionClasseConfigurationRepository repository;
    
    public List<AvionClasseConfiguration> findAll() {
        return repository.findAll();
    }
    
    public Optional<AvionClasseConfiguration> findById(Long id) {
        return repository.findById(id);
    }
    
    public List<AvionClasseConfiguration> findByAvionId(Long avionId) {
        return repository.findByAvionId(avionId);
    }
    
    public Optional<AvionClasseConfiguration> findByAvionIdAndClasseSiegeId(Long avionId, Long classeSiegeId) {
        return repository.findByAvionIdAndClasseSiegeId(avionId, classeSiegeId);
    }
    
    public Integer getTotalCapaciteByAvionId(Long avionId) {
        Integer total = repository.getTotalCapaciteByAvionId(avionId);
        return total != null ? total : 0;
    }
    
    @Transactional
    public AvionClasseConfiguration save(AvionClasseConfiguration configuration) {
        return repository.save(configuration);
    }
    
    @Transactional
    public void deleteById(Long id) {
        repository.deleteById(id);
    }
    
    @Transactional
    public void deleteByAvionId(Long avionId) {
        repository.deleteByAvionId(avionId);
    }
}
