package com.airline.airline_app.service;

import com.airline.airline_app.entity.ProduitCategorieSiegePassager;
import com.airline.airline_app.repository.ProduitCategorieSiegePassagerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProduitCategorieSiegePassagerService {

    private final ProduitCategorieSiegePassagerRepository repository;

    public List<ProduitCategorieSiegePassager> findAll() {
        return repository.findAll();
    }

    public ProduitCategorieSiegePassager findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Association non trouvée avec l'ID: " + id));
    }

    @Transactional
    public ProduitCategorieSiegePassager save(ProduitCategorieSiegePassager association) {
        return repository.save(association);
    }

    @Transactional
    public void delete(Long id) {
        repository.deleteById(id);
    }

    public List<ProduitCategorieSiegePassager> findByProduit(Long idProduit) {
        return repository.findByProduitIdProduit(idProduit);
    }

    public List<ProduitCategorieSiegePassager> findByCategorie(Long idCategoriePassager) {
        return repository.findByCategoriePassagerIdCategoriePassager(idCategoriePassager);
    }

    public List<ProduitCategorieSiegePassager> findByClasse(Long idClasseSiege) {
        return repository.findByClasseSiegeId(idClasseSiege);
    }
}
