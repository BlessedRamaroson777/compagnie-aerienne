package com.airline.airline_app.service;

import com.airline.airline_app.entity.Societe;
import com.airline.airline_app.repository.SocieteRepository;
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
public class SocieteService {

    private final SocieteRepository societeRepository;

    public List<Societe> findAll() {
        return societeRepository.findAll();
    }

    public Page<Societe> findAll(Specification<Societe> spec, Pageable pageable) {
        return societeRepository.findAll(spec, pageable);
    }

    public Optional<Societe> findById(Long id) {
        return societeRepository.findById(id);
    }

    public Societe save(Societe societe) {
        return societeRepository.save(societe);
    }

    public void deleteById(Long id) {
        societeRepository.deleteById(id);
    }

    public Optional<Societe> findByLibelle(String libelle) {
        return societeRepository.findByLibelle(libelle);
    }
}
