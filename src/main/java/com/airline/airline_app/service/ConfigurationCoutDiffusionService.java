package com.airline.airline_app.service;

import com.airline.airline_app.entity.ConfigurationCoutDiffusion;
import com.airline.airline_app.repository.ConfigurationCoutDiffusionRepository;
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
public class ConfigurationCoutDiffusionService {

    private final ConfigurationCoutDiffusionRepository configurationCoutDiffusionRepository;

    public List<ConfigurationCoutDiffusion> findAll() {
        return configurationCoutDiffusionRepository.findAll();
    }

    public Page<ConfigurationCoutDiffusion> findAll(Specification<ConfigurationCoutDiffusion> spec, Pageable pageable) {
        return configurationCoutDiffusionRepository.findAll(spec, pageable);
    }

    public Optional<ConfigurationCoutDiffusion> findById(Long id) {
        return configurationCoutDiffusionRepository.findById(id);
    }

    public ConfigurationCoutDiffusion save(ConfigurationCoutDiffusion configurationCoutDiffusion) {
        return configurationCoutDiffusionRepository.save(configurationCoutDiffusion);
    }

    public void deleteById(Long id) {
        configurationCoutDiffusionRepository.deleteById(id);
    }

    public Optional<ConfigurationCoutDiffusion> findByIdDiffusionSociete(Long idDiffusionSociete) {
        return configurationCoutDiffusionRepository.findByDiffusionSocieteIdDiffusionSociete(idDiffusionSociete);
    }

    public List<ConfigurationCoutDiffusion> findAllByIdDiffusionSociete(Long idDiffusionSociete) {
        return configurationCoutDiffusionRepository.findAllByDiffusionSocieteIdDiffusionSociete(idDiffusionSociete);
    }
}
