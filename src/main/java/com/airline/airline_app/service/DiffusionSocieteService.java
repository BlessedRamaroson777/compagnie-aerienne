package com.airline.airline_app.service;

import com.airline.airline_app.entity.DiffusionSociete;
import com.airline.airline_app.repository.DiffusionSocieteRepository;
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
public class DiffusionSocieteService {

    private final DiffusionSocieteRepository diffusionSocieteRepository;

    public List<DiffusionSociete> findAll() {
        return diffusionSocieteRepository.findAll();
    }

    public Page<DiffusionSociete> findAll(Specification<DiffusionSociete> spec, Pageable pageable) {
        return diffusionSocieteRepository.findAll(spec, pageable);
    }

    public Optional<DiffusionSociete> findById(Long id) {
        return diffusionSocieteRepository.findById(id);
    }

    public DiffusionSociete save(DiffusionSociete diffusionSociete) {
        return diffusionSocieteRepository.save(diffusionSociete);
    }

    public void deleteById(Long id) {
        diffusionSocieteRepository.deleteById(id);
    }

    public List<DiffusionSociete> findBySocieteId(Long idSociete) {
        return diffusionSocieteRepository.findBySocieteIdSociete(idSociete);
    }
}
