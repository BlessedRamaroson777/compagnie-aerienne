package com.airline.airline_app.service;

import com.airline.airline_app.entity.HistoriqueConfigurationCout;
import com.airline.airline_app.entity.enums.StatusHistorique;
import com.airline.airline_app.repository.HistoriqueConfigurationCoutRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class HistoriqueConfigurationCoutService {

    private final HistoriqueConfigurationCoutRepository historiqueConfigurationCoutRepository;

    public List<HistoriqueConfigurationCout> findAll() {
        return historiqueConfigurationCoutRepository.findAll();
    }

    public Page<HistoriqueConfigurationCout> findAll(Specification<HistoriqueConfigurationCout> spec, Pageable pageable) {
        return historiqueConfigurationCoutRepository.findAll(spec, pageable);
    }

    public Optional<HistoriqueConfigurationCout> findById(Long id) {
        return historiqueConfigurationCoutRepository.findById(id);
    }

    public HistoriqueConfigurationCout save(HistoriqueConfigurationCout historiqueConfigurationCout) {
        return historiqueConfigurationCoutRepository.save(historiqueConfigurationCout);
    }

    public void deleteById(Long id) {
        historiqueConfigurationCoutRepository.deleteById(id);
    }

    public List<HistoriqueConfigurationCout> findByStatus(StatusHistorique status) {
        return historiqueConfigurationCoutRepository.findByStatus(status);
    }

    public List<HistoriqueConfigurationCout> findByConfigurationId(Long idConfigurationCoutDiffusion) {
        return historiqueConfigurationCoutRepository.findByConfigurationCoutDiffusionIdConfigurationCoutDiffusion(idConfigurationCoutDiffusion);
    }
}
