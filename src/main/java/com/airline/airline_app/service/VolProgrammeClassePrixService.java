package com.airline.airline_app.service;

import com.airline.airline_app.entity.ClasseSiege;
import com.airline.airline_app.entity.VolProgramme;
import com.airline.airline_app.entity.VolProgrammeClassePrix;
import com.airline.airline_app.repository.VolProgrammeClassePrixRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class VolProgrammeClassePrixService {

    @Autowired
    private VolProgrammeClassePrixRepository repository;

    public List<VolProgrammeClassePrix> getAllPrix() {
        return repository.findAll();
    }

    public Optional<VolProgrammeClassePrix> getPrixById(Long id) {
        return repository.findById(id);
    }

    public List<VolProgrammeClassePrix> getPrixByVolProgramme(Long volProgrammeId) {
        return repository.findByVolProgrammeId(volProgrammeId);
    }

    public Optional<VolProgrammeClassePrix> getPrixByVolProgrammeAndClasse(Long volProgrammeId, Long classeSiegeId) {
        return repository.findByVolProgrammeIdAndClasseSiegeId(volProgrammeId, classeSiegeId);
    }

    @Transactional
    public VolProgrammeClassePrix savePrix(VolProgrammeClassePrix prix) {
        return repository.save(prix);
    }

    @Transactional
    public VolProgrammeClassePrix createOrUpdatePrix(VolProgramme volProgramme, ClasseSiege classeSiege, BigDecimal prix) {
        Optional<VolProgrammeClassePrix> existing = repository.findByVolProgrammeIdAndClasseSiegeId(
            volProgramme.getId(), classeSiege.getId());
        
        VolProgrammeClassePrix prixEntity;
        if (existing.isPresent()) {
            prixEntity = existing.get();
            prixEntity.setPrix(prix);
        } else {
            prixEntity = new VolProgrammeClassePrix();
            prixEntity.setVolProgramme(volProgramme);
            prixEntity.setClasseSiege(classeSiege);
            prixEntity.setPrix(prix);
        }
        
        return repository.save(prixEntity);
    }

    @Transactional
    public void deletePrix(Long id) {
        repository.deleteById(id);
    }

    @Transactional
    public void deletePrixByVolProgramme(Long volProgrammeId) {
        repository.deleteByVolProgrammeId(volProgrammeId);
    }

    public boolean existsPrix(Long volProgrammeId, Long classeSiegeId) {
        return repository.existsByVolProgrammeIdAndClasseSiegeId(volProgrammeId, classeSiegeId);
    }
}
