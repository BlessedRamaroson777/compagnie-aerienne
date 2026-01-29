package com.airline.airline_app.service;

import com.airline.airline_app.entity.Produit;
import com.airline.airline_app.entity.PrixProduit;
import com.airline.airline_app.repository.ProduitRepository;
import com.airline.airline_app.repository.PrixProduitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProduitService {

    private final ProduitRepository produitRepository;
    private final PrixProduitRepository prixProduitRepository;

    public List<Produit> findAll() {
        return produitRepository.findAll();
    }

    public Produit findById(Long id) {
        return produitRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produit non trouvé avec l'ID: " + id));
    }

    @Transactional
    public Produit save(Produit produit) {
        return produitRepository.save(produit);
    }

    @Transactional
    public Produit update(Long id, Produit produitDetails) {
        Produit produit = findById(id);
        
        BigDecimal ancienPrix = produit.getPrix();
        
        produit.setLibelle(produitDetails.getLibelle());
        produit.setStock(produitDetails.getStock());
        produit.setPrix(produitDetails.getPrix());
        
        Produit produitUpdated = produitRepository.save(produit);
        
        // Si le prix a changé, insérer dans prix_produit (le trigger le fera aussi, mais on peut le faire manuellement)
        if (!ancienPrix.equals(produitDetails.getPrix())) {
            PrixProduit prixProduit = new PrixProduit();
            prixProduit.setProduit(produitUpdated);
            prixProduit.setPrix(produitDetails.getPrix());
            prixProduitRepository.save(prixProduit);
        }
        
        return produitUpdated;
    }

    @Transactional
    public void delete(Long id) {
        Produit produit = findById(id);
        produitRepository.delete(produit);
    }

    public List<Produit> searchByLibelle(String libelle) {
        return produitRepository.findByLibelleContainingIgnoreCase(libelle);
    }

    public List<Produit> findProduitsFaibleStock(Integer seuil) {
        return produitRepository.findProduitsFaibleStock(seuil);
    }

    public List<PrixProduit> getHistoriquePrix(Long idProduit) {
        return prixProduitRepository.findByProduitIdProduitOrderByDateModificationDesc(idProduit);
    }
}
