package com.airline.airline_app.service;

import com.airline.airline_app.entity.DiffusionVolProgramme;
import com.airline.airline_app.repository.DiffusionVolProgrammeRepository;
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
public class DiffusionVolProgrammeService {

    private final DiffusionVolProgrammeRepository diffusionVolProgrammeRepository;

    public List<DiffusionVolProgramme> findAll() {
        return diffusionVolProgrammeRepository.findAll();
    }

    public Page<DiffusionVolProgramme> findAll(Specification<DiffusionVolProgramme> spec, Pageable pageable) {
        return diffusionVolProgrammeRepository.findAll(spec, pageable);
    }

    public Optional<DiffusionVolProgramme> findById(Long id) {
        return diffusionVolProgrammeRepository.findById(id);
    }

    public DiffusionVolProgramme save(DiffusionVolProgramme diffusionVolProgramme) {
        return diffusionVolProgrammeRepository.save(diffusionVolProgramme);
    }

    public void deleteById(Long id) {
        diffusionVolProgrammeRepository.deleteById(id);
    }

    public List<DiffusionVolProgramme> findByDiffusionSocieteId(Long idDiffusionSociete) {
        return diffusionVolProgrammeRepository.findByDiffusionSocieteIdDiffusionSociete(idDiffusionSociete);
    }

    public List<DiffusionVolProgramme> findByVolProgrammeId(Long idVolProgramme) {
        return diffusionVolProgrammeRepository.findByVolProgramme_Id(idVolProgramme);
    }
}
